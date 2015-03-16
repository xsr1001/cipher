package com.cdcoder.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 缓存助手
 */
public class CacheManager {

	private final static Logger log = LoggerFactory.getLogger(CacheManager.class);
	private static CacheProvider provider;

	static {
		initCacheProvider("com.ketayao.fensy.cache.EhCacheProvider");
	}

	private static void initCacheProvider(String prvName) {
		try {
			CacheManager.provider = (CacheProvider) Class.forName(prvName).newInstance();
			CacheManager.provider.start();
			log.info("Using CacheProvider : " + provider.getClass().getName());
		} catch (Exception e) {
			log.error("Unabled to initialize cache provider:" + prvName + ", using ehcache default.", e);
			CacheManager.provider = new EhCacheProvider();
		}
	}

	private final static Cache getCache(String cacheName, boolean autoCreate) {
		if (provider == null) {
			provider = new EhCacheProvider();
		}
		return provider.buildCache(cacheName, autoCreate);
	}

	/**
	 * 获取缓存中的数据
	 * 
	 * @param name
	 * @param key
	 * @return
	 */
	public final static Object get(String name, Serializable key) {
		if (name != null && key != null)
			return getCache(name, true).get(key);
		return null;
	}

	/**
	 * 获取缓存中的数据
	 * 
	 * @param <T>
	 * @param resultClass
	 * @param name
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static <T> T get(Class<T> resultClass, String name,
			Serializable key) {
		if (name != null && key != null)
			return (T) getCache(name, true).get(key);
		return null;
	}

	/**
	 * 写入缓存
	 * 
	 * @param name
	 * @param key
	 * @param value
	 */
	public final static void put(String name, Serializable key,
			Serializable value) {
		if (name != null && key != null && value != null)
			getCache(name, true).put(key, value);
	}

	/**
	 * 清除缓冲中的某个数据
	 * 
	 * @param name
	 * @param key
	 */
	public final static void evict(String name, Serializable key) {
		if (name != null && key != null)
			getCache(name, true).remove(key);
	}

	/**
	 * 清除缓冲中的某个数据
	 * 
	 * @param name
	 * @param key
	 */
	public final static void justEvict(String name, Serializable key) {
		if (name != null && key != null) {
			Cache cache = getCache(name, false);
			if (cache != null)
				cache.remove(key);
		}
	}

	/**
	 * 清楚所有的缓存 描述
	 * 
	 * @param name
	 */
	public final static void clear(String name) {
		if (name != null)
			getCache(name, true).clear();
	}

}
