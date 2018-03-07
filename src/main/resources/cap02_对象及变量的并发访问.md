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
