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
    Lock lock = new ReentrantLock(true);

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
