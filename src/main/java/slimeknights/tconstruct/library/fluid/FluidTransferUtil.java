package slimeknights.tconstruct.library.fluid;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.BucketItemAccessor;
import slimeknights.mantle.transfer.TransferUtil;
import slimeknights.mantle.transfer.fluid.EmptyFluidHandler;
import slimeknights.mantle.transfer.fluid.IFluidHandler;
import slimeknights.mantle.transfer.fluid.IFluidHandlerItem;
import slimeknights.mantle.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.mantle.fluid.FluidTransferHelper;

/**
 * Alternative to {@link net.minecraftforge.fluids.FluidUtil} since no one has time to make the forge util not a buggy mess
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FluidTransferUtil {
  /** @deprecated use {@link FluidTransferHelper#tryTransfer(IFluidHandler, IFluidHandler, int)} */
  @Deprecated
  public static FluidStack tryTransfer(IFluidHandler input, IFluidHandler output, int maxFill) {
    return FluidTransferHelper.tryTransfer(input, output, maxFill);
  }

  /** @deprecated use {@link FluidTransferHelper#interactWithBucket(Level, BlockPos, Player, InteractionHand, Direction, Direction)} */
  @Deprecated
  public static boolean interactWithBucket(Level world, BlockPos pos, Player player, InteractionHand hand, Direction hit, Direction offset) {
    return FluidTransferHelper.interactWithBucket(world, pos, player, hand, hit, offset);
  }

  /** @deprecated use {@link FluidTransferHelper#interactWithFluidItem(Level, BlockPos, Player, InteractionHand, BlockHitResult)} (IFluidHandler, IFluidHandler, int)} */
  @Deprecated
  public static boolean interactWithFluidItem(Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    return FluidTransferHelper.interactWithFluidItem(world, pos, player, hand, hit);
  }

  /** @deprecated use {@link FluidTransferHelper#interactWithTank(Level, BlockPos, Player, InteractionHand, BlockHitResult)} (IFluidHandler, IFluidHandler, int)} */
  @Deprecated
  public static boolean interactWithTank(Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    return FluidTransferHelper.interactWithTank(world, pos, player, hand, hit);
  }
}
