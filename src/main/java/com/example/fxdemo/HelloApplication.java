package com.example.fxdemo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloApplication extends Application {
    static Student readhead = new Student("0", "", "");
    private Scene scene = null;
    private static final int WIDTH = 800;
    private static final int HEIGTH = 400;
    private final MenuBar menuBar = new MenuBar();
    private final Menu fileMenu = new Menu("文件");
    private final MenuItem openFile = new MenuItem("打开文件");
    private final MenuItem exit = new MenuItem("关闭");
    private static List<Student> list = new ArrayList<>();
    private List<TextField> textFieldList = new ArrayList<>();
    String filePath;

    private void initComponents(Stage stage) {
        initEvent(stage);
        scene = new Scene(new BorderPane(), WIDTH, HEIGTH);
        BorderPane root = (BorderPane) scene.getRoot();
        menuBar.getMenus().addAll(fileMenu);
        fileMenu.getItems().addAll(openFile, exit);
        root.setTop(menuBar);
    }

    @Override
    public void start(Stage stage) {
        initComponents(stage);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initEvent(Stage stage) {
        //stage.close();
        fileMenu.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("XLS", "*.xls"),
                    new FileChooser.ExtensionFilter("XLSX", "*.xlsx")
            );
            File file = fileChooser.showOpenDialog(new Stage());
            if (file == null) {
                System.exit(0);
            }
            filePath = file.getPath();
            readHead(filePath);

            scene = new Scene(new GridPane(), WIDTH, HEIGTH);
            GridPane bp = (GridPane) scene.getRoot();
            for (int i = 0; i < readhead.getGrade().size(); i++) {
                TextField tf = new TextField();
                tf.setPromptText("输入课程目标数");
                tf.setTextFormatter(new TextFormatter<String>(change -> {
                    if (change.getText().matches("[0-9]*")) {
                        return change;
                    }
                    return null;
                }));
                bp.add(new Text(readhead.getGrade().get(i).getClassName()), 0, i);
                bp.add(tf, 1, i);
                textFieldList.add(tf);
            }


            Button btSubmit = new Button("Submit");
            bp.add(btSubmit, 1, readhead.getGrade().size());
            btSubmit.setOnAction(actionEvent -> {
                List<Integer> aimNumber = new ArrayList<>();
                for (TextField tf : textFieldList) {
                    aimNumber.add(Integer.valueOf(tf.getText()));
                }
                scene = new Scene(new GridPane(), WIDTH, HEIGTH);
                GridPane gp = (GridPane) scene.getRoot();
                List<TextField> tfAimList = new ArrayList<>();
                List<TextField> tfWeightList = new ArrayList<>();
                int j;
                for (int i = 0; i < readhead.getGrade().size(); i++) {
                    gp.add(new Text(readhead.getGrade().get(i).getClassName()), 0, i);
                    j = 1;
                    for (int k = 0; k < aimNumber.get(i); k++) {
                        TextField aimtf = new TextField();
                        aimtf.setPromptText("目标名");
                        TextField weighttf = new TextField();
                        weighttf.setPromptText("权值");
                        gp.add(aimtf, j, i);
                        gp.add(weighttf, j + 1, i);
                        j += 2;
                        tfAimList.add(aimtf);
                        tfWeightList.add(weighttf);
                    }
                }
                Button button = new Button("Submit");
                gp.add(button, 1, readhead.getGrade().size());
                button.setOnAction(actionEvent1 -> {
                    int index = 0;
                    for (int i = 0; i < readhead.getGrade().size(); i++) {
                        for (int k = 0; k < aimNumber.get(i); k++) {
                            String aim = tfAimList.get(index).getText();
                            double weight = Double.parseDouble(tfWeightList.get(index).getText());
                            System.out.println(aim + "\t" + weight);
                            index++;
                            setWeight(readhead.getGrade().get(i).getClassName(), aim, weight);
                        }
                    }
                    readExcel(filePath);
                    dealData(list);

                    calculate();
                    createExcel();
                    Label label = new Label("原路径下新建一个的Excel");
                    Scene scene = new Scene(new GridPane(), WIDTH, HEIGTH);
                    ((GridPane) scene.getRoot()).add(label, 2, 2);
                    stage.setScene(scene);
                    stage.show();
                });
                stage.setScene(scene);
                stage.show();
            });
            stage.setScene(scene);
            stage.show();
        });
        exit.setOnAction(e -> System.exit(0));
    }
    /**
     * 生成Excel
     */
    private static void createExcel() {
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
        //多少个课
        int tempCol = 2;
        for(int i = 0 ; i < readhead.getGrade().size(); i++){
            tempCol += readhead.getGrade().get(i).getWeigth().size();
            HSSFCell headCell = hssfRow0.createCell(tempCol);
            headCell.setCellValue(readhead.getGrade().get(i).getClassName());
            headCell.setCellStyle(cellStyle);
            if("成绩".equals(readhead.getGrade().get(i).getClassName())) {
                tempCol += 1;
                headCell = hssfRow0.createCell(tempCol);
                headCell.setCellValue("理论总评");
                headCell.setCellStyle(cellStyle);

                tempCol += readhead.getGrade().get(i+1).getWeigth().size();
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
        for(Grade gd1 : student1.getGrade()) {
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

                    headCell = hssfRow2.createCell(tempCol );
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

        for(Student student : list){
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
            for(Grade gd : student.getGrade()){
                int count = 0;
                for (Map.Entry<String, Double> entry : gd.getNewGrade().entrySet()){
                    if("达成值".equals(gd.getClassName())){
                        count++;
                        tempCol++;
                        headCell = hssfRow.createCell(tempCol + 1);
                        headCell.setCellValue(entry.getValue());
                        headCell.setCellStyle(cellStyle);
                    } else if("理论成绩".equals(gd.getClassName())){
                        System.out.println(tempCol - count-1 + " 理论总评" +entry.getValue());
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
        try {
            OutputStream outputStream = new FileOutputStream("D:/students_new_s.xls");
            workbook.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取Excel的头
     */
    private static void readHead(String filePath) {
        HSSFSheet hssfSheet = initWorkBook(filePath);
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
    private static void readExcel(String filePath) {
        HSSFSheet hssfSheet = initWorkBook(filePath);
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
                        int numericCellValue = (int) cell.getNumericCellValue();
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

    private static HSSFSheet initWorkBook(String filePath) {
        HSSFWorkbook workbook = null;
        try {
            // 读取Excel文件
            InputStream inputStream = new FileInputStream(filePath);
            workbook = new HSSFWorkbook(inputStream);
            inputStream.close();
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
    private static void setWeight(String clazzName, String aim, double weigths) {
        for (int i = 0; i < readhead.getGrade().size(); i++) {
            if (readhead.getGrade().get(i).getClassName().equals(clazzName)) {
                readhead.getGrade().get(i).getWeigth().put(aim, weigths);
            }
        }
    }

    /**
     * 根据权重生成数据
     */
    private static void dealData(List<Student> students) {
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
    private static void calculate() {
        int index = 0;
        for (Student student : list) {
            for (Grade gd: student.getGrade()) {
                if("折合".equals(gd.getClassName())) {
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
                    student.getGrade().get(index).getNewGrade().put(keyword,sum);
                }
            }
        }
        //每个学生
        for (Student student : list) {
            for (Grade gd: student.getGrade()) {
                for (String keyword : gd.getNewGrade().keySet()) {
                    if("折合".equals(gd.getClassName())){
                        break;
                    }
                    if("成绩".equals(gd.getClassName())){
                        break;
                    }
                    if("达成值".equals(gd.getClassName())){
                        break;
                    }
                    double gradeFolder = gd.getNewGrade().get(keyword);
                    gradeFolder = getGradeFolder(gd, keyword, gradeFolder);
                    gd.getNewGrade().put(keyword,gradeFolder);
                }
            }
        }
    }

    private static double getGradeFolder(Grade gd, String keyword, double gradeFolder) {
        if(gd.getWeigth().get(keyword) != null){
            if(gradeFolder > 40){
                gradeFolder = Double.parseDouble(String.format("%.2f", gradeFolder + Math.random() * gd.getWeigth().get(keyword) * 2.5));
            }
            else{
                gradeFolder =Double.parseDouble(String.format("%.2f", gradeFolder - Math.random() * gd.getWeigth().get(keyword) * 2.5));
            }
        }
        else{
            if(gradeFolder > 30){
                gradeFolder = Double.parseDouble(String.format("%.2f", gradeFolder + Math.random() * 0.5 * 2.5));
            }
            else{
                gradeFolder =Double.parseDouble(String.format("%.2f", gradeFolder - Math.random() *0.5 * 2.5));
            }
        }
        return gradeFolder;
    }
}

class Student{
    private String no = "";
    private String stuId = "";
    private String stuName = "";
    private ArrayList<Grade> grade = new ArrayList<>();

    public Student(){

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

    public void setGrade(ArrayList<Grade> grade) {
        this.grade = grade;
    }

    public void addOldGrade(Grade grade){
        this.grade.add(grade);
    }

    public void createData(){
        for (Grade gd:this.getGrade()) {
            if("达成值".equals(gd.getClassName())){ culReach(); break; }
            gd.createData();
        }
    }


    /**生成达成值和理论总评的成绩*/
    public void culReach(){
        String key;
        List<String > className = new ArrayList<>();
        int index = 0;
        for (Grade gd:grade) {
            if("折合".equals(gd.getClassName())) {
                index = grade.indexOf(gd);
                break;
            }
        }
        //index为折合在表中的下标，+1为成绩，+2为达成值
        Map<String,Double> tempWeight = grade.get(index).getWeigth();
        Map<String,Double> tempGrade = grade.get(index).getNewGrade();
        Map<String,Double> GradeWeight = grade.get(index+1).getWeigth();
        Map<String,Double> GradeGrade = grade.get(index+1).getNewGrade();
        //遍历达成值的目标
        for (Map.Entry<String, Double> entry : grade.get(index + 2).getWeigth().entrySet()){
            key = entry.getKey();
            className.add(key);
        }
//        double sum = 0;
//        for (String name:className) {
//            sum += tempGrade.get(name) / tempWeight.get(name) + GradeGrade.get(name) / GradeWeight.get(name);
//            sum /= 200;
//            grade.get(index + 2).getNewGrade().put(name, Double.valueOf(String.format("%.2f",sum)));
//        }
        double sum;
        for (String name:className) {
            sum =  0;
            int count = index;
            for (int i = 0; i < index; i++) {
                if (grade.get(i).getNewGrade().get(name) != null){
                    sum += grade.get(i).getNewGrade().get(name) / grade.get(i).getWeigth().get(name);
                }
                else {
                    count--;
                }
            }
            if(count == index ) {
                sum = sum / index / 100;
            }
            else{
                sum = sum / count / 100;
            }
            grade.get(index + 2).getNewGrade().put(name, Double.valueOf(String.format("%.2f",sum)));
        }

        //一下为理论总评算法
        grade.get(index + 3).getNewGrade().put("Grade" ,grade.get(index).getOldGrade() * 0.3 +grade.get(index + 1).getOldGrade() * 0.7);
    }


    //这个方法得到一个学生的所有数据结构
    public ArrayList<DealDataStructure> createStructure(){
        ArrayList<DealDataStructure> DealDataStructures = new ArrayList<>();
        for (Grade gd: grade) {
            if("折合".equals(gd.getClassName())){ break; }
            else {
                for (String key :gd.getWeigth().keySet()) {
                    DealDataStructures.add(new DealDataStructure(gd.getClassName(),key,gd.getWeigth().get(key),gd.getNewGrade().get(key)));
                }
            }
        }
        return DealDataStructures;
    }



}

class Grade{
    private String className = "";
    private double oldGrade = 0;
    private Map<String,Double> weigth = new HashMap<>();
    private Map<String,Double> newGrade = new HashMap<>();

    public Grade(){

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

    public void setWeigth(Map<String, Double> weigth) {
        this.weigth = weigth;
    }

    public Map<String, Double> getNewGrade() {
        return newGrade;
    }

    public void setNewGrade(Map<String, Double> newGrade) {
        this.newGrade = newGrade;
    }

    //生成学生的分成绩
    public void createData(){
        for (Map.Entry<String, Double> entry : weigth.entrySet()) {
            String key = entry.getKey().toString();
            double value = entry.getValue();
            newGrade.put(key,value * oldGrade);
        }
    }
}

class DealDataStructure {
    public String className;
    public String aim;
    public double weight;
    public double grade;


    public DealDataStructure(String className, String aim, double weight, double grade) {
        this.className = className;
        this.aim = aim;
        this.weight = weight;
        this.grade = grade;

    }



}





















