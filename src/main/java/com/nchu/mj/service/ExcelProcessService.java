package com.nchu.mj.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author cjh
 * @description Excel 文件处理服务
 * @since 2022/05/16
 */
public interface ExcelProcessService {

    /**
     * 处理学生信息 Excel 文件
     *
     * @param sourceExcelStream 文件输入流
     * @throws IOException 文件处理异常
     */
    void excelProcessForStudent(InputStream sourceExcelStream) throws IOException;
}
