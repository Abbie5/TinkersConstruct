package slimeknights.tconstruct.plugin.emi.modifiers;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.plugin.emi.EMIPlugin;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ModifierEmiRecipe implements EmiRecipe {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private final List<EmiIngredient> input = new ArrayList<>();
  private final List<EmiIngredient> tools;
  private final EmiStack output;
  private final ResourceLocation id;
  private final IDisplayModifierRecipe recipe;
  private final Map<SlotType,TextureAtlasSprite> slotTypeSprites = new HashMap<>();

  public ModifierEmiRecipe(IDisplayModifierRecipe recipe) {
    id = recipe.getModifier().getId();
    tools = Stream.of(recipe.getToolWithoutModifier(), recipe.getToolWithModifier()).map(t -> t.stream().map(EmiStack::of).toList()).map(EmiIngredient::of).toList();
    IntStream.range(0, 5).forEachOrdered(i -> input.add(EmiIngredient.of(recipe.getDisplayItems(i).stream().map(EmiStack::of).toList())));
    output = new ModifierEmiStack(recipe.getDisplayResult());
    this.recipe = recipe;
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return EMIPlugin.MODIFIER_CATEGORY;
  }

  @Override
  public @Nullable ResourceLocation getId() {
    return id;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return input;
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(output);
  }

  @Override
  public List<EmiIngredient> getCatalysts() {
    return tools;
  }

  @Override
  public int getDisplayWidth() {
    return 128;
  }

  @Override
  public int getDisplayHeight() {
    return 77;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND_LOC, 0, 0, 128, 77, 0, 0);

    // slot outlines
    drawOutline(widgets, 0,  2, 32);
    drawOutline(widgets, 1, 24, 14);
    drawOutline(widgets, 2, 46, 32);
    drawOutline(widgets, 3, 42, 57);
    drawOutline(widgets, 4,  6, 57);

    // info icons
    if (recipe.hasRequirements()) {
      widgets.addTexture(BACKGROUND_LOC, 66, 58, 16, 16, 128, 17)
        .tooltip((checkX, checkY) -> {
          if (GuiUtil.isHovered(checkX, checkY, 66, 58, 16, 16))
            return List.of(ClientTooltipComponent.create(new TranslatableComponent(recipe.getRequirementsError()).getVisualOrderText()));
          return null;
        });
    }
    if (recipe.isIncremental()) {
      widgets.addTexture(BACKGROUND_LOC, 83, 59, 16, 16, 128, 33)
        .tooltip((checkX, checkY) -> {
          if (GuiUtil.isHovered(checkX, checkY, 83, 59, 16, 16))
            return List.of(ClientTooltipComponent.create(new TranslatableComponent("jei.tconstruct.modifiers.incremental").getVisualOrderText()));
          return null;
        });
    }

    // max count
    int max = recipe.getMaxLevel();
    if (max > 0) {
      widgets.addText(new TranslatableComponent("jei.tconstruct.modifiers.max").append(String.valueOf(max)),
        66, 16, Color.GRAY.getRGB(), false);
    }


    // slot cost
    SlotType.SlotCount slots = recipe.getSlots();
    if (slots == null) {
      drawSlotType(widgets, null, 110, 58);
    } else {
      drawSlotType(widgets, slots.getType(), 110, 58);
      Component text = new TextComponent(Integer.toString(slots.getCount()));
      widgets.addText(text, 111, 63, Color.GRAY.getRGB(), false).horizontalAlign(TextWidget.Alignment.CENTER);
    }

    // inputs
    widgets.addSlot(input.get(0),  2, 32).drawBack(false);
    widgets.addSlot(input.get(1), 24, 14).drawBack(false);
    widgets.addSlot(input.get(2), 46, 32).drawBack(false);
    widgets.addSlot(input.get(3), 42, 57).drawBack(false);
    widgets.addSlot(input.get(4),  6, 57).drawBack(false);
    // modifiers
    //widgets.addSlot(output, 3, 3).recipeContext(this);
    {
      Component name = recipe.getModifier().getDisplayName(recipe.getDisplayResult().getLevel());
      widgets.addText(name, 67, 4, -1, true).horizontalAlign(TextWidget.Alignment.CENTER);
    }
    // tool
    widgets.addSlot(tools.get(0),  24, 37).drawBack(false);
    widgets.addSlot(tools.get(1), 100, 29).drawBack(false).output(true);

    widgets.addTexture(TConstruct.getResource("textures/item/slot/ability.png"), 0, 0, 16, 16, 0, 0);
    //widgets.addSlot(EmiStack.of(Items.NETHER_STAR), 0, 0);
  }

  private void drawOutline(WidgetHolder widgets, int slot, int x, int y) {
    if (input.get(slot).isEmpty()) {
      widgets.addTexture(BACKGROUND_LOC, x + 1, y + 1, 16, 16, 128 + slot * 16, 0);
    }
  }

  private void drawSlotType(WidgetHolder widgets, @javax.annotation.Nullable SlotType slotType, int x, int y) {
    Minecraft minecraft = Minecraft.getInstance();
    TextureAtlasSprite sprite;
    if (slotTypeSprites.containsKey(slotType)) {
      sprite = slotTypeSprites.get(slotType);
    } else {
      ModelManager modelManager = minecraft.getModelManager();
      // gets the model for the item, its a sepcial one that gives us texture info
      BakedModel model = minecraft.getItemRenderer().getItemModelShaper().getItemModel(TinkerModifiers.creativeSlotItem.get());
      if (model != null && model.getOverrides() instanceof NBTKeyModel.Overrides) {
        Material material = ((NBTKeyModel.Overrides)model.getOverrides()).getTexture(slotType == null ? "slotless" : slotType.getName());
        sprite = modelManager.getAtlas(material.atlasLocation()).getSprite(material.texture());
      } else {
        // failed to use the model, use missing texture
        sprite = modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(MissingTextureAtlasSprite.getLocation());
      }
      slotTypeSprites.put(slotType, sprite);
    }
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

    // i DO NOT understand why this doesnt work
    widgets.addDrawable(x, y, 16, 16, (matrices, mouseX, mouseY, delta) ->
      Screen.blit(matrices, 0, 0, 0, 16, 16, sprite));
  }

}
