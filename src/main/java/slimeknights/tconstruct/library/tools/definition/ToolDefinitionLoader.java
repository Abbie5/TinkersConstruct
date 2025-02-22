package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.fabricators_of_create.porting_lib.event.common.OnDatapackSyncCallback;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.weapon.IWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** JSON loader that loads tool definitions from JSON */
@Log4j2
public class ToolDefinitionLoader extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
  public static final String FOLDER = "tinkering/tool_definitions";
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(StatsNBT.class, StatsNBT.SERIALIZER)
    .registerTypeAdapter(MultiplierNBT.class, MultiplierNBT.SERIALIZER)
    .registerTypeAdapter(PartRequirement.class, PartRequirement.SERIALIZER)
    .registerTypeAdapter(DefinitionModifierSlots.class, DefinitionModifierSlots.SERIALIZER)
    .registerTypeAdapter(ModifierEntry.class, ModifierEntry.SERIALIZER)
    .registerTypeAdapter(ToolAction.class, ToolActionSerializer.INSTANCE)
    .registerTypeHierarchyAdapter(IAreaOfEffectIterator.class, IAreaOfEffectIterator.LOADER)
    .registerTypeHierarchyAdapter(IHarvestLogic.class, IHarvestLogic.LOADER)
    .registerTypeHierarchyAdapter(IWeaponAttack.class, IWeaponAttack.LOADER)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();
  private static final ToolDefinitionLoader INSTANCE = new ToolDefinitionLoader();

  /** Map of loaded tool definition data */
  private Map<ResourceLocation,ToolDefinitionData> dataMap = Collections.emptyMap();

  /** Tool definitions registered to be loaded */
  private final Map<ResourceLocation,ToolDefinition> definitions = new HashMap<>();

  private ToolDefinitionLoader() {
    super(GSON, FOLDER);
  }

  /** Gets the instance of the definition loader */
  public static ToolDefinitionLoader getInstance() {
    return INSTANCE;
  }

  /** Initializes the tool definition loader */
  public static void init() {
    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(INSTANCE);
    OnDatapackSyncCallback.EVENT.register(INSTANCE::onDatapackSync);
  }

  /**
   * Updates the tool data from the server.list. Should only be called client side
   * @param dataMap  Server data map
   */
  protected void updateDataFromServer(Map<ResourceLocation,ToolDefinitionData> dataMap) {
    this.dataMap = dataMap;
    for (Entry<ResourceLocation,ToolDefinition> entry : definitions.entrySet()) {
      ToolDefinitionData data = dataMap.get(entry.getKey());
      ToolDefinition definition = entry.getValue();
      // errored serverside, so resolve without error here
      if (data != null) {
        definition.setData(data);
      } else {
        definition.setDefaultData();
      }
    }
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
    long time = System.nanoTime();
    ImmutableMap.Builder<ResourceLocation, ToolDefinitionData> builder = ImmutableMap.builder();
    for (Entry<ResourceLocation,ToolDefinition> entry : definitions.entrySet()) {
      ResourceLocation key = entry.getKey();
      ToolDefinition definition = entry.getValue();
      // first, need to have a json for the given name
      JsonElement element = splashList.get(key);
      if (element == null) {
        log.error("Missing tool definition for tool {}", key);
        definition.setDefaultData();
        continue;
      }
      try {
        ToolDefinitionData data = GSON.fromJson(GsonHelper.convertToJsonObject(element, "tool_definition"), ToolDefinitionData.class);
        definition.validate(data);
        builder.put(key, data);
        definition.setData(data);
      } catch (Exception e) {
        log.error("Failed to load tool definition for tool {}", key, e);
        definition.setDefaultData();
      }
    }
    this.dataMap = builder.build();
    log.info("Loaded {} tool definitions in {} ms", this.dataMap.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Gets a list of all tool definitions registered to the loader */
  public Collection<ToolDefinition> getRegisteredToolDefinitions() {
    return definitions.values();
  }

  /** Called on datapack sync to send the tool data to all players */
  private void onDatapackSync(PlayerList playerList, @Nullable ServerPlayer player) {
    UpdateToolDefinitionDataPacket packet = new UpdateToolDefinitionDataPacket(dataMap);
    TinkerNetwork.getInstance().sendToPlayerList(player, playerList, packet);
  }

  /** Registers a tool definition with the loader */
  public void registerToolDefinition(ToolDefinition definition) {
    ResourceLocation name = definition.getId();
    if (definitions.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate tool definition " + name);
    }
    definitions.put(name, definition);
  }

  @Override
  public ResourceLocation getFabricId() {
    return TConstruct.getResource("tool_definition_loader");
  }

  /** Logic to serialize and deserialize tool actions */
  private enum ToolActionSerializer implements JsonSerializer<ToolAction>, JsonDeserializer<ToolAction> {
    INSTANCE;

    @Override
    public ToolAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return ToolAction.get(GsonHelper.convertToString(json, "action"));
    }

    @Override
    public JsonElement serialize(ToolAction src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.name());
    }
  }
}
