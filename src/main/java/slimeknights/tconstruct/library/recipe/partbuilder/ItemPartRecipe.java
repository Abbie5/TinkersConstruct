package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;

/**
 * Recipe to craft an ordinary item using the part builder
 */
public class ItemPartRecipe implements IDisplayPartBuilderRecipe {
  @Getter
  private final ResourceLocation id;
  @Getter
  private final MaterialVariant material;
  @Getter
  private final Pattern pattern;
  @Getter
  private final int cost;
  private final ItemOutput result;

  public ItemPartRecipe(ResourceLocation id, MaterialVariantId material, Pattern pattern, int cost, ItemOutput result) {
    this.id = id;
    this.material = MaterialVariant.of(material);
    this.pattern = pattern;
    this.cost = cost;
    this.result = result;
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    // first, must have a pattern
    if (inv.getPatternStack().getItem() != TinkerTables.pattern.get()) {
      return false;
    }
    // if there is a material item, it must have a valid material and be craftable
    if (!inv.getStack().isEmpty()) {
      MaterialRecipe materialRecipe = inv.getMaterial();
      return materialRecipe != null && material.matchesVariant(materialRecipe.getMaterial());
    }
    // no material item? return match in case we get one later
    return true;
  }

  @Override
  public boolean matches(IPartBuilderContainer inv, Level worldIn) {
    MaterialRecipe materialRecipe = inv.getMaterial();
    return materialRecipe != null && material.matchesVariant(materialRecipe.getMaterial())
           && inv.getStack().getCount() >= materialRecipe.getItemsUsed(cost);
  }

  @Override
  public ItemStack getResultItem() {
    return result.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.itemPartBuilderSerializer.get();
  }

  public static class Serializer extends LoggingRecipeSerializer<ItemPartRecipe> {
    @Override
    public ItemPartRecipe fromJson(ResourceLocation id, JsonObject json) {
      MaterialVariantId materialId = MaterialVariantId.fromJson(json, "material");
      Pattern pattern = new Pattern(GsonHelper.getAsString(json, "pattern"));
      int cost = GsonHelper.getAsInt(json, "cost");
      ItemOutput result = ItemOutput.fromJson(JsonHelper.getElement(json, "result"));
      return new ItemPartRecipe(id, materialId, pattern, cost, result);
    }

    @Nullable
    @Override
    protected ItemPartRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      MaterialVariantId materialId = MaterialVariantId.parse(buffer.readUtf(Short.MAX_VALUE));
      Pattern pattern = new Pattern(buffer.readUtf(Short.MAX_VALUE));
      int cost = buffer.readVarInt();
      ItemOutput result = ItemOutput.read(buffer);
      return new ItemPartRecipe(id, materialId, pattern, cost, result);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, ItemPartRecipe recipe) {
      buffer.writeUtf(recipe.material.getVariant().toString());
      buffer.writeUtf(recipe.pattern.toString());
      buffer.writeVarInt(recipe.cost);
      recipe.result.write(buffer);
    }
  }
}
