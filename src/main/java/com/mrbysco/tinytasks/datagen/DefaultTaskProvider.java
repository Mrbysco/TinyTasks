package com.mrbysco.tinytasks.datagen;

import com.mrbysco.tinytasks.TinyTasksMod;
import com.mrbysco.tinytasks.tasks.CraftTask;
import com.mrbysco.tinytasks.tasks.EatTask;
import com.mrbysco.tinytasks.tasks.EquipTask;
import com.mrbysco.tinytasks.tasks.PickUpTask;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class DefaultTaskProvider extends TaskProvider {

	public DefaultTaskProvider(PackOutput packOutput, CompletableFuture<Provider> registries) {
		super(packOutput, registries, TinyTasksMod.MOD_ID);
	}

	@Override
	protected void start() {
		addPickUpTask("basic", new PickUpTask.Builder().addItems(
				Items.COBBLESTONE, Items.COBBLED_DEEPSLATE, Items.TUFF, Items.GRANITE, Items.DIORITE, Items.ANDESITE,
				Items.DIRT, Items.SAND, Items.GRAVEL, Items.CLAY_BALL, Items.SANDSTONE, Items.TERRACOTTA,
				Items.CARROT, Items.POTATO, Items.WHEAT_SEEDS, Items.WHEAT, Items.BREAD, Items.HAY_BLOCK,
				Items.APPLE, Items.EGG, Items.STICK, Items.KELP, Items.STRING, Items.GUNPOWDER, Items.ENDER_PEARL,
				Items.GLOWSTONE_DUST, Items.SNOWBALL, Items.COCOA_BEANS, Items.PUMPKIN, Items.MELON_SLICE,
				Items.BEEF, Items.MUTTON, Items.CHICKEN, Items.COD, Items.SALMON, Items.PORKCHOP,
				Items.CRAFTING_TABLE, Items.FURNACE, Items.CHEST, Items.MINECART, Items.OAK_DOOR, Items.OAK_PLANKS,
				Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG, Items.ACACIA_LOG,
				Items.CHERRY_LOG, Items.DARK_OAK_LOG, Items.MANGROVE_LOG, Items.POPPY, Items.DANDELION,
				Items.RAW_COPPER, Items.COAL
		).build());
		addPickUpTask("intermediate", new PickUpTask.Builder().addItems(
				Items.STONE, Items.RAW_IRON, Items.RAW_GOLD, Items.REDSTONE, Items.LAPIS_LAZULI,
				Items.EMERALD, Items.NETHERRACK, Items.SOUL_SAND, Items.SOUL_SOIL, Items.BASALT,
				Items.BLACKSTONE, Items.DEEPSLATE, Items.SUNFLOWER, Items.LILAC, Items.ROSE_BUSH,
				Items.PEONY, Items.RABBIT
		).weight(5).build());

		addEatTask("basic", new EatTask.Builder().addItems(
				Items.APPLE, Items.MELON_SLICE, Items.POTATO, Items.BAKED_POTATO, Items.CARROT, Items.BREAD,
				Items.BEEF, Items.MUTTON, Items.CHICKEN, Items.COD, Items.SALMON, Items.PORKCHOP,
				Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_CHICKEN, Items.COOKED_COD,
				Items.COOKED_SALMON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.PUMPKIN_PIE,
				Items.BEETROOT, Items.BEETROOT_SOUP, Items.MUSHROOM_STEW, Items.HONEY_BOTTLE,
				Items.SUSPICIOUS_STEW
		).build());
		addEatTask("intermediate", new EatTask.Builder().addItems(
				Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE,
				Items.PUFFERFISH
		).weight(5).build());

		addEquipTask("basic", new EquipTask.Builder().addItems(
				Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET,
				Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET,
				Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET,
				Items.CARVED_PUMPKIN
		).build());
		addEquipTask("intermediate", new EquipTask.Builder().addItems(
				Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET
		).weight(5).build());

		addCraftTask("basic", new CraftTask.Builder().addItems(
				Items.CRAFTING_TABLE, Items.FURNACE, Items.CHEST, Items.MINECART, Items.BUCKET,
				Items.SMOKER, Items.CAMPFIRE, Items.OAK_PLANKS, Items.SPRUCE_PLANKS, Items.BIRCH_PLANKS,
				Items.JUNGLE_PLANKS, Items.ACACIA_PLANKS, Items.CHERRY_PLANKS, Items.DARK_OAK_PLANKS, Items.MANGROVE_PLANKS
		).build());
		addCraftTask("intermediate", new CraftTask.Builder().addItems(
				Items.BLAST_FURNACE, Items.HOPPER, Items.DISPENSER, Items.DROPPER, Items.COMPARATOR,
				Items.REPEATER, Items.LECTERN, Items.BOOKSHELF, Items.ANVIL, Items.REDSTONE_BLOCK,
				Items.LAPIS_BLOCK, Items.EMERALD_BLOCK, Items.GOLD_BLOCK,
				Items.DIAMOND_BLOCK, Items.IRON_BLOCK, Items.COAL_BLOCK
		).weight(5).build());


	}
}
