package slimeknights.tconstruct.plugin.emi.partbuilder;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.plugin.emi.EMIPlugin;
import slimeknights.tconstruct.plugin.jei.partbuilder.MaterialItemList;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class PartBuilderEmiRecipe implements EmiRecipe {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private final IDisplayPartBuilderRecipe recipe;

  public PartBuilderEmiRecipe(IDisplayPartBuilderRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return EMIPlugin.PART_BUILDER_CATEGORY;
  }

  @Override
  public @Nullable ResourceLocation getId() {
    return recipe.getId();
  }

  @Override
  public List<EmiIngredient> getInputs() {
    EmiIngredient materialVariant = EmiIngredient.of(MaterialItemList.getItems(recipe.getMaterial().getVariant()).stream().map(EmiStack::of).toList());
    EmiIngredient patternItems = EmiIngredient.of(recipe.getPatternItems().stream().map(EmiStack::of).toList());
    return List.of(materialVariant, patternItems);
  }

  @Override
  public List<EmiIngredient> getCatalysts() {
    return List.of(new PatternEmiStack(recipe.getPattern()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(EmiStack.of(recipe.getResultItem()));
  }

  @Override
  public int getDisplayWidth() {
    return 121;
  }

  @Override
  public int getDisplayHeight() {
    return 46;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(new EmiTexture(BACKGROUND_LOC, 0, 117, 121, 46), 0, 0);

    // items
    widgets.addSlot(getInputs().get(0), 24, 15).drawBack(false);
    widgets.addSlot(getInputs().get(1), 3, 15).drawBack(false);
    // patterns
    widgets.addSlot(getCatalysts().get(0), 45, 15).drawBack(false).catalyst(true);
    // TODO: material input?

    // output
    widgets.addSlot(getOutputs().get(0), 91, 10).drawBack(false).output(true).recipeContext(this);

    // texts
    Component name = MaterialTooltipCache.getColoredDisplayName(recipe.getMaterial().getVariant());
    widgets.addText(name, 3, 2, Objects.requireNonNullElse(name.getStyle().getColor(), ResourceColorManager.WHITE).getValue(), true);
    Component cooling = new TranslatableComponent("jei.tconstruct.part_builder.cost", recipe.getCost());
    widgets.addText(cooling, 3, 35, Color.GRAY.getRGB(), false);
  }
}
