package com.nowcent.mc.component.locationcache.logic

import net.minecraft.core.BlockPos

/**
 * @author orangeboyChen
 * @version 1.0
 * @date 2026/1/2 22:41
 */
object LocationCacheManager {
    private val privateLocation = mutableMapOf<String, LRUCache<String, BlockPos>>()
    private val publicLocation = LRUCache<String, BlockPos>(100)

    fun putPublicLocation(key: String, location: BlockPos) {
        publicLocation[key] = location
    }

    fun putPrivateLocation(user: String, key: String, location: BlockPos) {
        privateLocation.getOrPut(user) { LRUCache(100) }[key] = location
    }

    fun getLocation(user: String, key: String): BlockPos? {
        return (privateLocation[user].orEmpty() + publicLocation)[key]
    }

    fun removePrivateLocation(user: String, key: String): BlockPos? {
        return privateLocation.getOrPut(user) { LRUCache(100) }.remove(key)
    }

    fun removePublicLocation(key: String): BlockPos? {
        return publicLocation.remove(key)
    }
}
