package com.mrbysco.tinytasks;

import com.mrbysco.tinytasks.tasks.CraftTask;
import com.mrbysco.tinytasks.tasks.EatTask;
import com.mrbysco.tinytasks.tasks.EquipTask;
import com.mrbysco.tinytasks.tasks.MineTask;
import com.mrbysco.tinytasks.tasks.PickUpTask;
import com.mrbysco.tinytasks.tasks.Task;
import com.mrbysco.tinytasks.tasks.UseTask;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskRegistry {
	private static final List<Task> tasks = new ArrayList<>();

	/**
	 * Returns an unmodifiable list of all tasks in the registry.
	 * @return An unmodifiable list of tasks.
	 */
	public static List<Task> getTasks() {
		return List.copyOf(tasks);
	}

	public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(CraftTask.REGISTRY_KEY, CraftTask.CODEC);
		event.dataPackRegistry(EatTask.REGISTRY_KEY, EatTask.CODEC);
		event.dataPackRegistry(EquipTask.REGISTRY_KEY, EquipTask.CODEC);
		event.dataPackRegistry(MineTask.REGISTRY_KEY, MineTask.CODEC);
		event.dataPackRegistry(PickUpTask.REGISTRY_KEY, PickUpTask.CODEC);
		event.dataPackRegistry(UseTask.REGISTRY_KEY, UseTask.CODEC);
	}

	/**
	 * Called when the server has started, used to reload tasks from the registries.
	 * @param event The server started event.
	 */
	public static void onServerStarted(ServerStartedEvent event) {
		final RegistryAccess registryAccess = event.getServer().registryAccess();

		reloadTasks(registryAccess);
	}

	/**
	 * Reloads the tasks from the registries.
	 *
	 * @param registryAccess The registry access to use for loading tasks.
	 */
	private static void reloadTasks(RegistryAccess registryAccess) {
		tasks.clear();

		final Registry<CraftTask> craftTaskRegistry = registryAccess.registryOrThrow(CraftTask.REGISTRY_KEY);
		List<CraftTask> craftTasks = new ArrayList<>();
		craftTaskRegistry.entrySet().forEach((key) -> craftTasks.add(key.getValue()));
		tasks.addAll(craftTasks);
		TinyTasksMod.LOGGER.info("Loaded {} craft tasks", craftTasks.size());

		final Registry<EatTask> eatTaskRegistry = registryAccess.registryOrThrow(EatTask.REGISTRY_KEY);
		List<EatTask> eatTasks = new ArrayList<>();
		eatTaskRegistry.entrySet().forEach((key) -> eatTasks.add(key.getValue()));
		tasks.addAll(eatTasks);
		TinyTasksMod.LOGGER.info("Loaded {} eat tasks", eatTasks.size());

		final Registry<EquipTask> equipTaskRegistry = registryAccess.registryOrThrow(EquipTask.REGISTRY_KEY);
		List<EquipTask> equipTasks = new ArrayList<>();
		equipTaskRegistry.entrySet().forEach((key) -> equipTasks.add(key.getValue()));
		tasks.addAll(equipTasks);
		TinyTasksMod.LOGGER.info("Loaded {} equip tasks", equipTasks.size());

		final Registry<MineTask> mineTaskRegistry = registryAccess.registryOrThrow(MineTask.REGISTRY_KEY);
		List<MineTask> mineTasks = new ArrayList<>();
		mineTaskRegistry.entrySet().forEach((key) -> mineTasks.add(key.getValue()));
		tasks.addAll(mineTasks);
		TinyTasksMod.LOGGER.info("Loaded {} mine tasks", mineTasks.size());

		final Registry<PickUpTask> pickUpTaskRegistry = registryAccess.registryOrThrow(PickUpTask.REGISTRY_KEY);
		List<PickUpTask> pickUpTasks = new ArrayList<>();
		pickUpTaskRegistry.entrySet().forEach((key) -> pickUpTasks.add(key.getValue()));
		tasks.addAll(pickUpTasks);
		TinyTasksMod.LOGGER.info("Loaded {} pick up tasks", pickUpTasks.size());

		final Registry<UseTask> useTaskRegistry = registryAccess.registryOrThrow(UseTask.REGISTRY_KEY);
		List<UseTask> useTasks = new ArrayList<>();
		useTaskRegistry.entrySet().forEach((key) -> useTasks.add(key.getValue()));
		tasks.addAll(useTasks);
		TinyTasksMod.LOGGER.info("Loaded {} use tasks", useTasks.size());

		TinyTasksMod.LOGGER.info("Loaded {} tasks in total", tasks.size());

		if (!tasks.isEmpty())
			tasks.sort(Comparator.comparingInt(Task::getWeight));
	}

	/**
	 * Returns a random task from the registry based on their weights.
	 * @return A random task from the registry.
	 */
	@NotNull
	public static Task getRandomTask() {
		List<Task> tasks = TaskRegistry.getTasks();
		// Compute the total weight of all tasks together
		double totalWeight = 0.0d;
		for (Task task : tasks) {
			totalWeight += task.getWeight();
		}
		// Now choose a random task
		int randomIndex = -1;
		double random = Math.random() * totalWeight;
		for (int i = 0; i < tasks.size(); ++i) {
			random -= tasks.get(i).getWeight();
			if (random <= 0.0d) {
				randomIndex = i;
				break;
			}
		}
		return tasks.get(randomIndex);
	}
}
