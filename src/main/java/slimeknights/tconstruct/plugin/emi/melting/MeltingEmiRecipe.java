package slimeknights.tconstruct.plugin.emi.melting;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.emi.EMIPlugin;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;

public class MeltingEmiRecipe extends AbstractMeltingEmiRecipe {

  public MeltingEmiRecipe(MeltingRecipe recipe) {
    super(recipe);
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return EMIPlugin.MELTING_CATEGORY;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    super.addWidgets(widgets);

    // input
    widgets.addSlot(getInputs().get(0), 24, 18).drawBack(false);

    // output
//    IMeltingContainer.OreRateType oreType = recipe.getOreType();
//    IRecipeSlotTooltipCallback tooltip;
//    if (oreType == IMeltingContainer.OreRateType.METAL) {
//      tooltip = METAL_ORE_TOOLTIP;
//    } else if (oreType == IMeltingContainer.OreRateType.GEM) {
//      tooltip = GEM_ORE_TOOLTIP;
//    } else {
//      tooltip = MeltingCategory.MeltingFluidCallback.INSTANCE;
//    }
    widgets.addSlot(getOutputs().get(0), 96, 4)
      .output(true)
      .drawBack(false)
      .recipeContext(this)
      .customBackground(null, 0, 0, 32, 32);

    // show fuels that are valid for this recipe
    int fuelHeight = 32;
    // solid fuel
    if (recipe.getTemperature() <= FuelModule.SOLID_TEMPERATURE) {
      fuelHeight = 15;
      EmiIngredient solidFuels = EmiIngredient.of(MeltingFuelHandler.SOLID_FUELS.get()
          .stream().map(EmiStack::of).toList());
      widgets.addSlot(solidFuels, 2, 22).drawBack(false);
    }

    // liquid fuel
    EmiIngredient liquidFuels = EmiIngredient.of(MeltingFuelHandler.getUsableFuels(recipe.getTemperature())
      .stream().map(s -> FluidEmiStack.of(s.getFluid(), s.getAmount())).toList());
    widgets.addSlot(liquidFuels, 4, 4).customBackground(null, 0, 0, 12, fuelHeight).drawBack(false);
  }
}
