package com.mrbysco.tinytasks;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(TinyTasksMod.MOD_ID)
public class TinyTasksMod {
	public static final String MOD_ID = "tinytasks";
	public static final Logger LOGGER = LogUtils.getLogger();

	public TinyTasksMod(IEventBus eventBus, Dist dist) {
		eventBus.addListener(TaskRegistry::onNewRegistry);

		NeoForge.EVENT_BUS.addListener(TaskRegistry::onServerStarted);
	}

	/**
	 * Creates an Identifier with the mod ID as the namespace.
	 *
	 * @param path The path for the resource location, relative to the mod's namespace.
	 * @return An Identifier with the mod ID as the namespace and the provided path.
	 */
	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
