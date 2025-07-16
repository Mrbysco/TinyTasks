package com.mrbysco.tinytasks.datagen;

import com.google.common.collect.ImmutableList;
import com.mrbysco.tinytasks.TinyTasksMod;
import com.mrbysco.tinytasks.tasks.CraftTask;
import com.mrbysco.tinytasks.tasks.EatTask;
import com.mrbysco.tinytasks.tasks.EquipTask;
import com.mrbysco.tinytasks.tasks.MineTask;
import com.mrbysco.tinytasks.tasks.PickUpTask;
import com.mrbysco.tinytasks.tasks.UseTask;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A data provider for Tiny Tasks that generates tasks.
 */
public abstract class TaskProvider implements DataProvider {
	private final PackOutput output;
	private final CompletableFuture<Provider> registries;
	private final String modid;
	private final Map<String, WithConditions<CraftTask>> toSerializeCraft = new HashMap<>();
	private final Map<String, WithConditions<EatTask>> toSerializeEat = new HashMap<>();
	private final Map<String, WithConditions<EquipTask>> toSerializeEquip = new HashMap<>();
	private final Map<String, WithConditions<MineTask>> toSerializeMine = new HashMap<>();
	private final Map<String, WithConditions<PickUpTask>> toSerializePickUp = new HashMap<>();
	private final Map<String, WithConditions<UseTask>> toSerializeUse = new HashMap<>();

	public TaskProvider(PackOutput packOutput, CompletableFuture<Provider> registries, String modid) {
		this.output = packOutput;
		this.registries = registries;
		this.modid = modid;
	}

	@Override
	public final CompletableFuture<?> run(CachedOutput cache) {
		return this.registries.thenCompose(registries -> this.run(cache, registries));
	}

	public CompletableFuture<?> run(CachedOutput cache, HolderLookup.Provider registries) {
		start();

		ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

		Path craftPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve(TinyTasksMod.MOD_ID).resolve("craft_task");
		for (var entry : toSerializeCraft.entrySet()) {
			var name = entry.getKey();
			var modifier = entry.getValue();
			var conditionalCodec = modifier.carrier().conditionalCodec();
			Path modifierPath = craftPath.resolve(name + ".json");
			futuresBuilder.add(DataProvider.saveStable(cache, registries, conditionalCodec, Optional.of(modifier), modifierPath));
		}

		Path eatPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve(TinyTasksMod.MOD_ID).resolve("eat_task");
		for (var entry : toSerializeEat.entrySet()) {
			var name = entry.getKey();
			var modifier = entry.getValue();
			var conditionalCodec = modifier.carrier().conditionalCodec();
			Path modifierPath = eatPath.resolve(name + ".json");
			futuresBuilder.add(DataProvider.saveStable(cache, registries, conditionalCodec, Optional.of(modifier), modifierPath));
		}

		Path equipPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve(TinyTasksMod.MOD_ID).resolve("equip_task");
		for (var entry : toSerializeEquip.entrySet()) {
			var name = entry.getKey();
			var modifier = entry.getValue();
			var conditionalCodec = modifier.carrier().conditionalCodec();
			Path modifierPath = equipPath.resolve(name + ".json");
			futuresBuilder.add(DataProvider.saveStable(cache, registries, conditionalCodec, Optional.of(modifier), modifierPath));
		}

		Path minePath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve(TinyTasksMod.MOD_ID).resolve("mine_task");
		for (var entry : toSerializeMine.entrySet()) {
			var name = entry.getKey();
			var modifier = entry.getValue();
			var conditionalCodec = modifier.carrier().conditionalCodec();
			Path modifierPath = minePath.resolve(name + ".json");
			futuresBuilder.add(DataProvider.saveStable(cache, registries, conditionalCodec, Optional.of(modifier), modifierPath));
		}

		Path pickUpPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve(TinyTasksMod.MOD_ID).resolve("pick_up_task");
		for (var entry : toSerializePickUp.entrySet()) {
			var name = entry.getKey();
			var modifier = entry.getValue();
			var conditionalCodec = modifier.carrier().conditionalCodec();
			Path modifierPath = pickUpPath.resolve(name + ".json");
			futuresBuilder.add(DataProvider.saveStable(cache, registries, conditionalCodec, Optional.of(modifier), modifierPath));
		}

		Path usePath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve(TinyTasksMod.MOD_ID).resolve("use_task");
		for (var entry : toSerializeUse.entrySet()) {
			var name = entry.getKey();
			var modifier = entry.getValue();
			var conditionalCodec = modifier.carrier().conditionalCodec();
			Path modifierPath = usePath.resolve(name + ".json");
			futuresBuilder.add(DataProvider.saveStable(cache, registries, conditionalCodec, Optional.of(modifier), modifierPath));
		}

		return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
	}

	protected abstract void start();

	public void addCraftTask(String taskID, CraftTask instance, List<ICondition> conditions) {
		this.toSerializeCraft.put(taskID, new WithConditions<>(conditions, instance));
	}

	public void addCraftTask(String taskID, CraftTask instance, ICondition... conditions) {
		addCraftTask(taskID, instance, Arrays.asList(conditions));
	}

	public void addEatTask(String taskID, EatTask instance, List<ICondition> conditions) {
		this.toSerializeEat.put(taskID, new WithConditions<>(conditions, instance));
	}

	public void addEatTask(String taskID, EatTask instance, ICondition... conditions) {
		addEatTask(taskID, instance, Arrays.asList(conditions));
	}

	public void addEquipTask(String taskID, EquipTask instance, List<ICondition> conditions) {
		this.toSerializeEquip.put(taskID, new WithConditions<>(conditions, instance));
	}

	public void addEquipTask(String taskID, EquipTask instance, ICondition... conditions) {
		addEquipTask(taskID, instance, Arrays.asList(conditions));
	}

	public void addMineTask(String taskID, MineTask instance, List<ICondition> conditions) {
		this.toSerializeMine.put(taskID, new WithConditions<>(conditions, instance));
	}

	public void addMineTask(String taskID, MineTask instance, ICondition... conditions) {
		addMineTask(taskID, instance, Arrays.asList(conditions));
	}

	public void addPickUpTask(String taskID, PickUpTask instance, List<ICondition> conditions) {
		this.toSerializePickUp.put(taskID, new WithConditions<>(conditions, instance));
	}

	public void addPickUpTask(String taskID, PickUpTask instance, ICondition... conditions) {
		addPickUpTask(taskID, instance, Arrays.asList(conditions));
	}

	public void addUseTask(String taskID, UseTask instance, List<ICondition> conditions) {
		this.toSerializeUse.put(taskID, new WithConditions<>(conditions, instance));
	}

	public void addUseTask(String taskID, UseTask instance, ICondition... conditions) {
		addUseTask(taskID, instance, Arrays.asList(conditions));
	}

	@Override
	public String getName() {
		return "Task Provider: " + modid;
	}


}
