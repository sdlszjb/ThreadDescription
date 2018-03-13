# Lock的使用
- ReentrantLock的使用
- ReentrantReadWriteLock的使用。

## 使用ReentrantLock类
1. **Example**
```java
package cn.kisslinux.cap_04.mod_01;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-12 21:56
 */
public class Pag01_ReentrantLock_Example {
    @Test
    public void testClient() throws InterruptedException {
        Service service = new Service();
        new MyThread(service).start();
        new MyThread(service).start();
        new MyThread(service).start();
        new MyThread(service).start();

        Thread.sleep(50000);
    }

    private class Service {
        private ReentrantLock lock = new ReentrantLock();

        void testMethod() {
            lock.lock();
            for (int i=0; i<5; i++) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
            lock.unlock();
        }
    }

    private class MyThread extends Thread {

        Service service;

        public MyThread(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            service.testMethod();
        }
    }
}
```

2. **使用Condition实现等待/通知**
condition之前一定要lock.lock，在最后一定要lock.unlock
- 多个condition条件
```java
package cn.kisslinux.cap_04.mod_01;

import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-13 21:38
 */
public class Pag02_ReentrantLock_Condition {

    @Test
    public void testClient() throws InterruptedException {
        Service service = new Service();
        Thread threadA = new ThreadA(service);
        Thread threadB = new ThreadB(service);

        threadA.start();

        Thread.sleep(2000);
        threadB.start();

        Thread.sleep(2000);
        service.signalAll_A();
        
        Thread.sleep(2000);
        service.signalAll_B();

        Thread.sleep(8000);
    }

    private class Service {
        private Lock lock = new ReentrantLock();
        private Condition conditionA = lock.newCondition();
        private Condition conditionB = lock.newCondition();

        void awitA() {
            try {
                lock.lock();
                System.out.println("awitA: " + System.currentTimeMillis());
                conditionA.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        void awitB() {

            try {
                lock.lock();
                System.out.println("awitB: " + System.currentTimeMillis());
                conditionB.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        void signalAll_A() {
            try {
                lock.lock();
                System.out.println("signalA: " + System.currentTimeMillis());
                conditionA.signal();
            } finally {
                lock.unlock();
            }
        }

        void signalAll_B() {

            try {
                lock.lock();
                System.out.println("signalB: " + System.currentTimeMillis());
                conditionB.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    class ThreadA extends Thread {
        Service service;

        public ThreadA(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            service.awitA();
        }
    }

    class ThreadB extends Thread {
        Service service;

        public ThreadB(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            service.awitB();
        }
    }
}
```
- 公平锁与非公平锁
锁Lock分为公平锁与非公平锁：公平锁表示线程获取锁的顺序是按照线程加锁的顺序来分配的，即先进先出。非公平锁是抢占机制，类似synchronized。
```java

public class Pag02_ReentrantLock_Condition {
    Lock fairLock = new ReentrantLock(true); // 公平锁
    Lock notFairLock = new ReentrantLock(false); // 非公平锁
}
```
- 1.
  - getHoldCount()查询当前线程保持此锁的个数，也就是调用lock方法的次数。
  - getQueueLength()返回正等待获得此锁的线程估计数。
  - getWaitQueueLength(Condition condition)返回等待与此锁相关的给定条件的线程估计数。
- 2.
  - hasQueuedThread(Thread thread)查询指定线程是否在等待获取此锁定。
  - hasQueuedThread()查询是否在等待获取此锁定。
  - hasWaiters(Condition condition)查询是否有线程正在等待与此锁定有关的condition条件
- 3.
  - isFair()判断是不是公平锁
  - isHeldByCurrentThread()查询当前线程是否持有此锁定。
  - isLocked()查询当前锁定是否已经被任意线程持有。
- 4.
  - lockInterruptibly()如果当前线程未被中断，则获得锁定，如果已经被中断，则抛出异常
  - tryLock() 在调用时，锁定未被另一个线程持有，则获取该锁定
  - tryLock(long timeout, TimeUnit unit)若锁定在给定的时间内没有被另一个线程保持，且当前线程未被中断，则持有该锁。
  
## ReentrantReadWriteLock类
类ReentrantLock具有完全互斥排他特性，在某些情况下，效率太低。ReentrantReadWriteLock表示一个是读操作相关锁，称为共享锁，一个是写操作相关锁，称为排他锁。
- 共享/互斥情况 说明
  - 读-读 共享
  - 读-写 排斥
  - 写-写 排斥

```java
package cn.kisslinux.cap_04.mod_02;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-13 22:35
 */
public class Pag01_ReentrantReadWriteLock_Example {
    @Test
    public void testRR() throws InterruptedException {
        Service service = new Service();
        Thread threadA = new Thread(service::readA);
        Thread threadB = new Thread(service::readB);

        threadA.start();
        Thread.sleep(1000);
        threadB.start();

        Thread.sleep(5000);
    }
    
    @Test
    public void testRW() throws InterruptedException {

        Service service = new Service();
        Thread threadA = new Thread(service::readA);
        Thread threadB = new Thread(service::writeA);

        threadA.start();
        Thread.sleep(1000);
        threadB.start();

        Thread.sleep(5000);
    }

    @Test
    public void testWW() throws InterruptedException {

        Service service = new Service();
        Thread threadA = new Thread(service::writeA);
        Thread threadB = new Thread(service::writeB);

        threadA.start();
        Thread.sleep(1000);
        threadB.start();

        Thread.sleep(5000);
    }
    

    private class Service {
        private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        
        public void readA() {
            try {
                lock.readLock().lock();
                System.out.println("ReadLockA: " + System.currentTimeMillis());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public void readB() {
            try {
                lock.readLock().lock();
                System.out.println("ReadLockB: " + System.currentTimeMillis());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public void writeA() {
            try {
                lock.writeLock().lock();
                System.out.println("WriteLockA: " + System.currentTimeMillis());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void writeB() {
            try {
                lock.writeLock().lock();
                System.out.println("WriteLockB: " + System.currentTimeMillis());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}

```
  