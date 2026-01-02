package com.nowcent.mc.component.locationcache

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.nowcent.mc.ComponentCommandRegister
import com.nowcent.mc.component.locationcache.logic.LocationCacheManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

/**
 * @author orangeboyChen
 * @version 1.0
 * @date 2026/1/5 00:26
 */
class LocationCacheCommandRegister: ComponentCommandRegister {
    override fun buildCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("location")
            .then(setCommand())
            .then(getCommand())
            .then(removeCommand())
    }

    private fun setCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("set")
            .then(
                Commands.argument("scope", StringArgumentType.word())
                    .suggests { _, builder ->
                        builder.suggest("public")
                        builder.suggest("private")
                        builder.buildFuture()
                    }
                    .then(
                        Commands.argument("key", StringArgumentType.word())
                            .then(
                                Commands.argument("x", IntegerArgumentType.integer())
                                    .then(
                                        Commands.argument("y", IntegerArgumentType.integer())
                                            .then(
                                                Commands.argument("z", IntegerArgumentType.integer())
                                                    .executes { ctx ->
                                                        val source = ctx.source
                                                        val player = source.playerOrException

                                                        val scope =
                                                            StringArgumentType.getString(ctx, "scope")
                                                        val key = StringArgumentType.getString(ctx, "key")
                                                        val x = IntegerArgumentType.getInteger(ctx, "x")
                                                        val y = IntegerArgumentType.getInteger(ctx, "y")
                                                        val z = IntegerArgumentType.getInteger(ctx, "z")

                                                        val pos = BlockPos(x, y, z)

                                                        when (scope) {
                                                            "public" -> {
                                                                LocationCacheManager.putPublicLocation(key, pos)
                                                                source.sendSuccess(
                                                                    { Component.literal("已设置公共位置 [$key] = ($x, $y, $z)") },
                                                                    true
                                                                )
                                                                source.sendSystemMessage(Component.literal("${(source.entity as? Player)?.displayName?.string.orEmpty()}已设置公共位置 [$key] = ($x, $y, $z)"))
                                                            }

                                                            "private" -> {
                                                                LocationCacheManager.putPrivateLocation(
                                                                    player.stringUUID,
                                                                    key,
                                                                    pos
                                                                )
                                                                source.sendSuccess(
                                                                    { Component.literal("已设置你的私有位置 [$key] = ($x, $y, $z)") },
                                                                    false
                                                                )
                                                            }

                                                            else -> {
                                                                source.sendFailure(
                                                                    Component.literal("scope 必须是 public 或 private")
                                                                )
                                                                return@executes 0
                                                            }
                                                        }
                                                        1
                                                    }
                                            )
                                    )
                            )
                    )
            )
    }

    private fun removeCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("remove")
            .then(
                Commands.argument("scope", StringArgumentType.word())
                    .suggests { _, builder ->
                        builder.suggest("public")
                        builder.suggest("private")
                        builder.buildFuture()
                    }
                    .then(
                        Commands.argument("key", StringArgumentType.word())
                            .executes { ctx ->
                                val source = ctx.source
                                val player = source.playerOrException

                                val scope =
                                    StringArgumentType.getString(ctx, "scope")
                                val key = StringArgumentType.getString(ctx, "key")
                                when (scope) {
                                    "public" -> {
                                        val pos = LocationCacheManager.removePublicLocation(key)
                                        if (pos != null) {
                                            source.sendSuccess(
                                                { Component.literal("已删除公共位置 [$key] = (${pos.x}, ${pos.y}, ${pos.z})") },
                                                true
                                            )
                                            source.sendSystemMessage(Component.literal("${(source.entity as? Player)?.displayName?.string.orEmpty()}已删除公共位置 [$key] = (${pos.x}, ${pos.y}, ${pos.z})"))
                                        } else {
                                            source.sendFailure(Component.literal("删除失败！位置不存在"))
                                        }
                                    }

                                    "private" -> {
                                        val pos = LocationCacheManager.removePrivateLocation(
                                            player.stringUUID,
                                            key
                                        )
                                        if (pos != null) {
                                            source.sendSuccess(
                                                { Component.literal("已删除私有位置 [$key]") },
                                                false
                                            )
                                        } else {
                                            source.sendFailure(Component.literal("删除失败！位置不存在"))
                                        }
                                    }

                                    else -> {
                                        source.sendFailure(
                                            Component.literal("scope 必须是 public 或 private")
                                        )
                                        return@executes 0
                                    }
                                }
                                1
                            }
                    )
            )
    }

    private fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("get")
            .then(
                Commands.argument("key", StringArgumentType.word())
                    .executes { ctx ->
                        val player = ctx.source.playerOrException
                        val key = StringArgumentType.getString(ctx, "key")

                        val pos = LocationCacheManager.getLocation(player.stringUUID, key)
                        if (pos == null) {
                            ctx.source.sendFailure(
                                Component.literal("未找到位置: $key")
                            )
                            return@executes 0
                        }

                        ctx.source.sendSuccess(
                            { Component.literal("[$key] = (${pos.x}, ${pos.y}, ${pos.z})") },
                            false
                        )
                        1
                    }
            )
    }
}
