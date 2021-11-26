package com.jhmarryme.web.controller;

import com.jhmarryme.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * description: 增删改查测试controller
 * @author JiaHao Wang
 * @date 2021/1/26 22:58
 */
@ApiIgnore
@RestController
@RequestMapping("/stu")
public class StuFooController {

    @Autowired
    private StuService stuService;

    @GetMapping("/get/{id}")
    public Object getStu(@PathVariable int id) {
        return stuService.getStu(id);
    }

    @GetMapping("/save")
    public Object saveStu() {
        stuService.saveStu();
        return "success";
    }

    @GetMapping("/delete/{id}")
    public Object deleteStu(@PathVariable int id) {
        stuService.deleteStu(id);
        return "success";
    }

    @GetMapping("/update/{id}")
    public Object updateStu(@PathVariable int id) {
        stuService.updateStu(id);
        return "success";
    }
}
