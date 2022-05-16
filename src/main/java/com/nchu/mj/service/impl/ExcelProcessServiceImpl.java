package com.nchu.mj.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nchu.mj.service.ExcelProcessService;
import org.springframework.stereotype.Service;

/**
 * @author cjh
 * @since 2022/05/16
 * @description Excel 文件处理服务实现
 */
@Service
public class ExcelProcessServiceImpl implements ExcelProcessService {

    @Override
    public void excelProcessForStudent(InputStream sourceExcelStream) throws IOException {
        OutputStream out = new StudentExcelProcess().process(sourceExcelStream);

    }
}
