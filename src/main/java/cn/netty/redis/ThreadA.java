package cn.netty.redis;

/**
 * Created by liu_penghui on 2018/3/30.
 */
public class ThreadA extends Thread {
    private PoolInfo service;

    public ThreadA(PoolInfo service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.seckill();
    }
}
