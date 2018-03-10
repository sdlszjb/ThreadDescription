package cn.kisslinux.cap_02.mod_02;

import org.junit.Test;

/**
 * while循环此时是停止不掉的，
 * 因为在thread内部，变量读取的是线程内部的堆栈。
 *
 * 使用volatile关键字后，告诉线程，强制从公共堆栈中读取变量的值。因此，此时可以停止死循环。
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-10 10:01
 */
public class Pag01_Volatile {
    @Test
    public void withoutVolatileTest() throws InterruptedException {
        RunThreadWithoutVolatile threadWithoutVolatile = new RunThreadWithoutVolatile();
        threadWithoutVolatile.start();
        Thread.sleep(1000);
        threadWithoutVolatile.setRunning(false);
        System.out.println("已经赋值为" + threadWithoutVolatile.isRunning());

        Thread.sleep(10000);
    }

    private class RunThreadWithoutVolatile extends Thread {
        private boolean running = true;

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            System.out.println("进入Run...");
            while (isRunning()) {
            }
            System.out.println("线程被停止了...");
        }
    }

    @Test
    public void withVolatileTest() throws InterruptedException {
        RunThreadWithVolatile threadWithVolatile = new RunThreadWithVolatile();
        threadWithVolatile.start();
        Thread.sleep(1000);
        threadWithVolatile.setRunning(false);
        System.out.println("已经赋值为" + threadWithVolatile.isRunning());

        Thread.sleep(10000);
    }

    private class RunThreadWithVolatile extends Thread {
        volatile private boolean running = true;

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            System.out.println("进入Run...");
            while (isRunning()) {
            }
            System.out.println("线程被停止了...");
        }
    }
}
