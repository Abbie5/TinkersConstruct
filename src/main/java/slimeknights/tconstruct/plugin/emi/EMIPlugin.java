package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.plugin.emi.entity.SeveringEmiRecipe;
import slimeknights.tconstruct.plugin.emi.modifiers.ModifierEmiRecipe;
import slimeknights.tconstruct.plugin.emi.modifiers.ModifierEmiStack;
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
    new EmiRecipeCategory(new ResourceLocation(MOD_ID, "severing"),
      EmiStack.of(TinkerTools.cleaver.get().getRenderTool()));
  public static final EmiRecipeCategory MODIFIER_CATEGORY =
    new EmiRecipeCategory(new ResourceLocation(MOD_ID, "modifiers"),
      EmiStack.of(CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem), SlotType.UPGRADE)));


  @Override
  public void register(EmiRegistry registry) {
    // categories
    // casting
//    registry.addRecipeCategories(new CastingBasinCategory(guiHelper));
//    registry.addRecipeCategories(new CastingTableCategory(guiHelper));
//    registry.addRecipeCategories(new MoldingRecipeCategory(guiHelper));
    // melting and casting
//    registry.addRecipeCategories(new MeltingCategory(guiHelper));
//    registry.addRecipeCategories(new AlloyRecipeCategory(guiHelper));
//    registry.addRecipeCategories(new EntityMeltingRecipeCategory(guiHelper));
//    registry.addRecipeCategories(new FoundryCategory(guiHelper));
    // tinker station
    registry.addCategory(MODIFIER_CATEGORY);
    registry.addCategory(SEVERING_CATEGORY);
    // part builder
//    registry.addRecipeCategories(new PartBuilderCategory(guiHelper));

    // recipes
    RecipeManager manager = registry.getRecipeManager();

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
