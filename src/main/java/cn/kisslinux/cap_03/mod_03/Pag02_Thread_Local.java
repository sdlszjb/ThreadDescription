package cn.kisslinux.cap_03.mod_03;

import org.junit.Test;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-12 21:35
 */
public class Pag02_Thread_Local {
    @Test
    public void testClient() throws InterruptedException {
        Thread a = new ThreadA();
        a.start();
        Thread.sleep(2000);
        Thread b = new ThreadB();
        b.start();

        Thread.sleep(10000);

    }
}

class Tools {
    public static ThreadLocal<Integer> t1 = new ThreadLocal<>();
}

class ThreadA extends Thread {
    @Override
    public void run() {
        System.out.println("ThreadA set 100.");
        Tools.t1.set(100);
        System.out.println("ThreadA red " + Tools.t1.get());

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ThreadB extends Thread {
    @Override
    public void run() {
        System.out.println("ThreadB red " + Tools.t1.get());
    }
}
