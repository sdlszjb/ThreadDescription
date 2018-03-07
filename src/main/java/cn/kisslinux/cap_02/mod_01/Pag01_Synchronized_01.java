package cn.kisslinux.cap_02.mod_01;

import org.junit.Test;

/**
 * 测试
 * 类中只有 都加了synchronized关键字的方法才会同步
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-07 19:59
 */
public class Pag01_Synchronized_01 {

    @Test
    public void testClient() throws InterruptedException {
        MyObject object = new MyObject();

        Thread thread1 = new Thread(new MyRunnable01(object));
        Thread thread2 = new Thread(new MyRunnable01(object));
        Thread thread3 = new Thread(new MyRunnable02(object));

        thread1.setName("thread1");
        thread2.setName("thread2");
        thread3.setName("thread3");

        thread1.start();
        thread2.start();
        thread3.start();

        Thread.sleep(20000);
    }

    private class MyObject {
        synchronized public void methodA() {
            try {
                System.out.println(Thread.currentThread().getName() + "begin methodA time=" + System.currentTimeMillis());
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + "end time=" + System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void methodB() {
            try {
                System.out.println(Thread.currentThread().getName() + "begin methodB time=" + System.currentTimeMillis());
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + "end time=" + System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyRunnable01 implements Runnable {

        private MyObject object;

        public MyRunnable01(MyObject object) {
            this.object = object;
        }

        @Override
        public void run() {
            object.methodA();
        }
    }

    private class MyRunnable02 implements Runnable {
        private MyObject object;

        public MyRunnable02(MyObject object) {
            this.object = object;
        }

        @Override
        public void run() {
            object.methodB();
        }
    }
}
