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

public class UseTask extends AbstractItemTask {
	public static final ResourceKey<Registry<UseTask>> REGISTRY_KEY = ResourceKey.createRegistryKey(
			TinyTasksMod.modLoc("use_task"));

	public static final Codec<UseTask> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					ItemStack.ITEM_NON_AIR_CODEC.listOf().fieldOf("items").forGetter(UseTask::items),
					Codec.intRange(1, 100).fieldOf("weight").forGetter(UseTask::weight)
			).apply(instance, UseTask::new)
	);

	public UseTask(List<Holder<Item>> items, int weight) {
		super(items, weight);
	}

	@Override
	public String getName() {
		return "Use Task";
	}

	@Override
	public String getDescription() {
		if (chosenItem() == null) {
			throw new IllegalStateException("No item selected for Use Task");
		}
		return String.format("Use 1 %s", chosenItem().getDescription().getString());
	}

	@Override
	public Codec<UseTask> codec() {
		return CODEC;
	}

	@Override
	public Codec<Optional<WithConditions<UseTask>>> conditionalCodec() {
		return ConditionalOps.createConditionalCodecWithConditions(CODEC);
	}

	@Override
	public String toString() {
		return "UseTask[" +
				"items=" + items() + ", " +
				"weight=" + weight() + ']';
	}

	/**
	 * Builder for creating a UseTask instance.
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

		public UseTask build() {
			if (items.isEmpty()) {
				throw new IllegalArgumentException("Items list cannot be empty");
			}
			return new UseTask(items, weight);
		}
	}
}
