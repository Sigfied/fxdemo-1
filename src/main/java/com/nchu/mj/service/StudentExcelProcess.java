package com.nchu.mj.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nchu.mj.bo.DealDataStructure;
import com.nchu.mj.bo.Grade;
import com.nchu.mj.bo.Student;
import com.nchu.mj.bo.StudentExcelProcessOptions;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * 学生信息 Excel 处理器
 * StudentExcelProcess.build().process(anyFileStream)
 */
public class StudentExcelProcess {
    private Student readhead = new Student("0", "", "");
    private List<Student> list = new ArrayList<>();

    public static StudentExcelProcess build() {
        return new StudentExcelProcess();
    }

    public byte[] process(byte[] fileBytes, List<StudentExcelProcessOptions> optionsList) throws IOException {
        readHead(fileBytes);
        for (int i = 0; i < readhead.getGrade().size(); i++) {
            //遍历课程目标个数，从第一个作业开始，访问它的课程目标数。
            int finalI = i;
            StudentExcelProcessOptions matchOptions = optionsList.stream()
                .filter(options -> options.getCourseForExcelIndex().equals(finalI))
                .findFirst().orElseThrow(() -> new RuntimeException("配置不合法，表头第" + finalI + "列没有配置覆盖"));
            for (int k = 0; k < matchOptions.getCourseGoalNumber(); k++) {
                String aim = matchOptions.getCourseGoalOptionsList().get(k).getAim();
                double weight = matchOptions.getCourseGoalOptionsList().get(k).getWeight();
                System.out.println(aim + "\t" + weight);
                setWeight(readhead.getGrade().get(i).getClassName(), aim, weight);
            }
        }
        readExcel(fileBytes);
        dealData(list);
        calculate();
        return createExcel();
    }

    /**
     * @param aimList    课程目标的列表，把所有作业的课程目标都放在里面，里面有很多重复项
     * @param weightList 对应课程目标权重的列表。
     * @param aimNumber  给每次作业设置课程目标个数，比如:第一次作业有3个课程目标，则aimNumber.add(3);
     */
    public byte[] process(byte[] fileBytes, List<Integer> aimNumber, List<String> aimList, List<Double> weightList)
        throws IOException {
        readHead(fileBytes);
        int index = 0;
        for (int i = 0; i < readhead.getGrade().size(); i++) {
            //遍历课程目标个数，从第一个作业开始，访问它的课程目标数。
            for (int k = 0; k < aimNumber.get(i); k++) {
                String aim = aimList.get(index);
                double weight = weightList.get(index);
                System.out.println(aim + "\t" + weight);
                index++;
                setWeight(readhead.getGrade().get(i).getClassName(), aim, weight);
            }
        }
        readExcel(fileBytes);
        dealData(list);
        calculate();
        return createExcel();
    }

