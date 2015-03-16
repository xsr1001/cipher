package com.cdcoder.core.db;

import com.cdcoder.core.cache.CacheManager;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: cipher
 * @Package com.cdcoder.core.db
 * @Description: 内置数据库基类
 * @date 2015/3/16 22:09
 */
public class BaseEntity extends BaseBean {

    private static final long serialVersionUID = 161331467465531285L;

    private long id;

    private String tableName;

    public Object getCache(Serializable key) {
        return CacheManager.get(getCacheRegion(), key);
    }

    public void putCache(Serializable key, Serializable value) {
        CacheManager.put(getCacheRegion(), key, value);
    }

    public void evictCache(Serializable key) {
        CacheManager.evict(getCacheRegion(), key);
    }

    public void evictCache(boolean dr) {
        if (dr && isCachedByID()) {
            evictCache(getId());
        }
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the tableName
     */
    protected String getTableName() {
        if (tableName == null)
            tableName = DBManager.prefixTableName + Inflector.getInstance().tableize(getClass());
        return tableName;
    }

    /**
     * 是否根据ID缓存对象，此方法对Get(long id)有效
     *
     * @return
     */
    protected boolean isCachedByID() {
        return true;
    }

    /**
     * 返回对象对应的ID缓存区域名
     *
     * @return
     */
    protected String getCacheRegion() {
        return this.getClass().getSimpleName();
    }

    /**
     * 返回对象对应的查询缓存区域名
     *
     * @return
     */
    protected String getQueryCacheRegion() {
        return getCacheRegion();
    }

    /**
     * 列出要插入到数据库的域集合，子类可以覆盖此方法
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> listInsertableFields() {
        try {
            Map<String, Object> props = BeanUtils.describe(this);
            if (getId() <= 0)
                props.remove("id");
            props.remove("class");

            Map<String, Object> priMap = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : props.entrySet()) {
                Field field = null;
                try {
                    // 有些方法可能并没有对应的属性，如果存在则跳过。
                    field = this.getClass().getDeclaredField(entry.getKey());
                } catch (NoSuchFieldException e) {
                    continue;
                }

                if (QueryHelper.isPrimitive(field.getType())) {
                    priMap.put(entry.getKey(), entry.getValue());
                }
            }

            return priMap;
        } catch (Exception e) {
            throw new RuntimeException("Exception when Fetching fields of " + this, e);
        }
    }