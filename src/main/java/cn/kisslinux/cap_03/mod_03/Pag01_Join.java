package cn.kisslinux.cap_03.mod_03;

import org.junit.Test;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-12 21:27
 */
public class Pag01_Join {
    @Test
    public void testClient() throws InterruptedException {
        System.out.println("程序开始");
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Thread start.");
                Thread.sleep(5000);
                System.out.println("Thread stop.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join();
        System.out.println("程序结束");
    }
}
