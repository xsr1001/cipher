package com.cdcoder.core.cache;

import java.util.Collection;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: cipher
 * @Package com.cdcoder.core.cache
 * @Description: 缓存管理器接口
 * @date 2015/3/11 23:22
 */
public interface CacheManager {

    /**
     * Return the cache associated with the given name.
     * @param name the cache identifier (must not be {@code null})
     * @return the associated cache, or {@code null} if none found
     */
    Cache getCache(String name);

    /**
     * Return a collection of the cache names known by this manager.
     * @return the names of all caches known by the cache manager
     */
    Collection<String> getCacheNames();
}
