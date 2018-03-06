package cn.kisslinux.cap_01.mod_01;

import org.junit.Test;

/**
 * 如何使用多线程
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-06 19:36
 */
public class Pag01_HowToUseThreadTests {

    @Test
    public void testThreadUsingThread() throws InterruptedException {
        Thread thread = new MyThread();
        thread.start();
        for (int i=0; i<100; i++) {
            System.out.println("MainThread---" + i);
        }
        Thread.sleep(1000);
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            for (int i=0; i<100; i++) {
                System.out.println("My  Thread---" + i);
            }
        }
    }


    @Test
    public void testThreadUsingRunnable() throws InterruptedException {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
        for (int i=0; i<100; i++) {
            System.out.println("MainThread---" + i);
        }
        Thread.sleep(1000);
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            for (int i=0; i<100; i++) {
                System.out.println("My  Thread---" + i);
            }
        }
    }
}
