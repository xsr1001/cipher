package com.cdcoder.core.cache;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: 缓存注册接口
 * @Package com.cdcoder.core.cache
 * @Description: ${todo}
 * @date 2015/3/11 23:25
 */
public interface CacheProvider  {
    /**
     * Configure the cache
     *
     * @param regionName the name of the cache region
     * @param autoCreate autoCreate settings
     *
     */
    public Cache buildCache(String regionName, boolean autoCreate) ;

    /**
     * Callback to perform any necessary initialization of the underlying cache implementation
     * during SessionFactory construction.
     *
     *
     */
    public void start();

    /**
     * Callback to perform any necessary cleanup of the underlying cache implementation
     * during SessionFactory.close().
     */
    public void stop();
}
