package slimeknights.tconstruct.plugin.emi.melting;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.emi.EMIPlugin;

public class FoundryEmiRecipe extends AbstractMeltingEmiRecipe {
  public FoundryEmiRecipe(MeltingRecipe recipe) {
    super(recipe);
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return EMIPlugin.FOUNDRY_CATEGORY;
  }
}
