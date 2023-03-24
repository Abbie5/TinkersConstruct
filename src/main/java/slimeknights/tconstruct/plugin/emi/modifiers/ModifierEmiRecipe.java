package slimeknights.tconstruct.plugin.emi.modifiers;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.plugin.emi.EMIPlugin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ModifierEmiRecipe implements EmiRecipe {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private final List<EmiIngredient> input = new ArrayList<>();
  private final List<EmiIngredient> tools;
  private final EmiStack output;
  private final ResourceLocation id;
  private final boolean hasRequirements;
  private final boolean isIncremental;
  private final int maxLevel;
  private final SlotType.SlotCount slots;
  private final String requirementsError;
  public ModifierEmiRecipe(IDisplayModifierRecipe recipe) {
    id = recipe.getModifier().getId();
    tools = Stream.of(recipe.getToolWithoutModifier(), recipe.getToolWithModifier()).map(t -> t.stream().map(EmiStack::of).toList()).map(EmiIngredient::of).toList();
    IntStream.range(0, 5).forEachOrdered(i -> input.add(EmiIngredient.of(recipe.getDisplayItems(i).stream().map(EmiStack::of).toList())));
    output = new ModifierEmiStack(recipe.getDisplayResult());
    hasRequirements = recipe.hasRequirements();
    isIncremental = recipe.isIncremental();
    maxLevel = recipe.getMaxLevel();
    slots = recipe.getSlots();
    requirementsError = recipe.getRequirementsError();
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return EMIPlugin.MODIFIER_CATEGORY;
  }

  @Override
  public @Nullable ResourceLocation getId() {
    return id;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return input;
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(output);
  }

  @Override
  public List<EmiIngredient> getCatalysts() {
    return tools;
  }

  @Override
  public int getDisplayWidth() {
    return 128;
  }

  @Override
  public int getDisplayHeight() {
    return 77;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND_LOC, 0, 0, 128, 77, 0, 0);

    // slot outlines
    drawOutline(widgets, 0,  2, 32);
    drawOutline(widgets, 1, 24, 14);
    drawOutline(widgets, 2, 46, 32);
    drawOutline(widgets, 3, 42, 57);
    drawOutline(widgets, 4,  6, 57);

    // info icons
    if (hasRequirements) {
      widgets.addTexture(BACKGROUND_LOC, 66, 58, 16, 16, 128, 17)
        .tooltip((checkX, checkY) -> {
          if (GuiUtil.isHovered(checkX, checkY, 66, 58, 16, 16))
            return List.of(ClientTooltipComponent.create(new TranslatableComponent(requirementsError).getVisualOrderText()));
          return null;
        });
    }
    if (isIncremental) {
      widgets.addTexture(BACKGROUND_LOC, 83, 59, 16, 16, 128, 33)
        .tooltip((checkX, checkY) -> {
          if (GuiUtil.isHovered(checkX, checkY, 83, 59, 16, 16))
            return List.of(ClientTooltipComponent.create(new TranslatableComponent("jei.tconstruct.modifiers.incremental").getVisualOrderText()));
          return null;
        });
    }

    // max count
    Font fontRenderer = Minecraft.getInstance().font;
    if (maxLevel > 0) {
      widgets.addText(new TranslatableComponent("jei.tconstruct.modifiers.max").append(String.valueOf(maxLevel)), 66, 16, Color.GRAY.getRGB(), false);
    }

    // slot cost
    String name;
    if (slots == null) {
      // slotless
      name = "slotless";
    } else {
      Component text = new TextComponent(String.valueOf(slots.getCount()));
      widgets.addText(text, 111 - fontRenderer.width(text), 63, Color.GRAY.getRGB(), false);

      SlotType type = slots.getType();
      if (type == SlotType.ABILITY) {
        name = "ability";
      } else if (type == SlotType.UPGRADE) {
        name = "upgrade";
      } else if (type == SlotType.DEFENSE) {
        name = "defense";
      } else {
        name = "default";
      }
    }
    widgets.addTexture(TConstruct.getResource("textures/item/slot/"+name+".png"), 0, 0, 16, 16, 0, 0);

    // inputs
    widgets.addSlot(input.get(0),  2, 32).drawBack(false);
    widgets.addSlot(input.get(1), 24, 14).drawBack(false);
    widgets.addSlot(input.get(2), 46, 32).drawBack(false);
    widgets.addSlot(input.get(3), 42, 57).drawBack(false);
    widgets.addSlot(input.get(4),  6, 57).drawBack(false);
    // modifiers
    widgets.addSlot(output, 3, 3).recipeContext(this);
    // tool
    widgets.addSlot(tools.get(0),  24, 37).drawBack(false);
    widgets.addSlot(tools.get(1), 100, 29).drawBack(false).output(true);
  }

  private void drawOutline(WidgetHolder widgets, int slot, int x, int y) {
    if (input.get(slot).isEmpty()) {
      widgets.addTexture(BACKGROUND_LOC, x + 1, y + 1, 16, 16, 128 + slot * 16, 0);
    }
  }
}
