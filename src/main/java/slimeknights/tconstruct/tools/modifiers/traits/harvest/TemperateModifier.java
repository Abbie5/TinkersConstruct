package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import io.github.fabricators_of_create.porting_lib.event.PlayerBreakSpeedCallback.BreakSpeed;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class TemperateModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "temperate.mining_speed");

  /** Gets the bonus for the given position */
  private static float getBonus(Player player, BlockPos pos, int level) {
    return Math.abs(player.level.getBiome(pos).getTemperature(pos) - BASELINE_TEMPERATURE) * level / 10;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      // temperature ranges from 0 to 1.25. Division makes it 0 to 0.125 per level
      event.setNewSpeed(event.getNewSpeed() * (1 + getBonus(event.getPlayer(), event.getPos(), level)));
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    if (tool.hasTag(TinkerTags.Items.HARVEST)) {
      float bonus;
      if (player != null && key == TooltipKey.SHIFT) {
        bonus = getBonus(player, player.blockPosition(), level);
      } else {
        bonus = level * 0.125f;
      }
      addPercentTooltip(MINING_SPEED, bonus, tooltip);
    }
  }
}
