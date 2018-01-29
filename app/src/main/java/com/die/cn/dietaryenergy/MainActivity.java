package com.die.cn.dietaryenergy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText editText;
    private EditText input_text;
    private TextView textView;
    private TextView add_textView;
    private TextView lunch_textView;
    private TextView dinner_textView;
    private DietDBManager dbManager;
    /**
     * 一天摄入的总的能量
     */
    private int CALORY = 3000;
    /**
     * 早餐中主食和饮品的比值为3:1
     */
    private final int breakfastProportion = 4;

    /**
     * 早餐占一天中卡路里的中间值
     */
    private double breakfastMiddle = ((CALORY - 90) * 0.27);
    private double lunchMiddle = ((CALORY - 90) * 0.35);
    /**
     * 早餐卡路里每一份的值
     */
    private double breakfastEach = breakfastMiddle / breakfastProportion;
    /**
     * 午餐卡路里每一份的值
     */
    private double lunchEach = lunchMiddle / breakfastProportion;
    private MyBroadcastReceiver myBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.query).setOnClickListener(this);
        findViewById(R.id.change).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.lunch).setOnClickListener(this);
        findViewById(R.id.dinner).setOnClickListener(this);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        add_textView = findViewById(R.id.add_textView);
        lunch_textView = findViewById(R.id.lunch_textView);
        dinner_textView = findViewById(R.id.dinner_textView);
        input_text = findViewById(R.id.input_text);

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter data = new IntentFilter("data");
        registerReceiver(myBroadcastReceiver,data);


    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("data".equals(action)) {
                Toast.makeText(MainActivity.this,"数据导入完成",Toast.LENGTH_SHORT).show();
            }

            Bundle bundle = intent.getExtras();

        }
    }

    @Override
    public void onClick(View view) {

        //根据能量值重新计算比值
        String s = input_text.getText().toString();
        if (!TextUtils.isEmpty(s)) {
            CALORY = Integer.parseInt(s);
            /**
             * 早餐占一天中卡路里的中间值
             */
            breakfastMiddle = ((CALORY - 90) * 0.27);
            lunchMiddle = ((CALORY - 90) * 0.35);
            /**
             * 早餐卡路里每一份的值
             */
            breakfastEach = breakfastMiddle / breakfastProportion;
            /**
             * 午餐卡路里每一份的值
             */
            lunchEach = lunchMiddle / breakfastProportion;
        }

        int id = view.getId();
        if (R.id.btn == id) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dbManager = DietDBManager.getInstance(MainActivity.this);
                }
            }).start();
        } else if (R.id.query == id) {
            DietInfoBean dietInfoBean = DietDBManager.getInstance(this).getInfoById(editText.getText().toString().trim());
            textView.setText(dietInfoBean.toString().trim());
        } else if (R.id.change == id) {//早餐换一换
            getBreakfastInfo();
        } else if (R.id.add == id) {//加餐换一换
            getAddInfo();
        } else if (R.id.lunch == id) {//午餐换一换
            getLunchInfo(1);
        } else if (R.id.dinner == id) {//晚餐换一换
            getLunchInfo(2);
        }

    }

    /**
     * 获取午餐数据
     * 2代表查询晚餐
     * 1代表查询午餐
     */
    private void getLunchInfo(int status) {
        //包含主食和全
        double protein = 0;
        double fat = 0;
        double carbohydrate = 0;
        List<DietInfoBean> Foods;
        if (2 == status) {
            Foods = DietDBManager.getInstance(this).queryDinner("主食", "全");
        } else {

            Foods = DietDBManager.getInstance(this).queryLunchFood2("主食", "全");
        }
        int foodNum = 0;
        int mainCourseNum = 0;
        int dishNum = 0;
        int soupNum = 0;
        String foodName = "";
        String mainCourseName = "";
        String dishName = "";
        String soupName = "";
        String lunchInfo = "";
        if (0 != Foods.size()) {
            //随机产生选中早餐主食
            int addPostion = (int) (Math.random() * (Foods.size() - 1));
            DietInfoBean dietInfoBean = Foods.get(addPostion);
            double calory = 1d * dietInfoBean.getCalory() / 100;
            foodNum = (int) (lunchEach * 3 / calory);//主食的g数

            //含有蛋白质的量
            protein = 1d * foodNum * dietInfoBean.getProtein() / 100;
            //含有脂肪的量
            fat = 1d * foodNum * dietInfoBean.getFat() / 100;
            //含有碳水化合物的量
            carbohydrate = 1d * foodNum * dietInfoBean.getCarbohydrate() / 100;

            //食物名称
            foodName = dietInfoBean.getName();
            String variety = dietInfoBean.getVariety();
            if ("全".equals(variety)) {//组合为全+汤 3:1
                List<DietInfoBean> soups;
                if (2 == status) {
                    soups = DietDBManager.getInstance(this).queryDinnerFood("汤");
                } else {

                    soups = DietDBManager.getInstance(this).queryLunchFood("汤");
                }
                if (0 != soups.size()) {
                    //随机数
                    int soupPostion = (int) (Math.random() * soups.size());
                    DietInfoBean soupBean = soups.get(soupPostion);
                    double soupcalory = 1d * soupBean.getCalory() / 100;
                    //配菜或者汤的g数
                    soupNum = (int) (lunchEach / soupcalory);

                    //含有蛋白质的量
                    protein = protein + 1d * soupNum * soupBean.getProtein() / 100;
                    //含有脂肪的量
                    fat = fat + 1d * soupNum * soupBean.getFat() / 100;
                    //含有碳水化合物的量
                    carbohydrate = carbohydrate + 1d * soupNum * soupBean.getCarbohydrate() / 100;
                    //食物名称
                    soupName = soupBean.getName();
                }
                lunchInfo = foodName + foodNum + "g " + "   " + soupName + soupNum + "g ";
            } else {//主食+主菜+配菜/汤 比例为2:1:1
                foodNum = (int) (lunchEach * 2 / calory);//主食的g数
                //主菜
                List<DietInfoBean> mainCourseFoods = DietDBManager.getInstance(this).queryLunchFood("主菜");
                if (0 != mainCourseFoods.size()) {
                    //随机产生选中早餐主食
                    int mainCoursePostion = (int) (Math.random() * mainCourseFoods.size());
                    DietInfoBean mainCourseBean = mainCourseFoods.get(mainCoursePostion);
                    double mainCoursecalory = 1d * mainCourseBean.getCalory() / 100;
                    //主菜的g数
                    mainCourseNum = (int) (lunchEach / mainCoursecalory);
                    //含有蛋白质的量
                    protein = protein + 1d * mainCourseNum * mainCourseBean.getProtein() / 100;
                    //含有脂肪的量
                    fat = fat + 1d * mainCourseNum * mainCourseBean.getFat() / 100;
                    //含有碳水化合物的量
                    carbohydrate = carbohydrate + 1d * mainCourseNum * mainCourseBean.getCarbohydrate() / 100;
                    //食物名称
                    mainCourseName = mainCourseBean.getName();
                }
                //配菜或者汤
                List<DietInfoBean> dishs = DietDBManager.getInstance(this).queryLunchFood2("配菜", "汤");
                if (0 != dishs.size()) {
                    //随机数
                    int dishPostion = (int) (Math.random() * dishs.size());
                    DietInfoBean dishBean = dishs.get(dishPostion);
                    double dishcalory = 1d * dishBean.getCalory() / 100;
                    //配菜或者汤的g数
                    dishNum = (int) (lunchEach / dishcalory);
                    //含有蛋白质的量
                    protein = protein + 1d * dishNum * dishBean.getProtein() / 100;
                    //含有脂肪的量
                    fat = fat + 1d * dishNum * dishBean.getFat() / 100;
                    //含有碳水化合物的量
                    carbohydrate = carbohydrate + 1d * dishNum * dishBean.getCarbohydrate() / 100;
                    //食物名称
                    dishName = dishBean.getName();
                }
                lunchInfo = foodName + "   " + foodNum + " g " + mainCourseName + "   " + mainCourseNum + " g " +
                        dishName + "   " + dishNum + " g ";
            }
            double specific1 = protein / (fat * 2.25d);
            double specific2 = (fat * 2.25d) / carbohydrate;
            if (specific1 > 0.3 && specific1 < 0.75 && specific2 > 0.31 && specific2 < 0.55) {
                if (2 == status) {
                    dinner_textView.setText(lunchInfo);
                } else {
                    lunch_textView.setText(lunchInfo);
                }
            } else {
                getLunchInfo(status);
            }
        }

    }

    /**
     * 获取加餐数据
     */
    private void getAddInfo() {
        double protein = 0;
        double fat = 0;
        double carbohydrate = 0;
        List<DietInfoBean> breakfastFoods = DietDBManager.getInstance(this).queryAddFood();
        if (0 != breakfastFoods.size()) {
            //随机产生选中早餐主食
            int addPostion = (int) (Math.random() * breakfastFoods.size());
            DietInfoBean dietInfoBean = breakfastFoods.get(addPostion);
            double calory = 1d * dietInfoBean.getCalory() / 100;
            int addFoodNum = (int) (90 / calory);//主食的g数
            //含有蛋白质的量
            protein = 1d * addFoodNum * dietInfoBean.getProtein() / 100;
            //含有脂肪的量
            fat = 1d * addFoodNum * dietInfoBean.getFat() / 100;
            //含有碳水化合物的量
            carbohydrate = 1d * addFoodNum * dietInfoBean.getCarbohydrate() / 100;
            //食物名称
            String addFoodName = dietInfoBean.getName();
            double specific1 = protein / (fat * 2.25d);
            double specific2 = (fat * 2.25d) / carbohydrate;
            if (specific1 > 0.3 && specific1 < 0.75 && specific2 > 0.31 && specific2 < 0.55) {
                add_textView.setText(addFoodName + addFoodNum + "g ");
            } else {
                getAddInfo();
            }
        }
    }

    /**
     * 获取早餐数据
     */
    private void getBreakfastInfo() {
        int breakfastFoodNum = 0;
        String breakfastFoodName = null;
        String breakfastSoupName = null;
        int soupNum = 0;
        double protein = 0;
        double fat = 0;
        double carbohydrate = 0;
        List<DietInfoBean> breakfastFoods = DietDBManager.getInstance(this).queryBreakfastFood();
        if (0 != breakfastFoods.size()) {
            //随机产生选中早餐主食
            int breakfastPostion = (int) (Math.random() * (breakfastFoods.size()));
            DietInfoBean dietInfoBean = breakfastFoods.get(breakfastPostion);
            double calory = 1d * dietInfoBean.getCalory() / 100;
            breakfastFoodNum = (int) (breakfastEach * 3 / calory);//主食的g数

            //含有蛋白质的量
            protein = 1d * breakfastFoodNum * dietInfoBean.getProtein() / 100;
            //含有脂肪的量
            fat = 1d * breakfastFoodNum * dietInfoBean.getFat() / 100;
            //含有碳水化合物的量
            carbohydrate = 1d * breakfastFoodNum * dietInfoBean.getCarbohydrate() / 100;
            Log.d("huang主食占比", protein + "  " + fat + "  " + carbohydrate + "  ");
            //食物名称
            breakfastFoodName = dietInfoBean.getName();
        }
        //饮品
        List<DietInfoBean> breakfastSoups = DietDBManager.getInstance(this).queryBreakfastSoup();
        if (0 != breakfastSoups.size()) {
            int breakfastSoupPostion = (int) (Math.random() * breakfastSoups.size());
            DietInfoBean dietInfoBean = breakfastSoups.get(breakfastSoupPostion);

            //除以100是每g的卡洛里
            double calory = 1d * dietInfoBean.getCalory() / 100;
            //饮品的g数
            soupNum = (int) (breakfastEach / calory);
            //含有蛋白质的量
            protein = protein + 1d * soupNum * dietInfoBean.getProtein() / 100;
            //含有脂肪的量
            fat = fat + 1d * soupNum * dietInfoBean.getFat() / 100;
            //含有碳水化合物的量
            carbohydrate = carbohydrate + 1d * soupNum * dietInfoBean.getCarbohydrate() / 100;
            Log.d("huang总占比", protein + "  " + fat + "  " + carbohydrate + "  ");
            breakfastSoupName = dietInfoBean.getName();
        }
        double specific1 = protein / (fat * 2.25d);
        double specific2 = (fat * 2.25d) / carbohydrate;
        if (specific1 > 0.3 && specific1 < 0.75 && specific2 > 0.31 && specific2 < 0.55) {
            textView.setText(breakfastFoodName + "   " + breakfastFoodNum + "g " + breakfastSoupName + "   " + soupNum + "g");
        } else {
            //当不符合规则的时候继续筛选
            getBreakfastInfo();
//            String s = breakfastFoodName + "   " + breakfastFoodNum + "g " + breakfastSoupName + "   " + soupNum + "g";
//            textView.setText("不符合规则===比值1=="+specific1+"比值2=="+specific2+"食物为===="+s);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }
}
