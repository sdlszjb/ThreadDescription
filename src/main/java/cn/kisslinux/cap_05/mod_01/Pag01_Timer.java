package cn.kisslinux.cap_05.mod_01;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-14 20:01
 */
public class Pag01_Timer {

    // true设置为守护线程
    private static Timer timer = new Timer("myTimer", true);

    @Test
    public void testSchedule1() {
        try {
            TimerTask task = new MyTask();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = "2018-03-14 20:08:00";
            Date date = format.parse(dateString);
            timer.schedule(task, date);
            Thread.sleep(100000);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每隔一秒 重复执行
     */
    @Test
    public void testSchedule2() {

        try {
            TimerTask task = new MyTask();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = "2018-03-14 20:11:00";
            Date date = format.parse(dateString);
            timer.schedule(task, date, 1000);
            Thread.sleep(100000);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同一个Timer，如果有延时任务，则会延迟进行。在同一个Timer内，是同步执行的。
     * 按照队列顺序
     */
    @Test
    public void testSchedule3() {
        try {
            TimerTask sleepTask = new SleepTask();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = "2018-03-14 20:15:00";
            Date date = format.parse(dateString);
            timer.schedule(sleepTask, date, 1000);
            Thread.sleep(60000);

            /**
             * 取消当前task
             *
             * 但这样写不能用，因为获取不到timer线程的锁
             *
             * 要写在task的run方法中
             */
            // sleepTask.cancel();

            /**
             * 取消当前timer的全部任务
             *
             * 但这样写不能用，因为获取不到timer线程的锁
             *
             * 要写在task的run方法中
             */
            // timer.cancel();
            Thread.sleep(1000);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class MyTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("定时器运行了，时间为：" + System.currentTimeMillis());
        }
    }

    private class SleepTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Start: " + System.currentTimeMillis());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("End: " + System.currentTimeMillis());
        }
    }
}
