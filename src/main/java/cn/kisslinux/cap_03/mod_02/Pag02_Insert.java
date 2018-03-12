package cn.kisslinux.cap_03.mod_02;

import org.junit.Test;

/**
 * 各5各线程，交替打印
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-12 21:01
 */
public class Pag02_Insert {
    @Test
    public void testClient() throws InterruptedException {
        DBTools dbTools = new DBTools();

        Thread threadA1 = new Thread(new ThreadA(dbTools));
        Thread threadA2 = new Thread(new ThreadA(dbTools));
        Thread threadA3 = new Thread(new ThreadA(dbTools));
        Thread threadA4 = new Thread(new ThreadA(dbTools));
        Thread threadA5 = new Thread(new ThreadA(dbTools));

        Thread threadB1 = new Thread(new ThreadB(dbTools));
        Thread threadB2 = new Thread(new ThreadB(dbTools));
        Thread threadB3 = new Thread(new ThreadB(dbTools));
        Thread threadB4 = new Thread(new ThreadB(dbTools));
        Thread threadB5 = new Thread(new ThreadB(dbTools));

        threadA1.setName("A1");
        threadA2.setName("A2");
        threadA3.setName("A3");
        threadA4.setName("A4");
        threadA5.setName("A5");

        threadB1.setName("B1");
        threadB2.setName("B2");
        threadB3.setName("B3");
        threadB4.setName("B4");
        threadB5.setName("B5");

        threadA1.start();
        threadA2.start();
        threadA3.start();
        threadA4.start();
        threadA5.start();

        threadB1.start();
        threadB2.start();
        threadB3.start();
        threadB4.start();
        threadB5.start();

        Thread.sleep(50000);
    }
}

class DBTools {
    private static final int RUNNING_A = 1;
    private static final int RUNNING_B = 2;

    volatile private int whoIsWaiting = RUNNING_A;

    synchronized void backupA() {
        try {
            while (whoIsWaiting == RUNNING_A) {
                this.wait();
            }
            System.out.println(Thread.currentThread().getName());
            System.out.println("★★★★★");
            whoIsWaiting = RUNNING_A;
            notifyAll();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized void backupB() {
        try {
            while (whoIsWaiting == RUNNING_B) {
                this.wait();
            }
            System.out.println(Thread.currentThread().getName());
            System.out.println("☆☆☆☆☆");
            whoIsWaiting = RUNNING_B;
            notifyAll();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ThreadA implements Runnable{

    DBTools dbTools;

    public ThreadA(DBTools dbTools) {
        this.dbTools = dbTools;
    }

    @Override
    public void run() {
        dbTools.backupA();
    }
}

class ThreadB implements Runnable{

    DBTools dbTools;

    public ThreadB(DBTools dbTools) {
        this.dbTools = dbTools;
    }

    @Override
    public void run() {
        dbTools.backupB();
    }
}
