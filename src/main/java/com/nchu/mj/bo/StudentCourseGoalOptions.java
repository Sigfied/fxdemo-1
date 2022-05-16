package com.nchu.mj.bo;


/**
 * @author cjh
 * @since 2022/05/16
 * @description 课程目标配置
 */
public class StudentCourseGoalOptions {

    /**
     * 名称
     */
    private String aim;

    /**
     * 权重
     */
    private Double weight;

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
