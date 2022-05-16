package com.nchu.mj.controller;


import java.io.ByteArrayInputStream;
import java.io.IOException;


import com.nchu.mj.service.StudentExcelProcess;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author cjh
 * @since 2022/05/16
 * @description Excel 文件处理控制器
 */
@Controller
public class ExcelProcessController {

    @PostMapping("/excel/process")
    public ResponseEntity<Resource> hello(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = StudentExcelProcess.build().process(file.getBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", "student_out.xls"));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(new ByteArrayInputStream(bytes)));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
