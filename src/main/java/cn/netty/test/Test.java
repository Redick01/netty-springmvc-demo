package cn.netty.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * redis 限流思路
 * Created by liu_penghui on 2018/3/2.
 */
public class Test implements Runnable {

    private static final String key = "string:liupenghui";
    private volatile long count = 0;
    ApplicationContext ctx;
    RedisTemplate redisTemplate;

    public Test() {
        ctx = new ClassPathXmlApplicationContext("redis-test.xml");
        redisTemplate = (RedisTemplate) ctx.getBean("redisTemplate");
    }

    public static void main(String [] args) throws InterruptedException {
        Test test = new Test();
        Thread t1 = new Thread(test, "SyncThread1");
        Thread t2 = new Thread(test, "SyncThread2");
        t1.start();
        t2.start();
    }



    public void run() {
        synchronized(this) {
            while (count < 10) {

                count = redisTemplate.opsForValue().increment(key, (long)1);
                System.out.println("访问人数" + count + "访问时间：" + System.currentTimeMillis());
                redisTemplate.expire(key, (long)10, TimeUnit.SECONDS);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("限流：" + count);
        }
    }
}
