package com.yc.threadpool;

import java.util.Vector;
//单例
public class ThreadPoolManager {
    private int setMaxThread;  //最大线程数
    //Vector类实现了一个动态数组。和ArrayList和相似,但是两者是不同的: Vector是同步访问的。
    private Vector<SimpleThread> vector;
    
    private static ThreadPoolManager threadPoolManager;
    
    //获取ThreadPoolManager   单例模式     synchronized同步
    public static synchronized ThreadPoolManager getInstance(int count){
        if(threadPoolManager == null){
            threadPoolManager = new ThreadPoolManager(count);
        }
        return threadPoolManager;
    }
    
    private ThreadPoolManager(int setMaxThread) {
        this.setMaxThread = setMaxThread;
        vector = new Vector<SimpleThread>();
        for(int i =1; i < setMaxThread; i++){
            //创建一定数量的线程，放入到集合中 ,并开启线程
            SimpleThread thread = new SimpleThread(i);
            vector.addElement(thread);
            thread.start();
        }
    }
    
    public void process(SendEmailTask sendEmailTask){
        int i;
        for(i =0; i < vector.size();i++){
            SimpleThread currentThread = (SimpleThread) vector.elementAt(i);
            if(!currentThread.isRunning()){
                currentThread.setArgument(sendEmailTask);
                currentThread.setRunning(true);
                return;
            }
        }
        //如果线程池中所有的线程就都在工作，则要创建新的线程加到线程池中
        if(i == vector.size()){
            int currentthreadSize = vector.size();
            for(int j =currentthreadSize+1; j < currentthreadSize+10; j++){
                SimpleThread thread = new SimpleThread(j);
                vector.addElement(thread);
                thread.start();
            }
            SimpleThread currentThread = (SimpleThread) vector.elementAt(currentthreadSize);
            currentThread.setArgument(sendEmailTask);
            currentThread.setRunning(true);
        }
    }
}
