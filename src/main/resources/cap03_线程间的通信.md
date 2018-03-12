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
 * 解决这个问题，就是把 wait 所在的线程中的if换成while。
 * 正常教科书中的案例均是以while为判断符号的。
 * 目的是，即使被唤醒了，仍要再次检查一遍，是否满足被唤醒的条件，以防发生脏数据的现象。
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
2. **生产者/消费者**
```java
package cn.kisslinux.cap_03.mod_01;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-11 17:07
 */
public class Pag04_Producer_consumer_02 {
    @Test
    public void testClient() throws InterruptedException {
        ProductStack stack = new ProductStack();
        Producer producer = new Producer(stack);
        Consumer consumer = new Consumer(stack);

        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(producer);
        Thread t3 = new Thread(producer);
        Thread t4 = new Thread(producer);
        Thread t5 = new Thread(producer);

        t1.setName("t1");
        t2.setName("t2");
        t3.setName("t3");
        t4.setName("t4");
        t5.setName("t5");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        Thread c1 = new Thread(consumer);
        Thread c2 = new Thread(consumer);

        c1.setName("c1");
        c2.setName("c2");

        c1.start();
        c2.start();

        Thread.sleep(50000);
    }
}

/**
 * 产品类
 */
class Product {
    private String id;

    public Product(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}

/**
 * 产品缓冲池
 */
class ProductStack {
    public static final int MAX_SIZE = 10;
    private int index = 0;
    private List<Product> pool = new ArrayList<>();

    synchronized public void push(Product product) {
        try {
            while (index == MAX_SIZE) { // 如果已经达到池的最大容量，那么暂停向里面放入产品
                System.out.println("Push操作中的：" + Thread.currentThread().getName() + "线程处于wait状态！");
                this.wait();
                System.out.println("Push操作中的：" + Thread.currentThread().getName() + "线程处于被唤醒了！");
            }
            pool.add(product);
            index++;
            System.out.println("Add project. Now size is: " + pool.size());
            this.notify(); // 通知其他线程，当前的池已经放入了新的产品
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public Product pop() {
        try {
            while (index == 0) {
                System.out.println("Pop操作中的：" + Thread.currentThread().getName() + "线程处于wait状态！");
                this.wait(); // 如果池中已经木有产品，等待
                System.out.println("Pop操作中的：" + Thread.currentThread().getName() + "线程处于被唤醒了！");
            }
            index--;
            Product product = pool.remove(index);
            pool.remove(product);
            System.out.println("Del project. Now size is: " + pool.size());
            this.notify(); // 如果池中有产品，那么取出一个产品，并告诉其他线程，我已经取出一个产品了。
            return product;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

/**
 * 生产者
 */
class Producer implements Runnable {

    private ProductStack stack;

    public Producer(ProductStack stack) {
        this.stack = stack;
    }

    @Override
    public void run() {
        while (true) {
            Product product = new Product(System.currentTimeMillis() + "");
            System.out.println(Thread.currentThread().getName() + " P: " + product);
            stack.push(product);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 消费者
 */
class Consumer implements Runnable {
    private ProductStack stack;

    public Consumer(ProductStack stack) {
        this.stack = stack;
    }

    @Override
    public void run() {
        while (true) {
            Product product = stack.pop();
            System.out.println(Thread.currentThread().getName() + " C: " + product);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

```
```text
F:\DeveloperTools\SDK\jdk1.8.0_131\bin\java -ea -Didea.test.cyclic.buffer.size=1048576 -javaagent:F:\DeveloperTools\JetBrains\ideaIU\lib\idea_rt.jar=10614:F:\DeveloperTools\JetBrains\ideaIU\bin -Dfile.encoding=UTF-8 -classpath F:\DeveloperTools\JetBrains\ideaIU\lib\idea_rt.jar;F:\DeveloperTools\JetBrains\ideaIU\plugins\junit\lib\junit-rt.jar;F:\DeveloperTools\JetBrains\ideaIU\plugins\junit\lib\junit5-rt.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\charsets.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\deploy.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\access-bridge-64.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\cldrdata.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\dnsns.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\jaccess.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\jfxrt.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\localedata.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\nashorn.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\sunec.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\sunjce_provider.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\sunmscapi.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\sunpkcs11.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\ext\zipfs.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\javaws.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\jce.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\jfr.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\jfxswt.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\jsse.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\management-agent.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\plugin.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\resources.jar;F:\DeveloperTools\SDK\jdk1.8.0_131\jre\lib\rt.jar;D:\IdeaProjects\ThreadDescription\target\classes;C:\Users\Administrator\.m2\repository\junit\junit\4.12\junit-4.12.jar;C:\Users\Administrator\.m2\repository\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 -junit4 cn.kisslinux.cap_03.mod_01.Pag04_Producer_consumer_02,testClient
t1 P: 1520774272309
Add project. Now size is: 1
t5 P: 1520774272309
Add project. Now size is: 2
t4 P: 1520774272310
Add project. Now size is: 3
t2 P: 1520774272310
Add project. Now size is: 4
Del project. Now size is: 3
c1 C: 1520774272310
t3 P: 1520774272310
Add project. Now size is: 4
Del project. Now size is: 3
c2 C: 1520774272310
t3 P: 1520774272510
Add project. Now size is: 4
t2 P: 1520774272510
Add project. Now size is: 5
t1 P: 1520774272513
Add project. Now size is: 6
t5 P: 1520774272513
Add project. Now size is: 7
t4 P: 1520774272513
Add project. Now size is: 8
t2 P: 1520774272710
Add project. Now size is: 9
t3 P: 1520774272710
Add project. Now size is: 10
t4 P: 1520774272713 // 这里不知道是怎么发生的
t5 P: 1520774272713
t1 P: 1520774272713
Push操作中的：t4线程处于wait状态！
Push操作中的：t1线程处于wait状态！
Push操作中的：t5线程处于wait状态！
t2 P: 1520774272910
Push操作中的：t2线程处于wait状态！
t3 P: 1520774272910
Push操作中的：t3线程处于wait状态！
Del project. Now size is: 9
c1 C: 1520774272710
Push操作中的：t4线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t1线程处于被唤醒了！
Push操作中的：t1线程处于wait状态！
Del project. Now size is: 9
Push操作中的：t5线程处于被唤醒了！
Add project. Now size is: 10
c2 C: 1520774272713
Push操作中的：t2线程处于被唤醒了！
Push操作中的：t2线程处于wait状态！
t4 P: 1520774273510
Push操作中的：t4线程处于wait状态！
t5 P: 1520774273512
Push操作中的：t5线程处于wait状态！
Del project. Now size is: 9
c1 C: 1520774272713
Push操作中的：t3线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t1线程处于被唤醒了！
Push操作中的：t1线程处于wait状态！
Del project. Now size is: 9
c2 C: 1520774272910
Push操作中的：t2线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t4线程处于被唤醒了！
Push操作中的：t4线程处于wait状态！
t3 P: 1520774274510
Push操作中的：t3线程处于wait状态！
t2 P: 1520774274512
Push操作中的：t2线程处于wait状态！
Del project. Now size is: 9
c1 C: 1520774272910
Push操作中的：t5线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t1线程处于被唤醒了！
Push操作中的：t1线程处于wait状态！
Del project. Now size is: 9
c2 C: 1520774273512
Push操作中的：t4线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t3线程处于被唤醒了！
Push操作中的：t3线程处于wait状态！
t5 P: 1520774275510
Push操作中的：t5线程处于wait状态！
t4 P: 1520774275512
Push操作中的：t4线程处于wait状态！
Del project. Now size is: 9
c1 C: 1520774273510
Push操作中的：t2线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t1线程处于被唤醒了！
Push操作中的：t1线程处于wait状态！
Del project. Now size is: 9
c2 C: 1520774274512
Push操作中的：t3线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t5线程处于被唤醒了！
Push操作中的：t5线程处于wait状态！
t2 P: 1520774276510
Push操作中的：t2线程处于wait状态！
t3 P: 1520774276512
Push操作中的：t3线程处于wait状态！
Del project. Now size is: 9
c1 C: 1520774274510
Push操作中的：t4线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t1线程处于被唤醒了！
Push操作中的：t1线程处于wait状态！
Del project. Now size is: 9
c2 C: 1520774275512
Push操作中的：t5线程处于被唤醒了！
Add project. Now size is: 10
Push操作中的：t2线程处于被唤醒了！
Push操作中的：t2线程处于wait状态！

Process finished with exit code 1

```
> 修改生产者/消费者的Sleep数值，可以模拟供大于求/供不应求的情况。上面的例子是供大于求状态，push一直处于wait状态。

