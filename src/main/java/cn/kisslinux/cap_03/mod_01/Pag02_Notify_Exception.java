package cn.kisslinux.cap_03.mod_01;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * wait之后，线程就释放了对象锁，
 * 另外一个线程拿到锁，并进入wait状态。
 *
 * 当notifyAll之后，两者同时进入线程，因此产生逻辑错误。
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-11 13:12
 */
public class Pag02_Notify_Exception {

    @Test
    public void testClient() throws InterruptedException {
        Object lock = new Object();

        Add add = new Add(lock);
        Subtract subtract = new Subtract(lock);

        ThreadAdd threadAdd = new ThreadAdd(add);
        ThreadSub threadSub1 = new ThreadSub(subtract);
        ThreadSub threadSub2 = new ThreadSub(subtract);

        threadAdd.setName("threadAdd");
        threadSub1.setName("threadSub1");
        threadSub2.setName("threadSub2");

        threadSub1.start();
        threadSub2.start();

        Thread.sleep(1000);

        threadAdd.start();

        Thread.sleep(10000);
    }
}

class Add {
    private Object lock;

    public Add(Object lock) {
        this.lock = lock;
    }

    public void add() {
        synchronized (lock) {
            ValueObject.add("anyString");
            lock.notifyAll();
        }
    }
}
class Subtract {
    private Object lock;

    public Subtract(Object lock) {
        this.lock = lock;
    }

    public void substract() {
        try {
            synchronized (lock) {
                if (ValueObject.size() == 0) {
                    System.out.println(Thread.currentThread().getName() + " wait begin: " + System.currentTimeMillis());
                    lock.wait();
                    System.out.println(Thread.currentThread().getName() + " wait end: " + System.currentTimeMillis());
                }

                ValueObject.del(0);
                System.out.println(Thread.currentThread().getName() + " list size: " + ValueObject.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ThreadAdd extends Thread {
    private Add add;

    public ThreadAdd(Add add) {
        this.add = add;
    }

    @Override
    public void run() {
        add.add();
    }
}

class ThreadSub extends Thread {
    private Subtract subtract;

    public ThreadSub(Subtract subtract) {
        this.subtract = subtract;
    }

    @Override
    public void run() {
        subtract.substract();
    }
}

class ValueObject {
    private static List<String> data = new ArrayList<>();

    public static void add(String string) {
        data.add(string);
    }

    public static void  del(int index) {
        data.remove(index);
    }

    public static int size() {
        return data.size();
    }
}

