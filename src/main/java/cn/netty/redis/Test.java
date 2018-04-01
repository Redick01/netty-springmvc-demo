package cn.netty.redis;

/**
 * Created by liu_penghui on 2018/4/1.
 */
public class Test {
    public static void main(String[] args) {
        PoolInfo service = new PoolInfo();
        for (int i = 0; i < 50; i++) {
            ThreadA threadA = new ThreadA(service);
            threadA.start();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
