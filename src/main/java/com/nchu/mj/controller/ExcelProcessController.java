package com.nchu.mj.controller;

import org.springframework.stereotype.Controller;

/**
 * @author cjh
 * @since 2022/05/16
 * @description Excel 文件处理控制器
 */
@Controller
public class ExcelProcessController {

    @PostMapping("/excel/process")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
}
