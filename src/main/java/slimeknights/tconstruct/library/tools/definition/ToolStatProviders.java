package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStatsBuilder;
import slimeknights.tconstruct.tools.MeleeHarvestToolStatsBuilder;
import slimeknights.tconstruct.tools.RangedToolStatsBuilder;
import slimeknights.tconstruct.tools.stats.BowstringMaterialStats;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;

import java.util.Set;

public class ToolStatProviders {
  /** For tools that have no parts, crafted directly in the crafting table */
  public static final IToolStatProvider NO_PARTS = new IToolStatProvider() {
    @Override
    public StatsNBT buildStats(ToolDefinition definition, MaterialNBT materials) {
      return ToolStatsBuilder.noParts(definition).buildStats();
    }

    @Override
    public boolean isMultipart() {
      return false;
    }

    @Override
    public void validate(ToolDefinitionData data) {
      if (!data.getParts().isEmpty()) {
        throw new IllegalStateException("Cannot have parts for a specialized tool");
      }
    }
  };

  /** Tools with 1 or more tool parts using melee stats */
  public static final IToolStatProvider MELEE_HARVEST = new IToolStatProvider() {
    private static final Set<MaterialStatsId> VALID_STATS = ImmutableSet.of(HandleMaterialStats.ID, ExtraMaterialStats.ID);

    @Override
    public StatsNBT buildStats(ToolDefinition definition, MaterialNBT materials) {
      return MeleeHarvestToolStatsBuilder.from(definition, materials).buildStats();
    }

    @Override
    public boolean isMultipart() {
      return true;
    }

    @Override
    public void validate(ToolDefinitionData data) {
      IToolStatProvider.validate("Melee/Harvest", HeadMaterialStats.ID, VALID_STATS, data);
    }
  };

  /** Tools with 1 or more tool parts using ranged stats */
  public static final IToolStatProvider RANGED = new IToolStatProvider() {
    private static final Set<MaterialStatsId> VALID_STATS = ImmutableSet.of(BowstringMaterialStats.ID, GripMaterialStats.ID);

    @Override
    public StatsNBT buildStats(ToolDefinition definition, MaterialNBT materials) {
      return RangedToolStatsBuilder.from(definition, materials).buildStats();
    }

    @Override
    public boolean isMultipart() {
      return true;
    }

    @Override
    public void validate(ToolDefinitionData data) {
      IToolStatProvider.validate("Ranged", LimbMaterialStats.ID, VALID_STATS, data);
    }
  };
}
