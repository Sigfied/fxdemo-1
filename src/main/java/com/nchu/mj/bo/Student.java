package com.nchu.mj.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GYJ
 */
public class Student {
    private String no = "";
    private String stuId = "";
    private String stuName = "";
    private ArrayList<Grade> grade = new ArrayList<>();

    public Student() {

    }

    public Student(String no, String stuId, String stuName) {
        this.no = no;
        this.stuId = stuId;
        this.stuName = stuName;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public ArrayList<Grade> getGrade() {
        return grade;
    }

    public void addOldGrade(Grade grade) {
        this.grade.add(grade);
    }

    public void createData() {
        for (Grade gd : this.getGrade()) {
            if ("达成值".equals(gd.getClassName())) {
                culReach();
                break;
            }
            gd.createData();
        }
    }

    /**
     * 生成达成值和理论总评的成绩
     */
    public void culReach() {
        String key;
        List<String> className = new ArrayList<>();
        int index = 0;
        for (Grade gd : grade) {
            if ("折合".equals(gd.getClassName())) {
                index = grade.indexOf(gd);
                break;
            }
        }
        //index为折合在表中的下标，+1为成绩，+2为达成值
        //遍历达成值的目标
        for (Map.Entry<String, Double> entry : grade.get(index + 2).getWeigth().entrySet()) {
            key = entry.getKey();
            className.add(key);
        }
        double sum;
        for (String name : className) {
            sum = 0;
            int count = index;
            for (int i = 0; i < index; i++) {
                if (grade.get(i).getNewGrade().get(name) != null) {
                    sum += grade.get(i).getNewGrade().get(name) / grade.get(i).getWeigth().get(name);
                } else {
                    count--;
                }
            }
            if (count == index) {
                sum = sum / index / 100;
            } else {
                sum = sum / count / 100;
            }
            grade.get(index + 2).getNewGrade().put(name, Double.valueOf(String.format("%.2f", sum)));
        }
//          Map<String,Double> tempWeight = grade.get(index).getWeigth();
//        Map<String,Double> tempGrade = grade.get(index).getNewGrade();
//        Map<String,Double> GradeWeight = grade.get(index+1).getWeigth();
//        Map<String,Double> GradeGrade = grade.get(index+1).getNewGrade();
//        //遍历达成值的目标
//        for (Map.Entry<String, Double> entry : grade.get(index + 2).getWeigth().entrySet()){
//            key = entry.getKey();
//            className.add(key);
//        }
//        for (String name:className) {
//            sum = 0;
//            sum += tempGrade.get(name) / tempWeight.get(name) + GradeGrade.get(name) / GradeWeight.get(name);
//            sum /= 200;
//            grade.get(index + 2).getNewGrade().put(name, Double.valueOf(String.format("%.2f", sum)));
//        }
        //一下为理论总评算法
    }

    //这个方法得到一个学生的所有数据结构
    public ArrayList<DealDataStructure> createStructure() {
        ArrayList<DealDataStructure> dealDataStructures = new ArrayList<>();
        for (Grade gd : grade) {
            if ("折合".equals(gd.getClassName())) { break; } else {
                for (String key : gd.getWeigth().keySet()) {
                    dealDataStructures.add(new DealDataStructure(gd.getClassName(), key, gd.getWeigth().get(key),
                        gd.getNewGrade().get(key)));
                }
            }
        }
        return dealDataStructures;
    }
}
