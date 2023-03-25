package slimeknights.tconstruct.plugin.emi.melting;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;

import java.awt.Color;
import java.util.List;

public abstract class AbstractMeltingEmiRecipe implements EmiRecipe {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  protected static final EmiTexture plus = new EmiTexture(BACKGROUND_LOC, 132, 34, 6, 6);
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
    widgets.addFillingArrow(56, 18, recipe.getTime() * 250);
    if (recipe.getOreType() != null) {
      widgets.addTexture(plus, 87, 31)
        .tooltip((mouseX, mouseY) -> List.of(ClientTooltipComponent.create(new TranslatableComponent("jei.tconstruct.melting.ore").getVisualOrderText())));
    }

    // temperature
    int temperature = recipe.getTemperature();

    Font fontRenderer = Minecraft.getInstance().font;
    Component temp = new TranslatableComponent("jei.tconstruct.temperature", temperature);
    int x = 56 - fontRenderer.width(temp) / 2;
    widgets.addText(temp, x, 3, Color.GRAY.getRGB(), false);
  }
}
