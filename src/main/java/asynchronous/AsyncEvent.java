package asynchronous;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by wlp on 2019/8/15.
 */
public class AsyncEvent {
    private static final Logger logger = Logger.getLogger(AsyncEvent.class);
    private String id = null;// 事件唯一标识
    private EventStatus status = EventStatus.PENDING;// 事件状态,初始状态为PENDING
    private long startTime = -1L;// 事件起始时间

    private String beginTime = null; //格式化时间起始时间
    private long expireTime = 2 * 24 * 60 * 60 * 1000L;// 过期时间,单位:毫秒
    private String result;  //任务处理中间结果
    private String operation; //任务名称, 例如 add tenant
    private String operationUser; //事件操作用户
    private String detail;  //操作详情，例如 tenant1
    private int totalSteps; //操作总步数
    private int currentStep=0; //当前正在执行的步数

    private AsyncTask asyncTask ; //对应的task

    public AsyncEvent() {
        this.id = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.beginTime = Timestamp.valueOf(simpleDateFormat.format(new Date(startTime))).toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EventStatus getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public AsyncTask getAsyncTask() {
        return asyncTask;
    }

    public void setAsyncTask(AsyncTask asyncTask) {
        this.asyncTask = asyncTask;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public void setResult(String result) {
        if(this.result==null)
            this.result = result;
        else
            this.result=this.result+"\n"+result;
    }

    public String getOperationUser() {
        return operationUser;
    }

    public void setOperationUser(String operationUser) {
        this.operationUser = operationUser;
    }

    public void addTotalSteps(){
        this.totalSteps += 1;
    }
    /**
     * 更新事件状态
     *
     * @param status
     *            更新后的状态
     */
    public void setStatus(EventStatus status) {
        if (logger.isDebugEnabled()) {
            logger.debug("update event "
                    + this.id
                    + " status from "
                    + this.status
                    + " to "
                    + status
                    + " at "
                    + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System
                    .currentTimeMillis()));
        }
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * 判断事件是否过期
     *
     * @return
     */
    public boolean isExpired() {
        if (expireTime < 0){
            return false;// 永不过期
        }
        // 事件过期
        return (System.currentTimeMillis() - startTime) > expireTime;

    }

    public boolean isDone() {
        return  this.status == EventStatus.COMPLETED;
    }

    public boolean isFailed() {
        return this.status == EventStatus.FAILED;
    }

    /**
     * 事件状态
     *
     * @author wanglianping
     *
     */
    public enum EventStatus {
        PENDING("PENDING", 0), PROCESSING("PROCESSING", 1), SUCCESS("SUCCESS",
                2), FAILED("FAILED", 3), COMPLETED("COMPLETED", 4);

        private String name;
        private int index;

        EventStatus(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }

    }

    /**
     * 事件类型
     *
     * @author wanglianping
     *
     */
    public enum EventType {
        ASYNC("ASYNC", 0);

        private String name;
        private int index;

        EventType(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }

        public boolean equals(EventType eventType){
            return this.name.equals(eventType.getName()) && this.index == eventType.getIndex();
        }

    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        StringBuilder buf = new StringBuilder();
        buf.append("EventId:").append(id).append(" EventType:")
                .append(" StartTime:").append(sdf.format(startTime));
        return buf.toString();
    }


    public void addStep(){
        this.currentStep += 1;
    }
}


