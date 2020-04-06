package asynchronous;

import org.apache.log4j.Logger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by wlp on 2019/8/15.
 */

public class AsyncWorkThreadPool {
    private static final Logger logger = Logger.getLogger(AsyncWorkThreadPool.class);
    private BlockingQueue<AsyncTask> tasks = new ArrayBlockingQueue<>(100);// 异步任务队列,待执行的队列
    private EventMananger eventMananger;
    private int SIZE= 10;
    private WorkThread[] works = new WorkThread[SIZE];// 工作线程池
    private boolean isStarted = false;

    public static AsyncWorkThreadPool asyncWorkThreadPool;

    public static synchronized AsyncWorkThreadPool newInstance(){
        if(asyncWorkThreadPool == null){
            return new AsyncWorkThreadPool();
        }
        return asyncWorkThreadPool;
    }

    public AsyncWorkThreadPool(){
        //配置pool对应的event管理器
        this.eventMananger = new EventMananger();
        this.eventMananger.setAsyncWorkThreadPool(this);
    }

    public EventMananger getEventMananger() {
        return eventMananger;
    }

    public void setEventMananger(EventMananger eventMananger) {
        this.eventMananger = eventMananger;
    }

    /**
     * 向队列中添加任务
     *
     * @param task
     */
    public boolean addTask(AsyncTask task) {

        guranteAllThreadUp();//检查thread是否所有thread都启动

        if(this.tasks.offer(task)){
            logger.info("Add async task to queue complete,relate event information:"
                    + task.getEvent().toString());
            task.getEvent().setStatus(AsyncEvent.EventStatus.PENDING);
            this.eventMananger.addEvent(task.getEvent());
            return true;
        }
        return false;
    }

    /**
     * 从队列中获取任务
     *
     * @return
     */
    public AsyncTask getTask() {
        try {
            return tasks.take();
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public void start() {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting AsyncWorkThreadPool. total thread size: "+ SIZE);
        }
        if (!isStarted) {
            for (int index = 0; index < SIZE; index++) {
                WorkThread work = new WorkThread(index, this);
                works[index] = work;
                works[index].start();
            }
            isStarted = true;
            logger.info("Thread Pool Started...");
            this.eventMananger.start();
        }
    }

    public void guranteAllThreadUp(){
        for (int index = 0; index < SIZE; index++) {
            if (!works[index].isActive()){
                works[index].restart();
            }
        }
    }
}
