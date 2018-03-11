package cn.kisslinux.cap_03.mod_01;

import org.junit.Test;


/**
 * 即使线程发出notify消息，wait状态的线程也不会立刻唤醒，要等notify线程执行完成之后，wait状态的线程才会被唤醒
 * @author 庄壮壮 Administrator
 * @since 2018-03-10 17:33
 */
public class Pag01_Wait_Notify {

    @Test
    public void testClient() throws InterruptedException {
        Object lock = new Object();
        Thread a = new ThreadA(lock);
        a.start();

        Thread.sleep(500);

        Thread b = new ThreadB(lock);
        b.start();

        Thread.sleep(12000);

    }



    private class ThreadA extends Thread {
        private final Object lock;

        public ThreadA(Object lock) {
            super();
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                synchronized (lock) {
                    if (MyList.size() != 5) {
                        System.out.println("wait begin: " + System.currentTimeMillis());
                        lock.wait();
                        System.out.println("wait end: " + System.currentTimeMillis());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class ThreadB extends Thread {
        private final Object lock;

        public ThreadB(Object lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            super.run();
            try {
                synchronized (lock) {
                    for (int i=0; i<10; i++) {
                        MyList.add();
                        System.out.println("添加了" + (i + 1) + "个元素！");
                        if (MyList.size() == 5) {
                            lock.notify();
                            System.out.println("已经发出通知！");
                        }
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
