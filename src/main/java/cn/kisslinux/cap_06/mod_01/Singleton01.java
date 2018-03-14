package cn.kisslinux.cap_06.mod_01;

import org.junit.Test;

/**
 * 饿汉式
 * @author 庄壮壮 Administrator
 * @since 2018-03-14 20:39
 */
public class Singleton01 {

    private static Singleton01 instance = new Singleton01();

    // 在实际项目中，这个应该为private
    // private Singleton01() {}

    public static Singleton01 getInstance() {
        return instance;
    }

    @Test
    public void testClient() {

        for (int i=0; i<5; i++) {
            Thread thread = new MyThread();
            thread.start();
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println(Singleton01.getInstance().hashCode());
        }
    }
}