package slimeknights.tconstruct.plugin.emi.entity;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.plugin.emi.EMIPlugin;

import java.util.List;

public class SeveringEmiRecipe implements EmiRecipe {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private final ResourceLocation id;
  private final EmiIngredient input;
  private final EmiStack output;
  public SeveringEmiRecipe(SeveringRecipe recipe) {
      this.id = recipe.getId();
      this.input = EmiIngredient.of(recipe.getEntityInputs().stream().map(e -> new EntityEmiStack(e, 32)).toList());
      this.output = EmiStack.of(recipe.getOutput());
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return EMIPlugin.SEVERING_CATEGORY;
  }

  @Override
  public @Nullable ResourceLocation getId() {
    return id;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return List.of(input);
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(output);
  }

  @Override
  public int getDisplayWidth() {
    return 100;
  }

  @Override
  public int getDisplayHeight() {
    return 38;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND_LOC, 0, 0, 100, 38, 0, 78);
    widgets.addSlot(input, 3, 3).drawBack(false).customBackground(null, 0, 0, 32, 32);

    // output
    widgets.addSlot(output, 71, 6).drawBack(false).output(true).recipeContext(this);
  }

  @Override
  public List<EmiIngredient> getCatalysts() {
    return List.of();
  }
}
