package cn.netty.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantUtil {

    public static volatile int i = 0;

    public static void add() {
        Lock lock = new ReentrantLock();
        lock.lock();
        i++;
        lock.unlock();
    }

    public static void main(String [] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                public void run() {
                    add();
                }
            }).start();
        }
        Thread.sleep(5000);
        System.out.println(i);
    }
}
