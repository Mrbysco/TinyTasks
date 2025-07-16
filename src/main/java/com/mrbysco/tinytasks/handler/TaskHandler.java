package com.mrbysco.tinytasks.handler;

import com.mrbysco.tinytasks.TaskRegistry;
import com.mrbysco.tinytasks.data.LeaderboardData;
import com.mrbysco.tinytasks.tasks.CraftTask;
import com.mrbysco.tinytasks.tasks.EatTask;
import com.mrbysco.tinytasks.tasks.EquipTask;
import com.mrbysco.tinytasks.tasks.MineTask;
import com.mrbysco.tinytasks.tasks.PickUpTask;
import com.mrbysco.tinytasks.tasks.Task;
import com.mrbysco.tinytasks.tasks.UseTask;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles the Tiny Tasks system, managing task generation, completion, and player interactions.
 */
@EventBusSubscriber
public class TaskHandler {
	private static final int TASK_DURATION = (30 * 20); // 30 seconds in ticks
	private static Task currentTask = null;
	private static long completeTime = -0L;
	private static final List<UUID> completedPlayers = new ArrayList<>();

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		MinecraftServer server = event.getServer();
		boolean canHaveTask = !server.getPlayerList().getPlayers().isEmpty();
		if (currentTask == null) {
			if (canHaveTask)
				currentTask = TaskRegistry.getRandomTask();
		} else {
			ServerLevel serverLevel = server.overworld();
			if (serverLevel.getGameTime() % 20 == 0) { // Check every second
				MutableComponent component = Component.literal("Tiny Task: ").withStyle(ChatFormatting.YELLOW);
				component = component.append(getTaskDescription(currentTask));

				for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
					serverPlayer.displayClientMessage(
							component, true
					);
				}

				if (!canHaveTask || (completeTime != -0L && serverLevel.getGameTime() - completeTime >= TASK_DURATION)) { // 30 seconds
					server.getPlayerList().broadcastSystemMessage(
							Component.literal("§cTask ended!"), false
					);
					currentTask = null;
					completeTime = -0L;
					completedPlayers.clear();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onCrafted(ItemCraftedEvent event) {
		Player player = event.getEntity();
		Level level = player.level();
		if (!level.isClientSide && currentTask instanceof CraftTask craftTask) {
			ItemStack stack = event.getCrafting();
			if (craftTask.matches(stack)) {
				userCompletedTask((ServerLevel) level, player);
			}
		}
	}

	@SubscribeEvent
	public static void onPickup(ItemEntityPickupEvent.Post event) {
		Player player = event.getPlayer();
		Level level = player.level();
		if (!level.isClientSide && currentTask instanceof PickUpTask pickUpTask) {
			ItemStack stack = event.getItemEntity().getItem();
			if (pickUpTask.matches(stack)) {
				userCompletedTask((ServerLevel) level, player);
			}
		}
	}

	@SubscribeEvent
	public static void onEquip(LivingEquipmentChangeEvent event) {
		LivingEntity livingEntity = event.getEntity();
		Level level = livingEntity.level();
		if (!level.isClientSide && livingEntity instanceof Player player && currentTask instanceof EquipTask equipTask) {
			ItemStack stack = event.getTo();
			if (equipTask.matches(stack)) {
				userCompletedTask((ServerLevel) level, player);
			}
		}
	}

	@SubscribeEvent
	public static void onUse(LivingEntityUseItemEvent event) {
		LivingEntity livingEntity = event.getEntity();
		Level level = livingEntity.level();
		if (!level.isClientSide && livingEntity instanceof Player player && currentTask instanceof UseTask useTask) {
			ItemStack stack = event.getItem();
			if (useTask.matches(stack)) {
				userCompletedTask((ServerLevel) level, player);
			}
		}
	}

	@SubscribeEvent
	public static void onUse(BreakEvent event) {
		Player player = event.getPlayer();
		Level level = player.level();
		if (!level.isClientSide && currentTask instanceof MineTask mineTask) {
			if (mineTask.matches(event.getState().getBlock().asItem().getDefaultInstance())) {
				userCompletedTask((ServerLevel) level, player);
			}
		}
	}

	/**
	 * Handles the event when a player eats an item.
	 * @param livingEntity the entity that ate the item
	 * @param stack the item stack that was eaten
	 */
	public static void onEat(LivingEntity livingEntity, ItemStack stack) {
		Level level = livingEntity.level();
		if (!level.isClientSide && livingEntity instanceof Player player && currentTask instanceof EatTask eatTask) {
			if (eatTask.matches(stack)) {
				userCompletedTask((ServerLevel) level, player);
			}
		}
	}

	/**
	 * Handles the completion of a task by a player.
	 * @param serverLevel the overworld level used for the scoreboard data
	 * @param player the player who completed the task
	 */
	private static void userCompletedTask(ServerLevel serverLevel, Player player) {
		UUID playerId = player.getGameProfile().getId();
		if (completedPlayers.contains(playerId)) {
			return; // Player has already completed the task
		}
		LeaderboardData.incrementScore(serverLevel, player.getGameProfile().getId());
		broadcastTaskCompletion(serverLevel, player, completeTime == -1L);
		if (completeTime == -1L) {
			completeTime = serverLevel.getGameTime();
		}
		completedPlayers.add(player.getGameProfile().getId());
	}

	/**
	 * Broadcasts a message to all players when a task is completed.
	 * @param level the overworld server level
	 * @param player the player who completed the task
	 * @param firstTime true if this is the first time the task has been completed, false otherwise
	 */
	private static void broadcastTaskCompletion(ServerLevel level, Player player, boolean firstTime) {
		String completionMessage = "§6%s§r has completed the task";
		if (firstTime) {
			completionMessage = "§6%s§r has completed the task first";
		}
		MutableComponent component = Component.literal(String.format(completionMessage, player.getDisplayName())).withStyle(ChatFormatting.YELLOW);

		component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(Action.SHOW_TEXT, getTaskDescription(currentTask))));
		level.getServer().getPlayerList().broadcastSystemMessage(
				component, false
		);
	}

	private static Component getTaskDescription(Task task) {
		if (task == null) {
			return Component.literal("No current task");
		}
		return Component.literal(task.getDescription()).withStyle(ChatFormatting.WHITE);
	}
}
