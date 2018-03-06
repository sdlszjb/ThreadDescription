# Java多线程技能
## 进程和多线程的概念及线程的优点
## 使用多线程
### 继承Thread类
```java
/**
 * 如何使用多线程
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-06 19:36
 */
public class HowToUseThreadTests {

    @Test
    public void testThreadUsingThread() throws InterruptedException {
        Thread thread = new MyThread();
        thread.start();
        for (int i=0; i<100; i++) {
            System.out.println("MainThread---" + i);
        }
        Thread.sleep(1000);
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            for (int i=0; i<100; i++) {
                System.out.println("My  Thread---" + i);
            }
        }
    }
}
```
### 实现Runnable接口
```java
public class HowToUseThreadTests {

    @Test
    public void testThreadUsingRunnable() throws InterruptedException {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
        for (int i=0; i<100; i++) {
            System.out.println("MainThread---" + i);
        }
        Thread.sleep(1000);
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            for (int i=0; i<100; i++) {
                System.out.println("My  Thread---" + i);
            }
        }
    }
}
```
### 实例变量与线程安全
## currentThread()方法
## isAlive()方法
判断当前线程是否处于活动状态
## sleep()方法
让正在执行的线程休眠
## getId()
取得线程的唯一标识
## 停止线程
thread中，可以通过thread.interrupt()来进行中断线程，但是！interrupt()方法只是为当前线程设置一个停止位，实际线程并没有停止！
> 是为了保证及时线程被interrupt后，也能安全地保存线程中的数据或者其他必须处理的逻辑
> stop()会对锁定的对象进行“解锁”，导致数据不一致。
> 这也是stop()方法过期的原因之一。
### 判断线程是否是停止状态
1. this.interrupted()
  - 静态方法。
  - 测试是否中断，随后将中断状态由该方法清除（也就是，第二次调用该方法肯定会返回false）。
2. this.isInterrupted()
  - 测试线程Thread对象是否已经是中断状态，但不清楚状态标志。
### 停止线程-异常法
```java

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-06 19:59
 */
public class Pag02_HowToStopThreadTests {

    @Test
    public void testUsingException() throws InterruptedException {
        Thread thread = new Thread(new MyThread01());
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
    }

    private class MyThread01 implements Runnable {
        @Override
        public void run() {
            try {
                for (int i=0; i<500000; i++) {
                    Thread.sleep(50);
                    System.out.println(i);
                    if (Thread.interrupted()) {
                        System.out.println("已经是停止状态了！我要退出了！");
                        throw new InterruptedException();
                    }
                }
                System.out.println("我在for下面");
            } catch (InterruptedException e) {
                System.out.println("进入MyThread01类run方法中的catch了！");
                e.printStackTrace();
            }
        }
    }
}
```
### 停止线程-使用return
```java
public class Pag02_HowToStopThreadTests {

    @Test
    public void testUsingReturn() throws InterruptedException {
        System.out.println("start");
        Thread thread = new Thread(new MyRunnable03());
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
        System.out.println("end");
    }

    private class MyRunnable03 implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (Thread.interrupted()) {
                    System.out.println("停止了！");
                    return;
                }
                System.out.println("time: " + System.currentTimeMillis());
            }
        }
    }
}
```
### 停止线程-在沉睡中停止
如果线程在sleep()状态下停止线程，将会触发InterruptedException。
```java
/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-06 19:59
 */
public class Pag02_HowToStopThreadTests {
    @Test
    public void testInterruptDuringSleeping() throws InterruptedException {
        System.out.println("start");
        Thread thread = new Thread(new MyRunnable02());
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
        System.out.println("end");
    }

    public class MyRunnable02 implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("Run begin.");
                Thread.sleep(200000);
                System.out.println("Run end.");
            } catch (InterruptedException e) {
                System.out.println("在沉睡中被停止！进入Catch." + Thread.interrupted());
                e.printStackTrace();
            }
        }
    }
}
```
### 暴力停止
使用stop()方法停止线程。
使用stop()方法会抛出ThreadDeath异常，但通常，该异常不需要显式地捕捉。

## 暂停线程
### suspend()和resume()方法
- suspend()方法暂停线程。
- resume()方法恢复暂停的线程。
> suspend()和resume()方法的缺点--独占，会造成公共的同步对象的独占，使得其他对象无法访问公共同步对象。
> suspend()和resume()方法也容易出现因为线程的暂停而导致的数据不同步的现象。

## yield()方法
放弃当前CPU资源，让给其他任务去占用CPU执行时间，但放弃的时间不确定。

## 线程的优先级
- 线程优先级越高，得到的CPU资源越多。
- 设置线程优先级使用setPriority()方法。
  - MIN_PRIORITY = 1
  - NORM_PRIORITY = 5
  - MAX_PRIORITY = 10
- 线程优先级具有继承性
比如A线程启动B线程，则B线程的优先级与A是一样的。
- 优先级具有规则性
CPU尽量将更多的资源让给优先级较高的线程
- 优先级具有随机性
CPU不能保证优先级高的线程能一次执行完

## 守护线程
- 当进程中不存在非守护线程了，则守护线程自动摧毁。典型的守护线程是垃圾回收线程。
- 设置守护线程使用thread.setDaemon(true)