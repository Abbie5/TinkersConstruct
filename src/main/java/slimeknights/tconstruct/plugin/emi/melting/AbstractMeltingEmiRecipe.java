package slimeknights.tconstruct.plugin.emi.melting;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;

import java.util.List;

public abstract class AbstractMeltingEmiRecipe implements EmiRecipe {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");

  protected final MeltingRecipe recipe;

  public AbstractMeltingEmiRecipe(MeltingRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public ResourceLocation getId() {
    return recipe.getId();
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return List.of(EmiIngredient.of(recipe.getInput()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    // 0th output is main product, rest is byproducts
    return recipe.getOutputWithByproducts()
      .stream()
      .map(e -> e.get(0))
      .map(s -> FluidEmiStack.of(s.getFluid(), s.getAmount()))
      .toList();
  }

  @Override
  public int getDisplayWidth() {
    return 132;
  }

  @Override
  public int getDisplayHeight() {
    return 40;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND_LOC, 0, 0, 132, 40, 0, 0);

    // draw the arrow
    //widgets.addTexture(EmiTexture.EMPTY_ARROW, 56, 18);
  }
}
