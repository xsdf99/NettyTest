package client;

import io.netty.channel.Channel;
import myProtocol.MyRequest;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ClientManager {
    //    private static int coreSize = Runtime.getRuntime().availableProcessors();
//
    public static ExecutorService threadPool = Executors.newFixedThreadPool(1);
    public static ExecutorService cyclePool = Executors.newFixedThreadPool(1);


    public static LinkedBlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();

    public static ConcurrentHashMap<String, ClientFuture> taskMap = new ConcurrentHashMap<>();

    public static Channel channel;

    private static ReentrantLock lock = new ReentrantLock();

    private static Condition condition = lock.newCondition();

    public static void remoteMethod(String rpcConstant, Object[] objects, ClientFuture runnable) {
        String requestId = UUID.randomUUID().toString();
        taskMap.put(requestId, runnable);
        if (channel == null) {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        channel.writeAndFlush(new MyRequest(requestId, rpcConstant, objects));
    }

    public static void notifyChannel(){
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void start() {
        cyclePool.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String requestId = taskQueue.take();
                        ClientFuture runnable = taskMap.get(requestId);
                        threadPool.execute(runnable);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
