package com.skynet.network;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 循环处理中心
 */
class ThreadPoolManager {
    //把用户输入的任务添加到请求队列
    //阻赛 容量无限大 插入操作
    private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    //2把队列中的人物房屋到线程池执行
    private ThreadPoolExecutor threadPoolExecutor;
    //private RejectedExecutionHandler rejectedExecutionHandler
    private static ThreadPoolManager threadPoolManager;
    //3让1，2无限的运行下去
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while(true) {//循环执行runable
                try {
                    Runnable runnable = queue.take();//d当队列没有请求时一直阻塞在这里
                    if(null == runnable)//执行请求
                        threadPoolExecutor.execute(runnable);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    private ThreadPoolManager(){
        threadPoolExecutor = new ThreadPoolExecutor(4, 0x10, 0x20, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                try {
                    queue.put(r);//从新添加进去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });//最后个参数防止超出0x10线程后丢弃
        threadPoolExecutor.execute(runnable);
    }
    public void execute(Runnable runnable){
        if(null != runnable)
            queue.add(runnable);
    }
    public static ThreadPoolManager getOutInstance(){
        if(null == threadPoolManager)
            threadPoolManager = new ThreadPoolManager();
        return threadPoolManager;
    }
}
