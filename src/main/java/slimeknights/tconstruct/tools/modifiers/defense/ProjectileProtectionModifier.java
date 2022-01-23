package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;

import javax.annotation.Nullable;
import java.util.List;

public class ProjectileProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  /** Entity data key for the data associated with this modifier */
  private static final TinkerDataKey<ModifierMaxLevel> PROJECTILE_DATA = TConstruct.createKey("projectile_protection");
  public ProjectileProtectionModifier() {
    super(0xE2A856, PROJECTILE_DATA);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingVisibilityEvent.class, ProjectileProtectionModifier::livingVisibility);
  }

  @Override
  protected ModifierMaxLevel createData() {
    return new ModifierMaxLevel();
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (!source.isBypassMagic() && !source.isBypassInvul() && source.isProjectile()) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    AbstractProtectionModifier.addResistanceTooltip(this, tool, level, 2f, tooltip);
  }

  /** Reduces visibility to mobs */
  private static void livingVisibility(LivingVisibilityEvent event) {
    LivingEntity living = event.getEntityLiving();
    living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      ModifierMaxLevel projData = data.get(PROJECTILE_DATA);
      if (projData != null) {
        float max = projData.getMax();
        if (max > 0) {
          // reduces visibility by 5% per level
          event.modifyVisibility(Math.max(0, 1 - (max * 0.05)));
        }
      }
    });
  }
}