    /**
     * 生成Excel
     */
    private byte[] createExcel() throws IOException {
        //创建课程序列号累计
        // 创建一个Excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建一个工作表
        HSSFSheet sheet = workbook.createSheet("学生表一");
        // 添加表头行

        //从这里开始写第一行
        HSSFRow hssfRow0 = sheet.createRow(0);
        // 设置单元格格式居中

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 创建一个居中格式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        //        HSSFCellStyle cellStyle = workbook.createCellStyle();
        //课程序列需要循环，需要一定制空
        int tempCol = 2;
        for (int i = 0; i < readhead.getGrade().size(); i++) {
            tempCol += readhead.getGrade().get(i).getWeigth().size();
            HSSFCell headCell = hssfRow0.createCell(tempCol);
            headCell.setCellValue(readhead.getGrade().get(i).getClassName());
            headCell.setCellStyle(cellStyle);
            if ("成绩".equals(readhead.getGrade().get(i).getClassName())) {
                tempCol += 1;
                headCell = hssfRow0.createCell(tempCol);
                headCell.setCellValue("理论总评");
                headCell.setCellStyle(cellStyle);

                tempCol += readhead.getGrade().get(i + 1).getWeigth().size();
                headCell = hssfRow0.createCell(tempCol);
                headCell.setCellValue("达成值");
                headCell.setCellStyle(cellStyle);
                break;
            }
        }
        tempCol = 2;

        //从这里开始编写第二和第三行的后部分
        HSSFRow hssfRow1 = sheet.createRow(1);
        HSSFRow hssfRow2 = sheet.createRow(2);

        //所有成绩有多少个标识
        //这里使用了map的foreach，暂时不写

        HSSFCell headCell = hssfRow1.createCell(0);
        Student student1 = list.get(0);
        for (Grade gd1 : student1.getGrade()) {
            for (Map.Entry<String, Double> entry : gd1.getWeigth().entrySet()) {
                if ("达成值".equals(gd1.getClassName())) {
                    tempCol++;
                    headCell = hssfRow1.createCell(tempCol + 1);
                    headCell.setCellValue(entry.getKey());
                    headCell.setCellStyle(cellStyle);

                    headCell = hssfRow2.createCell(tempCol + 1);
                    headCell.setCellValue(entry.getValue());
                    headCell.setCellStyle(cellStyle);
                } else if ("理论成绩".equals(gd1.getClassName())) {

                } else {
                    tempCol++;
                    headCell = hssfRow1.createCell(tempCol);
                    headCell.setCellValue(entry.getKey());
                    headCell.setCellStyle(cellStyle);

                    headCell = hssfRow2.createCell(tempCol);
                    headCell.setCellValue(entry.getValue());
                    headCell.setCellStyle(cellStyle);
                }
            }
        }
        headCell = hssfRow2.createCell(0);
        headCell.setCellValue("序号");
        headCell.setCellStyle(cellStyle);

        headCell = hssfRow2.createCell(1);
        headCell.setCellValue("学号");
        headCell.setCellStyle(cellStyle);

        headCell = hssfRow2.createCell(2);
        headCell.setCellValue("姓名");
        headCell.setCellStyle(cellStyle);

        int temprow = 3;

        for (Student student : list) {
            tempCol = 2;
            HSSFRow hssfRow = sheet.createRow(temprow);
            headCell = hssfRow.createCell(0);
            headCell.setCellValue(student.getNo());
            headCell.setCellStyle(cellStyle);
            headCell = hssfRow.createCell(1);
            headCell.setCellValue(student.getStuId());
            headCell.setCellStyle(cellStyle);
            headCell = hssfRow.createCell(2);
            headCell.setCellValue(student.getStuName());
            headCell.setCellStyle(cellStyle);
            for (Grade gd : student.getGrade()) {
                int count = 0;
                for (Map.Entry<String, Double> entry : gd.getNewGrade().entrySet()) {
                    if ("达成值".equals(gd.getClassName())) {
                        count++;
                        tempCol++;
                        headCell = hssfRow.createCell(tempCol + 1);
                        headCell.setCellValue(entry.getValue());
                        headCell.setCellStyle(cellStyle);
                    } else if ("理论成绩".equals(gd.getClassName())) {
                        System.out.println(tempCol - count - 1 + " 理论总评" + entry.getValue());
                        headCell = hssfRow.createCell(tempCol - count - 1 - 1);
                        headCell.setCellValue(entry.getValue());
                        headCell.setCellStyle(cellStyle);
                    } else {
                        tempCol++;
                        headCell = hssfRow.createCell(tempCol);
                        headCell.setCellValue(entry.getValue());
                        headCell.setCellStyle(cellStyle);
                    }
                }
            }
            temprow++;
        }

        // 保存Excel文件
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return out.toByteArray();
    }

    /**
     * 读取Excel的头
     */
    private void readHead(byte[] fileBytes) {
        HSSFSheet hssfSheet = initWorkBook(fileBytes);
        //读取头
        // 将单元格中的内容存入集合
        HSSFRow hssfRow0 = hssfSheet.getRow(0);

        for (int i = 3; i < hssfRow0.getPhysicalNumberOfCells(); i++) {
            if (hssfRow0.getCell(i) == null) {
                continue;
            }

            HSSFCell cell = hssfRow0.getCell(i);
            if (cell == null) {
                continue;
            }
            if ("".equals(cell.getStringCellValue())) {
                continue;
            }
            cell.setCellType(Cell.CELL_TYPE_STRING);
            Grade temp = new Grade();
            temp.setClassName(cell.getStringCellValue());
            readhead.getGrade().add(temp);
            if ("成绩".equals(cell.getStringCellValue())) {
                break;
            }
        }
        Grade temp = new Grade();
        temp.setClassName("达成值");
        readhead.getGrade().add(temp);
    }

    /**
     * 读取Excel
     */
    private void readExcel(byte[] fileBytes) {
        HSSFSheet hssfSheet = initWorkBook(fileBytes);
        //读取头
        // 将单元格中的内容存入集合
        for (int rowNum = 1; rowNum < hssfSheet.getLastRowNum(); rowNum++) {
            HSSFRow hssfRow = hssfSheet.getRow(rowNum);
            if (hssfRow == null) {
                continue;
            }
            // 将单元格中的内容存入集合
            Student student = new Student();

            HSSFCell cell = hssfRow.getCell(0);
            if (cell == null) {
                continue;
            }

            if (("").equals(cell.toString())) {
                break;
            }
            cell.setCellType(Cell.CELL_TYPE_STRING);
            student.setNo(cell.getStringCellValue());

            cell = hssfRow.getCell(1);
            if (cell == null) {
                continue;
            }
            cell.setCellType(Cell.CELL_TYPE_STRING);
            student.setStuId(cell.getStringCellValue());

            cell = hssfRow.getCell(2);
            if (cell == null) {
                continue;
            }
            student.setStuName(cell.getStringCellValue());

            int count = 0;
            for (int i = 3; i < hssfRow.getPhysicalNumberOfCells(); i++) {
                cell = hssfRow.getCell(i);
                if (cell == null) {
                    continue;
                }
                if ("".equals(cell.toString())) {
                    count++;
                    continue;
                }
                String value = "";
                switch (cell.getCellTypeEnum()) {
                    case NUMERIC:
                        int numericCellValue = (int)cell.getNumericCellValue();
                        value += numericCellValue;
                        break;
                    case STRING:
                        String stringCellValue = cell.getStringCellValue();
                        value += stringCellValue;
                        break;
                    case BLANK:
                        value += "0";
                        break;
                    default:
                        break;
                }
                Grade grade = new Grade();
                grade.setOldGrade(Double.parseDouble(value));
                student.addOldGrade(grade);
                if ("成绩".equals(readhead.getGrade().get(i - 3 - count).getClassName())) {
                    break;
                }
            }

            list.add(student);
        }
    }

