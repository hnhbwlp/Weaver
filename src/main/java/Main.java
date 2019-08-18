import asynchronous.*;
import javafx.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlp on 2019/8/18.
 */
public class Main
{
    static AsyncWorkThreadPool asyncWorkThreadPool;
    /**
     * 做午饭： 分为买菜、洗菜、烹饪三步
     *
     */
    public static AsyncTask MakeLunch(){

        List<String> foods = new ArrayList<>();
        AsyncTask cookTask = new AsyncTask();
        AsyncEvent event = cookTask.getEvent();

        //1.买菜
        //模拟：每间隔1s买一种菜
        FuncExecute shop = p -> {
            String foodName;
            for (int i =0; i< 5; i++){
                foodName = "food"+i;
                foods.add(foodName);
                event.setResult("shoping: "+foodName);
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            return null;
        };
        TaskStep shopStep = TaskStep.newInstance("shop",shop, new Object[]{}, null);
        shopStep.setHasOutput(false);
        cookTask.addStep(shopStep);

        //2. wash
        //模拟：每间隔1s买一种菜
        FuncExecute wash = p -> {
            String foodName;
            for (int i =0; i< foods.size(); i++){
               foodName = foods.get(i);
                event.setResult("washing: "+foodName);
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            return null;
        };
        TaskStep washStep = TaskStep.newInstance("wash",wash, new Object[]{}, null);
        washStep.setHasOutput(false);
        cookTask.addStep(washStep);

        //3. cooking
        //模拟：每间隔1s买一种菜
        FuncExecute cook = p -> {
            String foodName;
            for (int i =0; i< foods.size(); i++){
                foodName = foods.get(i);
                event.setResult("cooking: "+foodName);
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            return null;
        };
        TaskStep cookStep = TaskStep.newInstance("cook",cook, new Object[]{}, null);
        cookStep.setHasOutput(false);
        cookTask.addStep(cookStep);

        return cookTask;
    }
   public static void main(String[] args ){
       //创建线程池
       asyncWorkThreadPool = AsyncWorkThreadPool.newInstance();
       asyncWorkThreadPool.start();
       //获取线程池中事件管理器
       EventMananger eventMananger = asyncWorkThreadPool.getEventMananger();
       //做午饭
       AsyncTask cookTask = new Main().MakeLunch();
       asyncWorkThreadPool.addTask(cookTask);

       //阻塞主进程退出
       for(int i =0 ;i<7; i++){
           //查看做饭进度
           System.out.println("============"+i+"=============");
           System.out.println(cookTask.getEvent().getResult());
           try{
               Thread.sleep(3000);
           }catch (InterruptedException e){
           }
       }

   }
}
