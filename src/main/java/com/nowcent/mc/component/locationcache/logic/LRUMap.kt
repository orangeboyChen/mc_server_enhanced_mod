package com.nowcent.mc.component.locationcache.logic

class LRUCache<K, V>(private val capacity: Int = 0): Map<K, V> {
    private val cache = LinkedHashMap<K, V>(capacity, 0.75F)

    override fun get(key: K): V? {
        if (!containsKey(key)) return null

        val value = remove(key)!!
        cache[key] = value
        return value
    }

    fun clear() = cache.clear()

    fun remove(key: K) = cache.remove(key)

    fun put(key: K, value: V) {
        if (cache.containsKey(key)) {
            cache.remove(key)
        } else if (cache.size == capacity) {
            cache.remove(cache.keys.first())
        }
        cache[key] = value
    }

    override val size: Int
        get() = cache.size

    override fun isEmpty() = cache.isEmpty()

    override fun containsKey(key: K) = cache.containsKey(key)

    override fun containsValue(value: V) = cache.containsValue(value)

    operator fun set(key: K, value: V) = put(key, value)

    fun putAll(from: Map<out K, V>) = cache.putAll(from)

    override val keys: MutableSet<K>
        get() = cache.keys

    override val values: MutableCollection<V>
        get() = cache.values

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = cache.entries
}
