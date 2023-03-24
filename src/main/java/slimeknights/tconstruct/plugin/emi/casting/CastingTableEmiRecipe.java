package slimeknights.tconstruct.plugin.emi.casting;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.plugin.emi.EMIPlugin;

public class CastingTableEmiRecipe extends AbstractCastingEmiRecipe {

  public CastingTableEmiRecipe(IDisplayableCastingRecipe recipe) {
    super(recipe, new EmiTexture(BACKGROUND_LOC, 117, 0, 16, 16));
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return EMIPlugin.CASTING_TABLE_CATEGORY;
  }
}
