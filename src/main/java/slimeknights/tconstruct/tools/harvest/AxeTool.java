package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.CircleAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Collections;
import java.util.Set;

public class AxeTool extends HarvestTool {
  private static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.WOOD, Material.NETHER_WOOD, Material.PLANTS, Material.TALL_PLANTS, Material.BAMBOO, Material.GOURD, Material.LEAVES);
  public static final MaterialHarvestLogic HARVEST_LOGIC = new MaterialHarvestLogic(EXTRA_MATERIALS, 0, 0, 0) {
    @Override
    public Iterable<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
      if (!canAOE(tool, stack, state, matchType)) {
        return Collections.emptyList();
      }
      // axe uses circular harvest
      return CircleAOEHarvestLogic.calculate(this, tool, stack, world, player, origin, sideHit, 1 + tool.getModifierLevel(TinkerModifiers.expanded.get()), false, matchType);
    }
  };
  public AxeTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return this.getToolHarvestLogic().transformBlocks(context, ToolType.AXE, SoundEvents.ITEM_AXE_STRIP, false);
  }

  @Override
  public boolean dealDamage(ToolStack tool, LivingEntity player, Entity entity, float damage, boolean isCriticalHit, boolean fullyCharged) {
    boolean hit = super.dealDamage(tool, player, entity, damage, isCriticalHit, fullyCharged);
    if (hit && fullyCharged) {
      ToolAttackUtil.spawnAttachParticle(TinkerTools.axeAttackParticle.get(), player, 0.8d);
    }
    return hit;
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
    return true;
  }
}
