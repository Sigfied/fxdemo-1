package com.nchu.mj.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GYJ,FLZ
 */
public class Grade {
    private String className = "";
    private double oldGrade = 0;
    private Map<String, Double> weigth = new HashMap<>();
    private Map<String, Double> newGrade = new HashMap<>();

    public Grade() {

    }

    public Grade(String className, double oldGrade) {
        this.className = className;
        this.oldGrade = oldGrade;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getOldGrade() {
        return oldGrade;
    }

    public void setOldGrade(double oldGrade) {
        this.oldGrade = oldGrade;
    }

    public Map<String, Double> getWeigth() {
        return weigth;
    }

    public Map<String, Double> getNewGrade() {
        return newGrade;
    }

    /**生成学生的分成绩*/
    public void createData() {
        for (Map.Entry<String, Double> entry : weigth.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();
            if("折合".equals(className)){
                newGrade.put(key, 0.0);
            }
            newGrade.put(key, value * oldGrade);
        }
    }
}