package cn.netty.redis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;

/**
 * Redis实现分布式锁
 * Created by liu_penghui on 2018/3/30.
 */
//@Component("RedisLock")
public class RedisLock {

    private static final Logger Log = Logger.getLogger(RedisLock.class);

    private final JedisPool jedisPool;

    public RedisLock(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    /*@Autowired
    private JedisPool jedisPool;*/

    /**
     *
     * @param lockName 锁的key
     * @param acquireTimeout 获取锁超时时间
     * @param timeOut 超时时间
     * @return 锁标记
     */
    public String lockWithTimeout(String lockName, long acquireTimeout, long timeOut) {
        Jedis conn = null;
        conn = jedisPool.getResource();
        //锁标记
        String retIdentifier = null;
        //生成随机值
        String value = UUID.randomUUID().toString();
        //锁名，即key
        String key = "lock:" + lockName;
        //超时时间
        int lockExpire = (int) (timeOut / 1000);
        //获取锁的超时时间,超过这个时间自动释放所
        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            if (conn.setnx(key, value) == 1) {//如果设置成功则设置超时时间
                conn.expire(key, lockExpire);
                //返回value，用于释放锁时间
                retIdentifier = value;
                return retIdentifier;
            }
            //返回-1代表没有设置超时时间为key设置超时时间
            if (conn.ttl(key) == -1) {
                conn.expire(key, lockExpire);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            conn.close();
        }
        return retIdentifier;
    }

    /**
     * 释放锁
     * @param key
     * @param value
     * @return
     */
    public boolean releaseLock(String key, String value) {
        Jedis conn = null;
        String lockKey = "lock:" + key;
        conn = jedisPool.getResource();
        boolean flag = false;
        while (true) {
            //监视lock,准备开始事务
            conn.watch(lockKey);
            //通过前面返回的value值判断是不是该锁，若是则删除，释放锁
            if (value.equals(conn.get(lockKey))) {
                Transaction transaction = conn.multi();
                transaction.del(lockKey);
                List<Object> results = transaction.exec();
                if (results == null) {
                    continue;
                }
                flag = true;
            }
            conn.unwatch();
            break;
        }
        if (conn != null) {
            conn.close();
        }
        return flag;
    }
}
