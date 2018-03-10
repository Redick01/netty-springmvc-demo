package cn.netty.test;

/**
 * Created by liu_penghui on 2018/3/5.
 */
public class SuperClass extends SSClass {
    static {
        System.out.println("SuperClass init");
    }
    public static int value = 123;

    public SuperClass() {
        System.out.println("init SuperClass");
    }
}
