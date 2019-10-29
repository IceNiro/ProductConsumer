package task;

import com.sun.corba.se.impl.oa.poa.AOMEntry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class TaskWork {


    private static Lock lock = new ReentrantLock();
    private static int queueSize = 256;
    private static Long writeIndex = 0l;
    private static Long readIndex = 0l;
    private static final AtomicInteger sendPeriod = new AtomicInteger(0);
    private static final AtomicInteger ai = new AtomicInteger(0);

    private static TaskEvent[] queue = new TaskEvent[queueSize];

    private static Long getIndex(Long value) {
        if (value.intValue() + 1 == queueSize) {
            return 0l;
        }
        return value + 1;
    }

    public static void main(String[] args) {
        //beginWork();
        beginWork();
        beginWork();
        beginWork();
        //sendTask(new TaskEvent("" + 1));
        sendTask();
        sendTask();
        final int runtime = 10;
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println(ai.get());
                System.out.println(ai.get() / runtime);
                System.exit(-1);
            }
        }, runtime, TimeUnit.SECONDS);
    }

    public static void sendTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (queue[(writeIndex = getIndex(writeIndex)).intValue()] != null) {
                            LockSupport.parkNanos(5l);
                        }
                        queue[writeIndex.intValue()] = new TaskEvent(sendPeriod.incrementAndGet());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }
        }).start();
    }

    public static void beginWork() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TaskEvent e;
                        e = queue[(readIndex = getIndex(readIndex)).intValue()];
                        while (e == null) {
                            //System.out.println("sender work lazy, wi :" + writeIndex + "-ri:" + readIndex);
                            LockSupport.parkNanos(5l);
                            e = queue[readIndex.intValue()];
                        }
                        queue[readIndex.intValue()] = null;
                        ai.set(e.getData());
                        System.out.println(writeIndex + "-" + readIndex + "-" + (e == null ? "null" : e.getData()));
                        //Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }
            }
        });
        t.setName("work");
        t.start();
    }
}
