package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.plugin.emi.casting.CastingBasinEmiRecipe;
import slimeknights.tconstruct.plugin.emi.casting.CastingTableEmiRecipe;
import slimeknights.tconstruct.plugin.emi.entity.SeveringEmiRecipe;
import slimeknights.tconstruct.plugin.emi.melting.FoundryEmiRecipe;
import slimeknights.tconstruct.plugin.emi.melting.MeltingEmiRecipe;
import slimeknights.tconstruct.plugin.emi.modifiers.ModifierEmiRecipe;
import slimeknights.tconstruct.plugin.emi.modifiers.ModifierEmiStack;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static slimeknights.tconstruct.TConstruct.MOD_ID;

public class EMIPlugin implements EmiPlugin {
  public static final EmiRecipeCategory SEVERING_CATEGORY =
    new EmiRecipeCategory(TConstruct.getResource("severing"),
      EmiStack.of(TinkerTools.cleaver.get().getRenderTool()));
  public static final EmiRecipeCategory MODIFIER_CATEGORY =
    new EmiRecipeCategory(TConstruct.getResource("modifiers"),
      EmiStack.of(CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem), SlotType.UPGRADE)));
  public static final EmiRecipeCategory CASTING_BASIN_CATEGORY =
    new EmiRecipeCategory(TConstruct.getResource("casting_basin"),
      EmiStack.of(TinkerSmeltery.searedBasin.get()));
  public static final EmiRecipeCategory CASTING_TABLE_CATEGORY =
    new EmiRecipeCategory(TConstruct.getResource("casting_table"),
      EmiStack.of(TinkerSmeltery.searedTable.get()));
  public static final EmiRecipeCategory MOLDING_CATEGORY =
    new EmiRecipeCategory(TConstruct.getResource("molding"),
      EmiStack.of(TinkerSmeltery.blankSandCast.get()));
  public static final EmiRecipeCategory MELTING_CATEGORY =
    new EmiRecipeCategory(TConstruct.getResource("melting"),
      EmiStack.of(TinkerSmeltery.searedMelter.get()));
  public static final EmiRecipeCategory FOUNDRY_CATEGORY =
    new EmiRecipeCategory(TConstruct.getResource("foundry"),
      EmiStack.of(TinkerSmeltery.foundryController.get()));

  @Override
  public void register(EmiRegistry registry) {
    // categories
    // casting
    registry.addCategory(CASTING_BASIN_CATEGORY);
    registry.addCategory(CASTING_TABLE_CATEGORY);
    registry.addCategory(MOLDING_CATEGORY);
    // melting and casting
    registry.addCategory(MELTING_CATEGORY);
//    registry.addRecipeCategories(new AlloyRecipeCategory(guiHelper));
//    registry.addRecipeCategories(new EntityMeltingRecipeCategory(guiHelper));
    registry.addCategory(FOUNDRY_CATEGORY);
    // tinker station
    registry.addCategory(MODIFIER_CATEGORY);
    registry.addCategory(SEVERING_CATEGORY);
    // part builder
//    registry.addRecipeCategories(new PartBuilderCategory(guiHelper));

    // recipes
    RecipeManager manager = registry.getRecipeManager();

    // casting
    RecipeHelper.getJEIRecipes(manager, TinkerRecipeTypes.CASTING_BASIN.get(), IDisplayableCastingRecipe.class)
        .forEach(recipe -> registry.addRecipe(new CastingBasinEmiRecipe(recipe)));
    RecipeHelper.getJEIRecipes(manager, TinkerRecipeTypes.CASTING_TABLE.get(), IDisplayableCastingRecipe.class)
        .forEach(recipe -> registry.addRecipe(new CastingTableEmiRecipe(recipe)));

    // melting
    List<MeltingRecipe> meltingRecipes = RecipeHelper.getJEIRecipes(manager, TinkerRecipeTypes.MELTING.get(), MeltingRecipe.class);
    meltingRecipes.forEach(recipe -> registry.addRecipe(new MeltingEmiRecipe(recipe)));
    meltingRecipes.forEach(recipe -> registry.addRecipe(new FoundryEmiRecipe(recipe)));

    // modifiers
    RecipeHelper.getJEIRecipes(manager, TinkerRecipeTypes.TINKER_STATION.get(), IDisplayModifierRecipe.class)
      .stream()
      .sorted((r1, r2) -> {
        SlotType t1 = r1.getSlotType();
        SlotType t2 = r2.getSlotType();
        String n1 = t1 == null ? "zzzzzzzzzz" : t1.getName();
        String n2 = t2 == null ? "zzzzzzzzzz" : t2.getName();
        return n1.compareTo(n2);
      }).forEach(recipe -> registry.addRecipe(new ModifierEmiRecipe(recipe)));

    // beheading
    RecipeHelper.getJEIRecipes(manager, TinkerRecipeTypes.SEVERING.get(), SeveringRecipe.class)
      .forEach(severingRecipe -> registry.addRecipe(new SeveringEmiRecipe(severingRecipe)));

    // ingredients
    if (Config.CLIENT.showModifiersInJEI.get()) {
      RecipeHelper.getJEIRecipes(manager, TinkerRecipeTypes.TINKER_STATION.get(), IDisplayModifierRecipe.class)
        .stream()
        .map(recipe -> recipe.getDisplayResult().getModifier())
        .distinct()
        .sorted(Comparator.comparing(Modifier::getId))
        .map(mod -> new ModifierEntry(mod, 1))
        .forEach(mod -> registry.addEmiStack(new ModifierEmiStack(mod)));
    }
  }
}
