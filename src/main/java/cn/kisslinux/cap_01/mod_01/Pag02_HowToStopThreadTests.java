package cn.kisslinux.cap_01.mod_01;

import org.junit.Test;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-06 19:59
 */
public class Pag02_HowToStopThreadTests {

    @Test
    public void testUsingException() throws InterruptedException {
        Thread thread = new Thread(new MyThread01());
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
    }

    private class MyThread01 implements Runnable {
        @Override
        public void run() {
            try {
                for (int i=0; i<500000; i++) {
                    Thread.sleep(50);
                    System.out.println(i);
                    if (Thread.interrupted()) {
                        System.out.println("已经是停止状态了！我要退出了！");
                        throw new InterruptedException();
                    }
                }
                System.out.println("我在for下面");
            } catch (InterruptedException e) {
                System.out.println("进入MyThread01类run方法中的catch了！");
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testInterruptDuringSleeping() throws InterruptedException {
        System.out.println("start");
        Thread thread = new Thread(new MyRunnable02());
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
        System.out.println("end");
    }

    public class MyRunnable02 implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("Run begin.");
                Thread.sleep(200000);
                System.out.println("Run end.");
            } catch (InterruptedException e) {
                System.out.println("在沉睡中被停止！进入Catch." + Thread.interrupted());
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testUsingReturn() throws InterruptedException {
        System.out.println("start");
        Thread thread = new Thread(new MyRunnable03());
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
        System.out.println("end");
    }

    private class MyRunnable03 implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (Thread.interrupted()) {
                    System.out.println("停止了！");
                    return;
                }
                System.out.println("time: " + System.currentTimeMillis());
            }
        }
    }
}
