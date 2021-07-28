package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.TinkerTables;

public class TinkersChestTileEntity extends ChestTileEntity {
  public TinkersChestTileEntity() {
    // max 64 stacks for the modifier chest
    super(TinkerTables.tinkersChestTile.get(), TConstruct.makeTranslationKey("gui", "tinkers_chest"), 64, 16);
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    // no duplicate limit, this chest just limits total slots
    return true;
  }

  @Override
  public int getVisualSize() {
    // always show all slots
    return getSizeInventory();
  }
}
