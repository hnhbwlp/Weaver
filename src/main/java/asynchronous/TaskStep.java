package asynchronous;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlp on 2019/8/17.
 */
public class TaskStep {
    private String name;//本步名称
    private FuncExecute funcExecute;
    private Object[] params;
    private AsyncEvent event;// 任务对应的事件Id
    private boolean hasOutput = false;//表示是否需要存储输出
    private String outputName;//输出的名字
    private boolean refrenceInput; //是否引用task中其他step输出
    private Map<Integer,String> refrenceParams; //应用其他输入的名字和其在自己参数中的位置,
                                                // key为参数在[]params中的位置，value为AsncTask outputs中的名称
    private int order;  //“任务步”排行，第几步

    public TaskStep(String name, FuncExecute funcExecute, Object[] params, String outputName) {
        this.name = name;
        this.funcExecute = funcExecute;
        this.params = params;
        this.outputName = outputName;
        this.refrenceParams = new HashMap<>();
    }

    public static TaskStep newInstance(String name, FuncExecute funcExecute, Object[] params, String outputName) {
        return new TaskStep(name, funcExecute, params,  outputName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AsyncEvent getEvent() {
        return event;
    }

    public void setEvent(AsyncEvent event) {
        this.event = event;
    }

    public FuncExecute getFuncExecute() {
        return funcExecute;
    }

    public void setFuncExecute(FuncExecute funcExecute) {
        this.funcExecute = funcExecute;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public boolean isHasOutput() {
        return hasOutput;
    }

    public void setHasOutput(boolean hasOutput) {
        this.hasOutput = hasOutput;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isRefrenceInput() {
        return refrenceInput;
    }

    public void setRefrenceInput(boolean refrenceInput) {
        this.refrenceInput = refrenceInput;
    }

    public Map<Integer, String> getRefrenceParams() {
        return refrenceParams;
    }

    public void addRefrenceParams(Integer index, String refrenceParamName){
        this.refrenceParams.put(index, refrenceParamName);
    }

    public boolean configParam( Map<String,Object> outputs){
        for(Integer paramIndex : this.refrenceParams.keySet()){
            if( !outputs.containsKey(this.refrenceParams.get(paramIndex))){
                return false;
            }
            this.params[paramIndex] = outputs.get(this.refrenceParams.get(paramIndex));
        }
        return true;
    }

    public Object execute() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            InstantiationException, TaskExecuteException {

            return this.funcExecute.execute(this.params);

    }
}
