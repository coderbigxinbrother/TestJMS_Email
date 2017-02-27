package com.yc.threadpool;

public class SimpleThread extends Thread {
    private boolean runningFlag;
    private SendEmailTask argument;
    //提示是哪个线程工作
    public SimpleThread (int threadNumber){
        runningFlag = false;
    }
    //标志runningFlag 用已激活线程
    public boolean isRunning(){
        return runningFlag;
    }
    public synchronized void setRunning(boolean flag){
        runningFlag = flag;
        if(flag){
           this.notify();
        }
    }
    public String getArgument(){
        return this.argument.toString();
    }
    
    public void setArgument(SendEmailTask sendEmailTask) {
        this.argument = sendEmailTask;
    }
    @Override
    public synchronized void run() {
        try {
            while(true){
                if(!runningFlag){
                    this.wait();
                }else{
                    argument.run();
                    //sleep(5000);
                    setRunning(false);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
