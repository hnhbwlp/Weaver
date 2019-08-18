package asynchronous;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlp on 2019/8/15.
 */
public class AsyncTask {
    private static final Logger logger = Logger.getLogger(AsyncTask.class);
    private List<TaskStep> steps = new ArrayList<>(); //“任务步”列表
    private Map<String,Object> outputs = new HashMap<>();//taskstep的输出
    private AsyncEvent event;// 任务对应的事件Id

    public AsyncTask(){
        setEvent(new AsyncEvent());
    }

    public AsyncTask(AsyncEvent event){
        setEvent(event);
    }

    public AsyncEvent getEvent() {
        return event;
    }

    public void setEvent(AsyncEvent event) {
        this.event = event;
        this.event.setAsyncTask(this);
    }

    public List<TaskStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TaskStep> steps) {
        this.steps = steps;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        outputs = outputs;
    }

    public void addStep(TaskStep taskStep){
        this.steps.add(taskStep);
        taskStep.setOrder(this.steps.size());
        this.event.addTotalSteps();//增加总步数
    }

    public Object execute() {
        long start = System.currentTimeMillis();
        logger.info("Starting to exec task " + this.getEvent().getId() + " at time " + start );
        Object obj = null;
        this.event.setStatus(AsyncEvent.EventStatus.PROCESSING);
        this.event.setStartTime(start);
        try {
            for(int i = this.getEvent().getCurrentStep(); i< this.getEvent().getTotalSteps(); i++){
                event.addStep();//当前步加一
                TaskStep taskStep = this.steps.get(i);
                if(taskStep.isRefrenceInput()){ //装填需要的参数
                    if (!taskStep.configParam(this.outputs)){
                        //如果装填失败，则直接报错
                        throw new TaskExecuteException("Async Task["+this.event.getOperation()+
                                "] taskStep["+taskStep.getName()+"] faild when config param.");
                    }
                }
                obj = taskStep.execute();//依次执行每一步，如果不抛出异常则执行成功，否则直接中断
                if(taskStep.isHasOutput()){  //输出存放到outputs
                    this.outputs.put(taskStep.getOutputName(), obj);
                }

            }

            if(this.event.getCurrentStep() == this.event.getTotalSteps()){
                this.event.setStatus(AsyncEvent.EventStatus.COMPLETED);
            }

        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException |TaskExecuteException e) {
            this.event.setStatus(AsyncEvent.EventStatus.FAILED);
            this.event.setResult(e.getMessage());
            logger.error("Excute task ["+this.event.getOperation()+"] failed, cause:"+e.getMessage());
        }catch (Exception e){
            logger.error("Excute task ["+this.event.getOperation()+"] failed, cause:"+e.getMessage());
        }

        logger.info("Task " + this.getEvent().getId()  + " exec over,time use:" + (System.currentTimeMillis() - start));
        return obj;  //如果成功，只返回最后一步执行结果，否则返回空值
    }
}