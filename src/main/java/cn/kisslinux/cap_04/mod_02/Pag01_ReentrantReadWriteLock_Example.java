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
