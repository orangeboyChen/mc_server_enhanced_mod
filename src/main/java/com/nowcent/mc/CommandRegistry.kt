package com.nowcent.mc

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.nowcent.mc.component.locationcache.LocationCacheCommandRegister
import net.minecraft.commands.CommandSourceStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent

/**
 * @author orangeboyChen
 * @version 1.0
 * @date 2026/1/2 22:52
 */
interface ComponentCommandRegister {
    fun buildCommand(): LiteralArgumentBuilder<CommandSourceStack>
}

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.DEDICATED_SERVER])
object CommandRegistry {
    private val componentCommand = listOf<ComponentCommandRegister>(
        LocationCacheCommandRegister()
    )

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        registerCommand(event.dispatcher)
    }

    private fun registerCommand(d: CommandDispatcher<CommandSourceStack>) {
        componentCommand.forEach { d.register(it.buildCommand()) }
    }
}
