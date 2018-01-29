package com.die.cn.dietaryenergy;

/**
 * {食物相关bean}
 * Created by huangchaobo on 2018/1/25 13.
 * 邮箱：huangchaobo@miao.cn
 */

public class DietInfoBean {
   private int id;
   private int minimum;
   private String name;
   private String meal;
   private String variety;
   private int calory;
   private float protein;
   private float fat;
   private float carbohydrate;



    public DietInfoBean(String name, int calory, float protein, float fat, float carbohydrate, String meal, String variety, int minimum) {
        this.minimum = minimum;
        this.name = name;
        this.meal = meal;
        this.variety = variety;
        this.calory = calory;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public int getCalory() {
        return calory;
    }

    public void setCalory(int calory) {
        this.calory = calory;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(float carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    @Override
    public String toString() {
        return "DietInfoBean{" +
                "id=" + id +
                ", minimum=" + minimum +
                ", name='" + name + '\'' +
                ", meal='" + meal + '\'' +
                ", variety='" + variety + '\'' +
                ", calory=" + calory +
                ", protein=" + protein +
                ", fat=" + fat +
                ", carbohydrate=" + carbohydrate +
                '}';
    }
}
