package slimeknights.tconstruct.plugin.emi.casting;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipe;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCastingEmiRecipe implements EmiRecipe {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/casting.png");
  protected IDisplayableCastingRecipe recipe;
  private final EmiTexture block;

  public AbstractCastingEmiRecipe(IDisplayableCastingRecipe recipe, EmiTexture block) {
    this.recipe = recipe;
    this.block = block;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    List<EmiIngredient> inputs = new ArrayList<>();
    // fluid input
    inputs.add(EmiIngredient.of(recipe.getFluids().stream().map(f -> FluidEmiStack.of(f.getFluid(), f.getAmount())).toList()));
    // cast items
    EmiIngredient castItems = EmiStack.EMPTY;
    inputs.add(EmiIngredient.of(recipe.getCastItems().stream().map(EmiStack::of).toList()));

    return inputs;
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(EmiStack.of(recipe.getOutput()));
  }

  @Override
  public int getDisplayWidth() {
    return 117;
  }

  @Override
  public int getDisplayHeight() {
    return 54;
  }

  @Override
  public @Nullable ResourceLocation getId() {
    if (recipe instanceof ContainerFillingRecipe r)
      return r.getId();
    else if (recipe instanceof slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe r)
      return r.getId();
    else return null;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    // background
    widgets.addTexture(BACKGROUND_LOC, 0, 0, 117, 54, 0, 0);

    // casting table/basin block
    widgets.addTexture(block, 38, 35);

    // items
    if (!getInputs().get(1).isEmpty()) {
      widgets.addSlot(getInputs().get(1), 38, 19).drawBack(false).catalyst(!recipe.isConsumed());
    }
    widgets.addSlot(getOutputs().get(0), 93, 18).drawBack(false).output(true).recipeContext(this);

    // fluids
    // tank fluids
    long capacity = FluidValues.METAL_BLOCK;
    widgets.addSlot(getInputs().get(0), 3, 3).drawBack(false)
      .customBackground(null, 0, 0, 32, 32);
    // pouring fluid
    int h = 11;
    if (!recipe.hasCast()) {
      h += 16;
    }
    widgets.addSlot(getInputs().get(0), 43, 8).drawBack(false)
      .customBackground(null, 0, 0, 6, h);
  }
}
