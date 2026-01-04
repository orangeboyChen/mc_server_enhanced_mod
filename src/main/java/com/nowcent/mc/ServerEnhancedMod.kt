package com.nowcent.mc

import com.mojang.logging.LogUtils
import com.nowcent.mc.component.livingdeath.LivingDeathManager
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.server.ServerStartingEvent

/**
 * @author orangeboyChen
 * @version 1.0
 * @date 2026/1/2 20:16
 */
const val MOD_ID = "serverenhancedmod"

@Mod(MOD_ID)
class ServerEnhancedMod(modEventBus: IEventBus, modContainer: ModContainer) {

    private val logger = LogUtils.getLogger()

    init {
        NeoForge.EVENT_BUS.register(this)
        NeoForge.EVENT_BUS.register(LivingDeathManager)
        NeoForge.EVENT_BUS.register(CommandRegistry)
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.spec)
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        logger.info("[ServerEnhancedMod] Mod loaded successfully.")
    }
}
