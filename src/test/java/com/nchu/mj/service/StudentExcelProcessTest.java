package com.nchu.mj.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.nchu.mj.bo.StudentCourseGoalOptions;
import com.nchu.mj.bo.StudentExcelProcessOptions;
import org.apache.poi.util.IOUtils;
import org.junit.jupiter.api.Test;

/**
 * @author cjh
 * @since 2022/05/16
 * @description Excel 处理单元测试
 */
public class StudentExcelProcessTest {

    @Test
    public void testProcess() throws IOException {
        InputStream testStream = StudentExcelProcessTest.class.getClassLoader().getResourceAsStream(
            "files/student_example_input.xls");
        List<StudentExcelProcessOptions> options = new ArrayList<>();
        options.add(getStudentExcelProcessOptions(0, "A3", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(1, "制图基础", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(2, "三视图", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(3, "组合体", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(4, "A3", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(5, "表达方法", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(6, "A3", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(7, "折合", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(8, "成绩", 1.2, 1.3));
        options.add(getStudentExcelProcessOptions(9, "达成值", 1.2, 1.3));
        byte[] result = StudentExcelProcess.build().process(IOUtils.toByteArray(testStream), options);
        FileOutputStream out = new FileOutputStream("out.xls");
        out.write(result);
    }

    private static StudentExcelProcessOptions getStudentExcelProcessOptions(int courseIndex, String courseName, double... weights) {
        StudentExcelProcessOptions options = new StudentExcelProcessOptions();
        options.setCourseForExcelIndex(courseIndex);
        options.setCourseName(courseName);
        options.setCourseGoalNumber(weights.length);
        List<StudentCourseGoalOptions> courseGoalOptionsList = new ArrayList<>();
        int i = 0;
        for (double weight : weights) {
            StudentCourseGoalOptions courseGoalOptions = new StudentCourseGoalOptions();
            courseGoalOptions.setAim("目标" + (++i));
            courseGoalOptions.setWeight(weight);
            courseGoalOptionsList.add(courseGoalOptions);
        }
        options.setCourseGoalOptionsList(courseGoalOptionsList);
        return options;
    }
}
