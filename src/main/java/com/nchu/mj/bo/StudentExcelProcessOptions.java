package com.nchu.mj.bo;

import java.util.List;

/**
 * 配置信息
 */
public class StudentExcelProcessOptions {

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程在 Excel 中表头的下标位置
     */
    private Integer courseForExcelIndex;

    /**
     * 课程目标个数
     */
    private Integer courseGoalNumber;

    /**
     * 课程目标详情
     */
    private List<StudentCourseGoalOptions> courseGoalOptionsList;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCourseForExcelIndex() {
        return courseForExcelIndex;
    }

    public void setCourseForExcelIndex(Integer courseForExcelIndex) {
        this.courseForExcelIndex = courseForExcelIndex;
    }

    public Integer getCourseGoalNumber() {
        return courseGoalNumber;
    }

    public void setCourseGoalNumber(Integer courseGoalNumber) {
        this.courseGoalNumber = courseGoalNumber;
    }

    public List<StudentCourseGoalOptions> getCourseGoalOptionsList() {
        return courseGoalOptionsList;
    }

    public void setCourseGoalOptionsList(List<StudentCourseGoalOptions> courseGoalOptionsList) {
        this.courseGoalOptionsList = courseGoalOptionsList;
    }
}
