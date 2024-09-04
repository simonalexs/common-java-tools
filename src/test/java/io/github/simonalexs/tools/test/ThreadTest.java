package io.github.simonalexs.tools.test;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.simonalexs.tools.other.PrintUtil.println;

/**
 * @ClassName: ThreadTest
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/6/24 10:39
 * @Version: 1.0
 */
public class ThreadTest {
    static AtomicInteger threadCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final MyThreadPoolExecutor threadPool = new MyThreadPoolExecutor(
                5,
                10,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new NameThreadFactory("ThreadTest"),
                new ProxyHandler(2));
        monitor(threadPool);
        new Thread(() -> {
            for (int i = 0; i < 30000; i++) {
                final Thread thread = new Thread(() -> {
                    threadCount.getAndIncrement();
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    threadCount.getAndDecrement();
                });
                println("添加" + StringUtils.leftPad(String.valueOf(i), 5, "0") + "到线程池");
                threadPool.execute(thread);
                println("添加" + StringUtils.leftPad(String.valueOf(i), 5, "0") + "结束");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("主线程结束");
    }

    private static void monitor(MyThreadPoolExecutor threadPool) {
        new Thread(() -> {
            int pre1 = 0;
            int pre2 = 0;
            int pre3 = 0;
            final long start = System.currentTimeMillis();
            while (true) {
                final int activeCount = threadPool.getActiveCount();
                final int poolSize = threadPool.getPoolSize();
                final int queueSize = threadPool.getQueue().size();
                if (activeCount == pre1 && poolSize == pre2 && queueSize == pre3) {
                    continue;
                }
                pre1 = activeCount;
                pre2 = poolSize;
                pre3 = queueSize;
                final long end = System.currentTimeMillis();
                System.out.print("time = " + (end - start));
                System.out.print(", ");
                System.out.print("threadCount = " + threadCount);
                System.out.print(", ");
                System.out.print("activeCount = " + activeCount);
                System.out.print(", ");
                System.out.print("poolSize = " + poolSize);
                System.out.print(", ");
                System.out.print("queueSize = " + queueSize);
                System.out.println();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

class MyThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
}
class NameThreadFactory implements ThreadFactory {

    private final AtomicInteger mThreadNum = new AtomicInteger(1);
    private final String threadName;

    public NameThreadFactory(String className){
        threadName = className + "-" + mThreadNum.getAndIncrement();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, threadName);
        return t;
    }
}
class ProxyHandler implements RejectedExecutionHandler {

    private final AtomicInteger handlerCount = new AtomicInteger(0);
    private final RejectedExecutionHandler rejectedExecutionHandler;

    public ProxyHandler(int i) {
        RejectedExecutionHandler rejectedExecutionHandler1 = null;
        switch (i) {
            case 1:
                rejectedExecutionHandler1 = new ThreadPoolExecutor.AbortPolicy();
                break;
            case 2:
                rejectedExecutionHandler1 = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case 3:
                rejectedExecutionHandler1 = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case 4:
                rejectedExecutionHandler1 = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            default:
                throw new IllegalArgumentException("暂时不支持");
        }
        rejectedExecutionHandler = rejectedExecutionHandler1;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        handlerCount.getAndIncrement();
        rejectedExecutionHandler.rejectedExecution(r, executor);
        doLog(r,executor);
    }

    int getRejectedExecutionCount() {
        return handlerCount.get();
    }

    private void doLog(Runnable r, ThreadPoolExecutor e) {
        // 可做日志记录等
        System.err.println( r.toString() + " rejected");
    }

}