    private HSSFSheet initWorkBook(byte[] fileBytes) {
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(new ByteArrayInputStream(fileBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 循环工作表
        assert workbook != null;
        return workbook.getSheetAt(0);
    }

    /**
     * 设置权重到readHead中
     */
    private void setWeight(String clazzName, String aim, double weigths) {
        for (int i = 0; i < readhead.getGrade().size(); i++) {
            if (readhead.getGrade().get(i).getClassName().equals(clazzName)) {
                readhead.getGrade().get(i).getWeigth().put(aim, weigths);
            }
        }
    }

    /**
     * 根据权重生成数据
     */
    private void dealData(List<Student> students) {
        //每个学生
        for (Student student : students) {
            student.getGrade().add(new Grade("达成值", 0));
            student.getGrade().add(new Grade("理论成绩", 0));
            //每个课
            for (int j = 0; j < readhead.getGrade().size(); j++) {
                student.getGrade().get(j).setClassName(readhead.getGrade().get(j).getClassName());
                for (Map.Entry<String, Double> entry : readhead.getGrade().get(j).getWeigth().entrySet()) {
                    student.getGrade().get(j).getWeigth().put(entry.getKey(), entry.getValue());
                }
            }
            student.createData();
        }
    }

    /**
     * 模糊算法，先使用新的数据和结构
     */
    private void calculate() {
        int index = 0;
        for (Student student : list) {
            for (Grade gd : student.getGrade()) {
                if ("折合".equals(gd.getClassName())) {
                    index = student.getGrade().indexOf(gd);
                    break;
                }
            }
            //每个学生
            //每个aim
            List<DealDataStructure> listNew = student.createStructure();
            double sum = 0;
            double count = 0;
            double weightFolder;
            double gradeFolder;
            //折合每个weight
            for (String keyword : student.getGrade().get(index).getWeigth().keySet()) {
                //折合每个权重
                weightFolder = student.getGrade().get(index).getWeigth().get(keyword);
                //折合每个结果值
                gradeFolder = student.getGrade().get(index).getNewGrade().get(keyword);
                for (DealDataStructure ds : listNew) {
                    if (ds.aim.equals(keyword)) {
                        count++;
                        sum += (ds.grade / ds.weight);
                    }
                }
                sum /= count;
                sum *= weightFolder;
                if (Math.abs(sum - gradeFolder) < 1.2) {
                    sum = (int)sum;
                    student.getGrade().get(index).getNewGrade().put(keyword, sum);
                }
            }
        }
        //每个学生
        for (Student student : list) {
            for (Grade gd : student.getGrade()) {
                for (String keyword : gd.getNewGrade().keySet()) {
                    if ("折合".equals(gd.getClassName())) {
                        break;
                    }
                    if ("成绩".equals(gd.getClassName())) {
                        break;
                    }
                    if ("达成值".equals(gd.getClassName())) {
                        break;
                    }
                    double gradeFolder = gd.getNewGrade().get(keyword);
                    gradeFolder = getGradeFolder(gd, keyword, gradeFolder);
                    gd.getNewGrade().put(keyword, gradeFolder);
                }
            }
        }
    }

    private double getGradeFolder(Grade gd, String keyword, double gradeFolder) {
        if (gd.getWeigth().get(keyword) != null) {
            if (gradeFolder > 40) {
                gradeFolder = Double.parseDouble(
                    String.format("%.2f", gradeFolder + Math.random() * gd.getWeigth().get(keyword) * 2.5));
            } else {
                gradeFolder = Double.parseDouble(
                    String.format("%.2f", gradeFolder - Math.random() * gd.getWeigth().get(keyword) * 2.5));
            }
        } else {
            if (gradeFolder > 30) {
                gradeFolder = Double.parseDouble(String.format("%.2f", gradeFolder + Math.random() * 0.5 * 2.5));
            } else {
                gradeFolder = Double.parseDouble(String.format("%.2f", gradeFolder - Math.random() * 0.5 * 2.5));
            }
        }
        return gradeFolder;
    }
}



























