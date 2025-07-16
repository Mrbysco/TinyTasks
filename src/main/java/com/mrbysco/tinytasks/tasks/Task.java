package com.mrbysco.tinytasks.tasks;

import com.mojang.serialization.Codec;

public interface Task {
	/**
	 * Returns the name of the task.
	 *
	 * @return The name of the task.
	 */
	String getName();

	/**
	 * Returns the weight of the task.
	 * The weight is used to determine the likelihood of this task being selected.
	 *
	 * @return The weight of the task.
	 */
	int getWeight();

	/**
	 * Returns the description of the task.
	 *
	 * @return The description of the task.
	 */
	String getDescription();

	/**
	 * Returns the codec of the task.
	 *
	 * @return The codec of the task.
	 */
	Codec<? extends Task> codec();

	/**
	 * Returns the type of the task.
	 *
	 * @return The type of the task.
	 */
	Codec<?> conditionalCodec();
}
