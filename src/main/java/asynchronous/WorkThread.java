package asynchronous;

/**
 * Created by wlp on 2019/8/15.
 */

import org.apache.log4j.Logger;

/**
 * 工作线程,用于处理耗时较长的任务
 *
 * @author wanglianping
 *
 */
public class WorkThread implements Runnable {
    private static final Logger logger = Logger.getLogger(WorkThread.class);
    private boolean isRunning = false;
    private Thread thread;

    private AsyncWorkThreadPool asyncWorkThreadPool;
    private int index;

    public WorkThread(int index, AsyncWorkThreadPool asyncWorkThreadPool) {
        this.index = index;
        this.asyncWorkThreadPool = asyncWorkThreadPool;
    }

    public void start() {
        isRunning = true;
        thread = new Thread(this, "WorkThread" + this.index);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();
    }

    public boolean isActive(){
        return thread.isAlive();
    }

    public void restart(){
        logger.debug("Thread "+ index + "restart !!!");
        thread.start();
    }

    @Override
    public void run() {
        AsyncTask task = null;
        while (isRunning) {
            task = asyncWorkThreadPool.getTask();
            task.execute();
        }
    }
}

