package asynchronous;

/**
 * Created by wlp on 2019/8/17
 * 用于业务中断异常，当某步执行失败时抛出，主要考虑失败回滚.
 */
public class TaskExecuteException extends Exception {

    private String message;
    public TaskExecuteException(String message) {
       this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
