# 线程间通信

**技术要点**
- 使用wait/notify实现线程间的通信
- 生产者/消费者模式的实现
- 方法join的使用
- ThreadLocal类的使用

## 等待/通知机制

1. **等待通知机制的实现**
- What is wait()
wait()方法是Object类的方法，将当前线程置入“预执行队列”中，并且在wait()所在的代码行处停止执行，知道接到通知或被中断为止。在调用wait()之前，线程必须获得该对象的对象级别锁，即，只能在同步方法或者同步块中调用wait()方法。执行wait()方法后，当前线程释放锁。如果在wait()时，没有持有适当的锁，则抛出IllegalMonitorStateException。
- what is notify()
notify()方法也要在同步方法或同步块中调用，即，在调用前必须取得该对象的对象级别锁，如果没有持有适当的锁，也会抛出IllegalMonitorStateException。该方法用来通知那些可能等待该对象的对象锁的其他线程，如果有多个线程等待，则由线程规划器随机挑出其中一个呈wait状态的线程，对其发出notify通知。在执行notify()方法后，线程并不会马上释放该对象锁，呈wait状态的线程也并不会马上获取该对象锁，要等到执行notify()方法的线程将程序执行完，也就是退出synchronized代码后，当前线程才会释放锁，而呈wait状态的线程才可以取得该对象锁。
> 用一句话总结：wait使线程停止运行，notify使停止的线程继续运行
```java
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
```
- 当线程呈wait状态时，调用线程对象的interrupt()方法会出现InterruptException。
- 可以使用notifyAll()方法，唤醒所有处于wait状态的线程。
- 使用wait(long)方法，可以自动唤醒。
- notify过早，会出现逻辑混乱，导致wait线程永远不会醒来。
```java
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
```