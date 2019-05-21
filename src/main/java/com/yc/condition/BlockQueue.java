package com.yc.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Yanchen
 * @ClassName BlockQueue
 * @Date 2019/5/21 16:44
 */
public class BlockQueue {
    private List list;
    private ReentrantLock lock;
    private Condition putCondition,takeCondition ;
    private static final int QUEQUE_SIZE = 10;

    public BlockQueue(){
        list = new ArrayList();
        lock = new ReentrantLock();
        putCondition = lock.newCondition();
        takeCondition  = lock.newCondition();
    }

    public void put(Object value) {
        try{
            lock.lock();
            while(list.size() >= QUEQUE_SIZE){
                System.out.println("线程【"+Thread.currentThread().getName()+"】等待空位,当前队列大小为【"+list.size()+"】。");
                putCondition.await();
            }
            list.add(value);
            System.out.println("线程【"+Thread.currentThread().getName()+"】成功放入对象【"+value+"】，等待其他线程取走该对象,当前队列大小为【"+list.size()+"】。");
            takeCondition.signalAll();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }


    }


    public Object take()  {
        Object v = null;
        try{
            lock.lock();
            while(list.size() == 0){
                System.out.println("线程【"+Thread.currentThread().getName()+"】等待其他线程放入对象,当前队列大小为【"+list.size() +"】。");
                takeCondition.await();
            }
            v = list.remove(0);
            System.out.println("线程【"+Thread.currentThread().getName()+"】成功取到对象【"+v+"】,当前队列大小为【"+list.size() +"】。");
            putCondition.signalAll();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
        return v;
    }

    public static void main(String[] args) {
        BlockQueue blockQueue = new BlockQueue();
        for(int i=0;i<20;i++){
            new Thread(()->blockQueue.put(new Object()),"Thread_PUT"+String.valueOf(i)).start();
        }
        for(int i=0;i<20;i++){
            new Thread(()->blockQueue.take(),"Thread_TAKE"+String.valueOf(i)).start();
        }
    }
}
