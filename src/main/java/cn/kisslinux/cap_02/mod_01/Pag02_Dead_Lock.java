package cn.kisslinux.cap_02.mod_01;

import org.junit.Test;

/**
 * 测试 死锁
 * @author 庄壮壮 Administrator
 * @since 2018-03-07 21:55
 */
public class Pag02_Dead_Lock {

    @Test
    public void testClient() throws InterruptedException {
        MyRunnable runnable = new MyRunnable();

        runnable.setFlag(true);
        Thread thread1 = new Thread(runnable);

        thread1.setName("thread1");

        thread1.start();
        Thread.sleep(500);



        runnable.setFlag(false);
        Thread thread2 = new Thread(runnable);
        thread2.setName("thread2");
        thread2.start();

        Thread.sleep(100000);


    }

    private class MyRunnable implements Runnable {

        private Object lock1 = new Object();
        private Object lock2 = new Object();

        private boolean flag = false;

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run() {
            if (flag) {
                synchronized (lock1) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " lock1 begin at: " + System.currentTimeMillis());
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName() + " lock1 waiting for lock2 at: " + System.currentTimeMillis());
                        synchronized (lock2) {
                            System.out.println(Thread.currentThread().getName() + " lock1 thread get lock2");
                        }
                        System.out.println(Thread.currentThread().getName() + " lock1 end at: " + System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                synchronized (lock2) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " lock2 begin at: " + System.currentTimeMillis());
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName() + " lock2 waiting for lock1 at: " + System.currentTimeMillis());
                        synchronized (lock1) {
                            System.out.println(Thread.currentThread().getName() + " lock2 thread get lock1");
                        }
                        System.out.println(Thread.currentThread().getName() + " lock2 end at: " + System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
