package cn.kisslinux.cap_02.mod_01;

import org.junit.Test;

/**
 *
 * synchronized同步代码块
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-07 19:59
 */
public class Pag01_Synchronized_02 {

    @Test
    public void testClient() throws InterruptedException {
        Service service = new Service();
        Thread a = new Thread(new MyRunnable(service));
        Thread b = new Thread(new MyRunnable(service));

        a.setName("a");
        b.setName("b");

        a.start();
        b.start();

        Thread.sleep(5000);

    }

    private class Service {
        public void serviceMethod() {
            try {
                synchronized (this) {
                    System.out.println(Thread.currentThread().getName() + " begin time: " + System.currentTimeMillis());
                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName() + " end time: " + System.currentTimeMillis());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyRunnable implements Runnable {

        Service service;

        public MyRunnable(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            service.serviceMethod();
        }
    }
}
