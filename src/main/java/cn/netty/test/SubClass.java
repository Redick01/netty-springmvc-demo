package cn.netty.test;

/**
 * Created by liu_penghui on 2018/3/5.
 */
public class SubClass extends SuperClass {
    static {
        System.out.println("SubClass init");
    }
    static int a;

    public SubClass() {
        System.out.println("init SubClass");
    }
}
