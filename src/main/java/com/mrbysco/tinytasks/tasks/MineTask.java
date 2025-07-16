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

public class MineTask extends AbstractItemTask {
	public static final ResourceKey<Registry<MineTask>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			TinyTasksMod.modLoc("mine_task"));

	public static final Codec<MineTask> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					ItemStack.ITEM_NON_AIR_CODEC.listOf().fieldOf("items").forGetter(MineTask::items),
					Codec.intRange(1, 100).fieldOf("weight").forGetter(MineTask::weight)
			).apply(instance, MineTask::new)
	);

	public MineTask(List<Holder<Item>> items, int weight) {
		super(items, weight);
	}

	@Override
	public String getName() {
		return "Mine Task";
	}

	@Override
	public String getDescription() {
		if (chosenItem() == null) {
			throw new IllegalStateException("No item selected for Mine Task");
		}
		return String.format("Mine 1 %s", chosenItem().getDescription().getString());
	}

	@Override
	public Codec<MineTask> codec() {
		return CODEC;
	}

	@Override
	public Codec<Optional<WithConditions<MineTask>>> conditionalCodec() {
		return ConditionalOps.createConditionalCodecWithConditions(CODEC);
	}

	@Override
	public String toString() {
		return "MineTask[" +
				"items=" + items() + ", " +
				"weight=" + weight() + ']';
	}

	/**
	 * Builder for creating a MineTask instance.
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

		public MineTask build() {
			if (items.isEmpty()) {
				throw new IllegalArgumentException("Items list cannot be empty");
			}
			return new MineTask(items, weight);
		}
	}
}
