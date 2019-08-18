package asynchronous;

import org.apache.log4j.Logger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wlp on 2019/8/16.
 */
public class EventMananger implements Runnable {
    private static final Logger logger = Logger.getLogger(EventMananger.class);
    private Map<String, AsyncEvent> events = new ConcurrentHashMap<String, AsyncEvent>();// 事件池
    private Thread thread = null;
    private boolean isRunning = false;
    AsyncWorkThreadPool asyncWorkThreadPool;

    public AsyncWorkThreadPool getAsyncWorkThreadPool() {
        return asyncWorkThreadPool;
    }

    public void setAsyncWorkThreadPool(AsyncWorkThreadPool asyncWorkThreadPool) {
        this.asyncWorkThreadPool = asyncWorkThreadPool;
    }

    /**
     * 启动事件管理器
     */
    public void start() {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting EventMgr");
        }
        isRunning = true;
        thread = new Thread(this, "EventMgr");
        thread.setDaemon(true);
        thread.start();

        if (logger.isDebugEnabled()) {
            logger.debug("Started EventMgr");
        }
    }

    /**
     * 根据id查询event
     *
     * @param id
     * @return
     */
    public AsyncEvent getEventById(String id) {
        AsyncEvent event = null;
        try{
            event = events.get(id);
        }catch (Exception e){
            logger.error(e);
        }

        return event;
    }
    /**
     * 返回所有event
     * @return
     */
    public Collection<AsyncEvent> getEvents() {
        return events.values();
    }

    /**
     * 添加事件
     *
     * @param event
     * @return
     */
    public boolean addEvent(AsyncEvent event) {
        boolean flag = false;
        try{
            if (!events.containsKey(event.getId())) {
                events.put(event.getId(), event);
                logger.info("add event " + event.getId() + " success");
                flag = true;
            }
        }catch (Exception e) {
            logger.error(e);
        }

        return flag;
    }
    // 重启事件对应的task
    public void restartAsyTask(String eventId){
        asyncWorkThreadPool.addTask( this.getEventById(eventId).getAsyncTask());
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning(){
        return isRunning;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                /**
                 * 每隔一小时扫描一遍事件池,移除过期的Event
                 */
                Thread.sleep(60 * 60 * 1000);

                Iterator<Map.Entry<String, AsyncEvent>> it = events
                        .entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, AsyncEvent> entry = it.next();
                    if (logger.isDebugEnabled()) {
                        logger.debug(entry.getValue().toString());
                    }
                    if (entry.getValue().isExpired()) {// 事件已过期,需移除
                        it.remove();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
        isRunning = false;
    }
}
