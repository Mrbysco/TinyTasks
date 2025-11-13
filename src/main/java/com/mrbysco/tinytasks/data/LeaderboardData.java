package com.mrbysco.tinytasks.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.tinytasks.TinyTasksMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeaderboardData extends SavedData {
	private static final String DATA_NAME = TinyTasksMod.MOD_ID + "_scoreboard";

	public static final Codec<LeaderboardData> CODEC = RecordCodecBuilder.create(inst -> inst.group(Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).fieldOf("scores").forGetter(data -> data.scoreMap)).apply(inst, LeaderboardData::new));


	private final Map<UUID, Integer> scoreMap = new HashMap<>();

	public LeaderboardData() {
		this(Maps.newHashMap());
	}

	public LeaderboardData(Map<UUID, Integer> scoreMap) {
		this.scoreMap.putAll(scoreMap);
	}

	/**
	 * Increments the score for a given UUID by a specified amount.
	 *
	 * @param uuid   the UUID of the player whose score is to be incremented
	 * @param amount the amount by which to increment the score
	 */
	public void incrementScore(UUID uuid, int amount) {
		scoreMap.merge(uuid, amount, Integer::sum);
	}

	/**
	 * Increments the score for a given UUID by 1.
	 *
	 * @param level the level from which to retrieve the leaderboard data
	 * @param uuid  the UUID of the player whose score is to be incremented
	 */
	public static void incrementScore(Level level, UUID uuid) {
		incrementScore(level, uuid, 1);
	}


	/**
	 * Increments the score for a given UUID by a specified amount.
	 *
	 * @param level  the level from which to retrieve the leaderboard data
	 * @param uuid   the UUID of the player whose score is to be incremented
	 * @param amount the amount by which to increment the score
	 */
	public static void incrementScore(Level level, UUID uuid, int amount) {
		LeaderboardData data = get(level);
		data.incrementScore(uuid, amount);
	}

	public static SavedDataType<LeaderboardData> type() {
		return new SavedDataType<>(DATA_NAME, LeaderboardData::new, CODEC, null);
	}

	/**
	 * Retrieves the leaderboard data for the given level.
	 *
	 * @param level the level from which to retrieve the leaderboard data
	 * @return LeaderboardData instance containing the scores for the level
	 */
	public static LeaderboardData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		assert overworld != null;
		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(type());
	}
}
