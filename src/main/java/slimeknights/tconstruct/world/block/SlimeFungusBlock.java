package slimeknights.tconstruct.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.function.Supplier;

/** Update of fungus that grows on slime soil instead */
public class SlimeFungusBlock extends FungusBlock {
  public SlimeFungusBlock(Properties properties, Supplier<Holder<ConfiguredFeature<HugeFungusConfiguration,?>>> fungusFeature) {
    super(properties, fungusFeature);
  }

  @Override
  public boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
    return state.is(TinkerTags.Blocks.SLIMY_SOIL);
  }

  @Override
  public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
    return worldIn.getBlockState(pos.below()).is(TinkerTags.Blocks.SLIMY_SOIL);
  }
}
