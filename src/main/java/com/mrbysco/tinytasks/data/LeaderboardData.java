package com.mrbysco.tinytasks.data;

import com.mrbysco.tinytasks.TinyTasksMod;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeaderboardData extends SavedData {
	private static final String DATA_NAME = TinyTasksMod.MOD_ID + "_scoreboard";
	private final Map<UUID, Integer> scoreMap = new HashMap<>();

	/**
	 * Loads the leaderboard data from the given tag.
	 * @param tag the NBT tag containing the leaderboard data
	 * @param provider the provider for the registries, can be null if not needed
	 * @return LeaderboardData instance populated with scores from the tag
	 */
	public static LeaderboardData load(CompoundTag tag, Provider provider) {
		LeaderboardData data = new LeaderboardData();
		if (tag.contains("scores")) {
			CompoundTag scoresTag = tag.getCompound("scores");
			for (String key : scoresTag.getAllKeys()) {
				UUID uuid = UUID.fromString(key);
				int score = scoresTag.getInt(key);
				data.scoreMap.put(uuid, score);
			}
		}
		return data;
	}

	/**
	 * Increments the score for a given UUID by a specified amount.
	 * @param uuid the UUID of the player whose score is to be incremented
	 * @param amount the amount by which to increment the score
	 */
	public void incrementScore(UUID uuid, int amount) {
		scoreMap.merge(uuid, amount, Integer::sum);
	}

	/**
	 * Increments the score for a given UUID by 1.
	 * @param level the level from which to retrieve the leaderboard data
	 * @param uuid the UUID of the player whose score is to be incremented
	 */
	public static void incrementScore(Level level, UUID uuid) {
		incrementScore(level, uuid, 1);
	}


	/**
	 * Increments the score for a given UUID by a specified amount.
	 * @param level the level from which to retrieve the leaderboard data
	 * @param uuid the UUID of the player whose score is to be incremented
	 * @param amount the amount by which to increment the score
	 */
	public static void incrementScore(Level level, UUID uuid, int amount) {
		LeaderboardData data = get(level);
		data.incrementScore(uuid, amount);
	}

	/**
	 * Saves the leaderboard data to the given tag.
	 * @param tag the NBT tag to save the leaderboard data into
	 * @param registries the provider for the registries, can be null if not needed
	 * @return the tag with the leaderboard data saved
	 */
	@Override
	public CompoundTag save(CompoundTag tag, Provider registries) {
		CompoundTag scoresTag = new CompoundTag();
		for (Map.Entry<UUID, Integer> entry : scoreMap.entrySet()) {
			scoresTag.putInt(entry.getKey().toString(), entry.getValue());
		}
		tag.put("scores", scoresTag);
		return tag;
	}

	/**
	 * Retrieves the leaderboard data for the given level.
	 * @param level the level from which to retrieve the leaderboard data
	 * @return LeaderboardData instance containing the scores for the level
	 */
	public static LeaderboardData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(new SavedData.Factory<>(LeaderboardData::new, LeaderboardData::load, null), DATA_NAME);
	}
}
