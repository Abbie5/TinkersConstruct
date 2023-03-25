package slimeknights.tconstruct.plugin.emi.entity;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.plugin.emi.EMIPlugin;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;

import java.awt.Color;
import java.util.List;

public class EntityMeltingEmiRecipe implements EmiRecipe {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  public static final EmiTexture icon = new EmiTexture(BACKGROUND_LOC, 174, 41, 16, 16);

  private final EntityMeltingRecipe recipe;

  public EntityMeltingEmiRecipe(EntityMeltingRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return EMIPlugin.ENTITY_MELTING_CATEGORY;
  }

  @Override
  public @Nullable ResourceLocation getId() {
    return recipe.getId();
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return List.of(EmiIngredient.of(recipe.getEntityInputs().stream().map(e -> new EntityEmiStack(e, 32)).toList()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(FluidEmiStack.of(recipe.getOutput().getFluid(), recipe.getOutput().getAmount()));
  }

  @Override
  public int getDisplayWidth() {
    return 150;
  }

  @Override
  public int getDisplayHeight() {
    return 62;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND_LOC, 0, 0, 150, 62, 0, 41);

    widgets.addFillingArrow(71, 21, 10000);

    // draw damage string next to the heart icon
    String damage = Float.toString(recipe.getDamage() / 2f);
    Font fontRenderer = Minecraft.getInstance().font;
    int x = 84 - fontRenderer.width(damage);
    widgets.addText(new TextComponent(damage), x, 8, Color.RED.getRGB(), false);

    // inputs, filtered by spawn egg item
    widgets.addSlot(getInputs().get(0), 19, 11)
      .drawBack(false)
      .customBackground(null, 0, 0, 32, 32);
    // add spawn eggs as hidden inputs
    //widgets.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getItemInputs());

    // output
    widgets.addSlot(getOutputs().get(0), 115, 11)
      .customBackground(null, 0, 0, 16, 32)
      .drawBack(false)
      .recipeContext(this);

    // show fuels that are valid for this recipe
    EmiIngredient fuels = EmiIngredient.of(MeltingFuelHandler
      .getUsableFuels(1)
      .stream()
      .map(f -> FluidEmiStack.of(f.getFluid(), f.getAmount()))
      .toList());
    widgets.addSlot(fuels, 74, 42)
      .drawBack(false);

  }
}