- 通过管道进行线程间的通信
  - PipedInputStream/PipedOutputStream
  - PipedReader/PipedWriter
  > 和Socket编程很像啊
```java
package cn.kisslinux.cap_03.mod_02;

import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-12 19:00
 */
public class Pag01_Pipe {

    @Test
    public void testClient() throws IOException, InterruptedException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();

        outputStream.connect(inputStream);

        Thread readThread = new Thread(new ReadData(inputStream));
        Thread writeThread = new Thread(new WriteData(outputStream));

        readThread.setName("ReadThread");
        writeThread.setName("WriteThread");

        readThread.start();
        Thread.sleep(2000);
        writeThread.start();

        Thread.sleep(500000);

    }

    class WriteData implements Runnable {

        private PipedOutputStream outputStream;

        public WriteData(PipedOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void run() {

            System.out.println("Write: ");
            try {
                for (int i=0; i<300; i++) {
                    String outData = "" + i;
                    outputStream.write(outData.getBytes());
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class ReadData implements Runnable{
        private PipedInputStream inputStream;

        public ReadData(PipedInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            System.out.println("Read: ");
            try {
                byte[] bytes = new byte[20];
                int length = inputStream.read(bytes);
                while (length != -1) {
                    String newData = new String(bytes, 0, length);
                    System.out.println(newData);
                    length = inputStream.read(bytes);
                }
                System.out.println();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

- example
```java
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
```

## 方法join的使用
主线程创建并启动子线程，如果子线程中要进行大量的耗时运算，主线程往往早于子线程结束之前停止。但若主线程要取得这个数据中的值，就用到了join()方法了。
1. **example**
```java
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
```
2. **join(long)**
- 等待long时间，然后自动停止等待
- 与**sleep(long)的区别
join(long)内部使用wait(long)实现，是具有释放锁的特点。

## ThreadLocal的使用
目的实现每一个线程都有自己的共享变量。
```java
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

```

- 为避免ThreadLocal默认get为null，可覆写其initialValue()方法。

## 类InheritableThreadLocal的使用
- 可以在子线程中取得父线程继承下来的值。
- 通过覆写childValue()方法可实现对父线程继承值的修改。


