package cn.netty.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by liu_penghui on 2017/11/14.
 */
@Service
public class RedisUtil {

    @Autowired
    @Qualifier("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("redisTemplate")
    protected RedisTemplate<Serializable, Serializable> redisTemplateSerializable;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     * @param key
     * @param value
     * @return 缓存对象
     */
    public void setCacheObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取缓存对象
     * @param key
     * @return 缓存键对应的值
     */
    public Object getCacheObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 缓存List对象
     * @param key
     * @param dataList
     * @return 缓存的对象
     */
    public Object setCacheList(String key, List<Object> dataList) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        if(dataList != null) {
            int size = dataList.size();
            for(int i = 0; i < size; i++){
                listOperations.rightPush(key, dataList.get(i));
            }
        }
        return listOperations;
    }

    /**
     * 获取缓存List
     * @param key
     * @return 对应key的缓存List的数据
     */
    public List<Object> getCacheList(String key) {
        List<Object> dataList = new ArrayList<Object>();
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        long size = listOperations.size(key);
        for(int i = 0; i < size; i++) {
            dataList.add(listOperations.leftPop(key));
        }
        return dataList;
    }

    /**
     * 获取缓存List对象
     * @Title range
     * @param key
     * @param start
     * @param end
     * @return List<T>
     */
    public List<Object> range(String key, long start, long end) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        return listOperations.range(key, start, end);
    }

    /**
     * 获取缓存List键为key的集合长度
     * @param key
     * @return
     */
    public Long listSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 覆盖List中指定位置的值
     * @param key
     * @param index
     * @param obj
     *
     */
    public void listSet(String key, int index, Object obj) {
        redisTemplate.opsForList().set(key, index, obj);
    }

    /**
     * 向List尾部追加记录
     * @param key
     * @param obj
     * @return 记录总数
     */
    public long leftPush(String key, Object obj) {
        return redisTemplate.opsForList().leftPush(key, obj);
    }

    /**
     * 向List头部追加记录
     * @param key
     * @param obj
     * @return 记录总数
     */
    public long rightPush(String key, Object obj) {
        return redisTemplate.opsForList().rightPush(key, obj);
    }

    /**
     * 保留start和end之间的记录
     * @param key
     * @param start
     * @param end
     */
    public void trim(String key, int start, int end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 删除List中i条记录，被删除记录值为value
     * @param key
     * @param i
     * @param obj 要匹配的值
     * @return 删除List中的记录数
     */
    public long remove(String key, long i, Object obj) {
        return redisTemplate.opsForList().remove(key, i, obj);
    }

    /**
     * 缓存Set
     * @param key
     * @param dataSet
     * @return 缓存数据的对象
     */
    public BoundSetOperations<String, Object> setCacheSet(String key, Set<Object> dataSet) {
        BoundSetOperations<String, Object> boundSetOperations = redisTemplate.boundSetOps(key);
        Iterator<Object> iterator = dataSet.iterator();
        while (iterator.hasNext()) {
            boundSetOperations.add(iterator.next());
        }
        return boundSetOperations;
    }

    /**
     * 获取缓存的Set
     * @param key
     * @return
     */
    public Set<Object> getCacheSet(String key) {
        Set<Object> dataSet = new HashSet<Object>();
        BoundSetOperations<String, Object> boundSetOperations = redisTemplate.boundSetOps(key);
        long size = boundSetOperations.size();
        for(int i = 0; i < size; i++){
            dataSet.add(boundSetOperations.pop());
        }
        return dataSet;
    }

    /**
     * 缓存Map
     * @param key
     * @param dataMap
     * @return
     */
    public int setCacheMap(String key, Map<String, Object> dataMap) {
        if(null != dataMap) {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            for(Map.Entry<String, Object> entry : dataMap.entrySet()) {
                if (hashOperations != null) {
                    hashOperations.put(key, entry.getKey(), entry.getValue());
                } else {
                    return 0;
                }

            }
        } else {
            return 0;
        }
        return dataMap.size();
    }

    /**
     * 获取缓存的Map
     * @param key
     * @return
     */
    public Map<Object, Object> getCacheMap(String key) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        return map;
    }

    /**
     * 从Hash中删除指定存储
     * @param key
     */
    public void deleteMap(String key) {
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.opsForHash().delete(key);
    }

    /**
     * 设置过期时间
     * @param key
     * @param time
     * @param unit
     * @return
     */
    public boolean expire(String key, long time, TimeUnit unit) {
        return redisTemplate.expire(key, time, unit);
    }

    /**
     * 自增加
     * @param key
     * @param step
     * @return
     */
    public long increment(String key, long step) {
        return redisTemplate.opsForValue().increment(key, step);
    }
}
