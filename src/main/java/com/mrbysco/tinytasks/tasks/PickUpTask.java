package com.mrbysco.tinytasks.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.tinytasks.TinyTasksMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PickUpTask extends AbstractItemTask {
	public static final ResourceKey<Registry<PickUpTask>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			TinyTasksMod.modLoc("pick_up_task"));

	public static final Codec<PickUpTask> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					ItemStack.ITEM_NON_AIR_CODEC.listOf().fieldOf("items").forGetter(PickUpTask::items),
					Codec.intRange(1, 100).fieldOf("weight").forGetter(PickUpTask::weight)
			).apply(instance, PickUpTask::new)
	);

	public PickUpTask(List<Holder<Item>> items, int weight) {
		super(items, weight);
	}

	@Override
	public String getName() {
		return "Pick Up Task";
	}

	@Override
	public String getDescription() {
		if (chosenItem() == null) {
			throw new IllegalStateException("No item selected for Pick Up Task");
		}
		return String.format("Pick Up 1 §6%s§r", chosenItem().getDescription().getString());
	}

	@Override
	public Codec<PickUpTask> codec() {
		return CODEC;
	}

	@Override
	public Codec<Optional<WithConditions<PickUpTask>>> conditionalCodec() {
		return ConditionalOps.createConditionalCodecWithConditions(CODEC);
	}

	@Override
	public String toString() {
		return "PickUpTask[" +
				"items=" + items() + ", " +
				"weight=" + weight() + ']';
	}

	/**
	 * Builder for creating a PickUpTask instance.
	 */
	public static class Builder {
		private final List<Holder<Item>> items = new ArrayList<>();
		private int weight = 10;

		public Builder() {

		}

		public Builder addItem(ItemLike item) {
			this.addItemInternal(item);
			return this;
		}

		public Builder addItems(ItemLike... items) {
			for (ItemLike item : items) {
				this.addItemInternal(item);
			}
			return this;
		}

		private void addItemInternal(ItemLike itemLike) {
			Holder<Item> itemHolder = itemLike.asItem().builtInRegistryHolder();
			if (this.items.contains(itemHolder)) {
				throw new IllegalArgumentException("Item " + itemHolder + " is already added to the task");
			}
			this.items.add(itemHolder);
		}

		public Builder weight(int weight) {
			this.weight = weight;
			return this;
		}

		public PickUpTask build() {
			if (items.isEmpty()) {
				throw new IllegalArgumentException("Items list cannot be empty");
			}
			return new PickUpTask(items, weight);
		}
	}
}
