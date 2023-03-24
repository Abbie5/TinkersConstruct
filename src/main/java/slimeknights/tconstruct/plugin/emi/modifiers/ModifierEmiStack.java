package slimeknights.tconstruct.plugin.emi.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.ArrayList;
import java.util.List;

public class ModifierEmiStack extends EmiStack {
  private final ModifierRenderer renderer;
  private final ModifierEntry entry;
  public ModifierEmiStack(ModifierEntry entry) {
    this.entry = entry;
    this.renderer = new ModifierRenderer(entry);
  }
  @Override
  public EmiStack copy() {
    return new ModifierEmiStack(entry);
  }

  @Override
  public void render(PoseStack matrices, int x, int y, float delta, int flags) {
    renderer.render(matrices, x, y, delta);
  }

  @Override
  public boolean isEmpty() {
    return entry == null;
  }

  @Override
  public CompoundTag getNbt() {
    return null;
  }

  @Override
  public Object getKey() {
    return entry.getModifier();
  }

  @Override
  public ResourceLocation getId() {
    return entry.getId();
  }

  @Override
  public List<Component> getTooltipText() {
    List<Component> tooltip = new ArrayList<>();
    tooltip.add(entry.getModifier().getDisplayName());
    tooltip.addAll(entry.getModifier().getDescriptionList());
    return tooltip;
  }

  @Override
  public List<ClientTooltipComponent> getTooltip() {
    return getTooltipText().stream()
      .map(Component::getVisualOrderText)
      .map(ClientTooltipComponent::create)
      .toList();
  }

  @Override
  public Component getName() {
    return entry.getModifier().getDisplayName(entry.getLevel());
  }
}
