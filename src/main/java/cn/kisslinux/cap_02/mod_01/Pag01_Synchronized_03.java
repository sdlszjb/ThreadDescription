package cn.kisslinux.cap_02.mod_01;

import org.junit.Test;

/**
 *
 * synchronized同步 静态方法
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-07 19:59
 */
public class Pag01_Synchronized_03 {

    @Test
    public void testClient() throws InterruptedException {
        Thread thread1 = new Thread(new MyRunnable1());
        Thread thread2 = new Thread(new MyRunnable2());
        Thread thread3 = new Thread(new MyRunnable3(new Pag01_Static_Class()));

        thread1.setName("thread1");
        thread2.setName("thread2");
        thread3.setName("thread3");

        thread1.start();
        thread2.start();
        thread3.start();

        Thread.sleep(7000);
    }

    private class MyRunnable1 implements Runnable {

        @Override
        public void run() {
            Pag01_Static_Class.staticMethod1();
        }
    }

    private class MyRunnable2 implements Runnable {
        @Override
        public void run() {
            Pag01_Static_Class.staticMethod2();
        }
    }

    private class MyRunnable3 implements Runnable {

        Pag01_Static_Class cls;

        public MyRunnable3(Pag01_Static_Class cls) {
            this.cls = cls;
        }

        @Override
        public void run() {
            cls.method3();
        }
    }
}
