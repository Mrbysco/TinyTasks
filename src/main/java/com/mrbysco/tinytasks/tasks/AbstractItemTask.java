package com.mrbysco.tinytasks.tasks;

import com.mrbysco.tinytasks.TinyTasksMod;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class AbstractItemTask implements Task {
	private final RandomSource randomSource = RandomSource.create();

	private final List<Holder<Item>> items;
	private final int weight;
	private Item chosenItem;

	public AbstractItemTask(List<Holder<Item>> items, int weight) {
		this.items = items;
		this.weight = weight;
	}

	@Nullable
	public Item chosenItem() {
		if (chosenItem == null) {
			selectItem(randomSource);
		}
		return chosenItem;
	}

	public void selectItem(@NotNull RandomSource randomSource) {
		if (items.isEmpty()) {
			throw new IllegalStateException(String.format("No items available for %s Task", getName()));
		}
		chosenItem = items.get(randomSource.nextInt(items.size())).value();
	}

	public void clearSelectedItem() {
		chosenItem = null;
	}

	public boolean matches(@NotNull ItemStack stack) {
		if (chosenItem == null) {
			TinyTasksMod.LOGGER.error("No item selected for Pick Up Task, cannot match with stack: {}", stack);
			return false;
		} else {
			return stack.is(chosenItem);
		}
	}

	@Override
	public int getWeight() {
		return weight;
	}

	public List<Holder<Item>> items() {
		return items;
	}

	public int weight() {
		return weight;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (AbstractItemTask) obj;
		return Objects.equals(this.items, that.items) &&
				this.weight == that.weight;
	}

	@Override
	public int hashCode() {
		return Objects.hash(items, weight);
	}
}
