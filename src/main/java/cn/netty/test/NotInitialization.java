package cn.netty.test;

/**
 * Created by liu_penghui on 2018/3/5.
 */
public class NotInitialization {

    static int data = 30;

    static class Inner {
        void msg() {
            System.out.println("data is :" + data);
        }
    }
    public static void main(String [] args) {
        System.out.println(SubClass.value);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        NotInitialization.Inner inner = new NotInitialization.Inner();
        inner.msg();
    }
}
