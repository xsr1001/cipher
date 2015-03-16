package com.cdcoder.core.cache;


import com.cdcoder.core.excption.CacheException;

import java.util.List;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: cipher
 * @Package com.cdcoder.core.cache
 * @Description: 缓存 异常有实现抛出 接口管理异常
 * @date 2015/3/11 23:12
 */
public interface Cache {

    /**
     * Get an item from the cache, nontransactionally
     * @param key
     * @return the cached object or <tt>null</tt>
     * @throws CacheException
     */
    public Object get(Object key) throws CacheException;

    /**
     * Add an item to the cache, nontransactionally, with
     * failfast semantics
     * @param key
     * @param value
     * @throws CacheException
     */
    public void put(Object key, Object value) throws CacheException;

    /**
     * Add an item to the cache
     * @param key
     * @param value
     * @throws CacheException
     */
    public void update(Object key, Object value) throws CacheException;

    @SuppressWarnings("rawtypes")
    public List keys() throws CacheException ;

    /**
     * Remove an item from the cache
     */
    public void remove(Object key) throws CacheException;

    /**
     * Clear the cache
     */
    public void clear() throws CacheException;

    /**
     * Clean up
     */
    public void destroy() throws CacheException;
}
