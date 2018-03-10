# 对象及变量的并发访问

## synchronized同步方法
- 方法内的变量是线程安全的
“非线程安全”问题存在于“实例变量”中，如果是方法内部的私有变量，则不存在“非线程安全”问题。
- 实例变量非线程安全
- 多个对象会有多个锁
关键字synchronized取得的锁都是对象锁，而不是把一段代码或方法当作锁。
- synchronized方法与锁对象
  - A线程先持有object对象的Lock锁，B线程可以异步调用object对象中的非synchronized类型的方法
  - A线程先持有object对象的Lock锁，B线程如果在这时调用object对象中的synchronized类型的方法，则需等待，也就是同步
```java
/**
 * 测试
 * 类中只有 都加了synchronized关键字的方法才会同步
 * 
 * @author 庄壮壮 Administrator
 * @since 2018-03-07 19:59
 */
public class Pag01_Synchronized_01 {

    @Test
    public void testClient() throws InterruptedException {
        MyObject object = new MyObject();

        Thread thread1 = new Thread(new MyRunnable01(object));
        Thread thread2 = new Thread(new MyRunnable01(object));
        Thread thread3 = new Thread(new MyRunnable02(object));

        thread1.setName("thread1");
        thread2.setName("thread2");
        thread3.setName("thread3");

        thread1.start();
        thread2.start();
        thread3.start();

        Thread.sleep(20000);
    }

    private class MyObject {
        synchronized public void methodA() {
            try {
                System.out.println(Thread.currentThread().getName() + "begin methodA time=" + System.currentTimeMillis());
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + "end time=" + System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void methodB() {
            try {
                System.out.println(Thread.currentThread().getName() + "begin methodB time=" + System.currentTimeMillis());
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + "end time=" + System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyRunnable01 implements Runnable {

        private MyObject object;

        public MyRunnable01(MyObject object) {
            this.object = object;
        }

        @Override
        public void run() {
            object.methodA();
        }
    }

    private class MyRunnable02 implements Runnable {
        private MyObject object;

        public MyRunnable02(MyObject object) {
            this.object = object;
        }

        @Override
        public void run() {
            object.methodB();
        }
    }
}
```
- 脏读
  - 发生脏读的情况是在读取实例变量时，此值已经被其他线程改变。
  - 脏读一定发生在操作实例变量的情况下，也就是不同线程“争抢”实例变量的结果。
- 同步不具有继承性
> synchronized关键字实际上是锁住的当前对象，可以理解为synchronized(this)

## synchronized同步语句块
### synchronized方法的弊端
A线程执行一个长时间任务，与它同步的B线程必须要等待，这种情况下可以使用synchronized同步语句块来解决。
- synchronized同步代码块的使用
```java
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
```
> 注意，不在synchronized代码块中的，就是异步执行，在synchronized块中的就是同步执行。
- 当一个线程访问object的一个synchronized(this)代码块时，其他线程对同一个object的其他synchronized(this)同步代码块的访问将被阻塞，这说明，synchronized使用的“对象监视器”是同一个。
- 可以将任意对象作为对象监视器
synchronized同步方法，或者synchronized(this)同步代码块分别有两种作用
  - 对其他synchronized同步方法或者synchronized(this)同步代码块调用呈阻塞状态
  - 同一时间只有一个线程可以执行synchronized同步方法或者synchronized(this)同步代码块中的代码。
  
对于synchronized(非this对象x)
  - 多个线程持有同一个“对象监视器”的前提下，同一时间只有一个线程可以执行同步代码块中的代码
  - 持有不同对象锁的线程执行时异步的。
  
  - 优点
    -  不与其他锁this对象同步方法争抢this锁。
    
  - 三条结论
    - 当多个线程同时执行synchronized（x){}同步代码块时呈现同步效果。
    - 当其他线程执行x对象中的synchronized同步方法时呈现同步效果。
    - 当其他线程执行x对象方法里面的synchronized(this)代码块时也呈现同步效果。
- 静态同步synchronized方法与synchronized(class)代码块
若synchronized用在static静态方法上，那就是对当前*.java文件对象的class类进行持锁。
> 如果你能明白什么是静态方法，很容易理解为什么是对当前的class持锁，而不是像之前，对某一个对象持锁。
```java
public class Pag01_Static_Class {

    synchronized public static void staticMethod1() {
        try {
            System.out.println(Thread.currentThread().getName() + " method1 start time: " + System.currentTimeMillis());
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " method1 start time: " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public static void staticMethod2() {
        try {
            System.out.println(Thread.currentThread().getName() + " method2 start time: " + System.currentTimeMillis());
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " method2 start time: " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public void method3() {
        try {
            System.out.println(Thread.currentThread().getName() + " method3 start time: " + System.currentTimeMillis());
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " method3 start time: " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 *
 * synchronized同步 静态方法
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-07 19:59
 */
public class Pag01_Synchronized_03 {

    @Test
    public void testClient() throws InterruptedException {
        Thread thread1 = new Thread(new MyRunnable1());
        Thread thread2 = new Thread(new MyRunnable2());
        Thread thread3 = new Thread(new MyRunnable3(new Pag01_Static_Class()));

        thread1.setName("thread1");
        thread2.setName("thread2");
        thread3.setName("thread3");

        thread1.start();
        thread2.start();
        thread3.start();

        Thread.sleep(7000);
    }

    private class MyRunnable1 implements Runnable {

        @Override
        public void run() {
            Pag01_Static_Class.staticMethod1();
        }
    }

    private class MyRunnable2 implements Runnable {
        @Override
        public void run() {
            Pag01_Static_Class.staticMethod2();
        }
    }

    private class MyRunnable3 implements Runnable {

        Pag01_Static_Class cls;

        public MyRunnable3(Pag01_Static_Class cls) {
            this.cls = cls;
        }

        @Override
        public void run() {
            cls.method3();
        }
    }
}
```
- 数据类型String的常量池特性
由于String本身特性，通常不使用String作为锁对象。

- 多线程的死锁
```java
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
```
> lsk$ jps // 查看当前Java线程
> lsk$ jstack // 查看线程，可查看死锁。

- 内置类与静态内置类
// TODO

- 锁对象的改变
通俗来说，如果引用对应的对象改变了，锁就改变了。
比如int a = 10，改成 a = 20，对应的对象改变了，锁也就失效了。
比如List array = new ArrayList(); arrar.add(1)，是不影响的，因为引用对应的对象地址并没有改变

## volatile关键字
volatile关键字的作用是强制从公共堆栈中，取得变量的值，而不是从线程私有数据栈中取得变量的值。
```java
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

```
- volatile同synchronized比较
  - volatile是线程同步的轻量级实现，所以性能更好一些，但只能修饰变量。而synchronized可以修饰方法、代码块，使用比率更高。
  - 多线程访问volatile不会发生阻塞，而synchronized会发生阻塞。
  - volatile能保证数据可见性，但不能保证原子性。synchronized两者都可保证。即，volatile并不能保证线程安全。
```text
  read         | 主存复制变量到当前线程工作内存
    |          |
  load   |
    |    | 非
  use    | 原  | 执行代码，改变共享变量
    |    | 子  | 
  asign  | 性
    |    |
  store        | 用工作内存数据刷新主存对应变量的值
    |          |
  write
```
  