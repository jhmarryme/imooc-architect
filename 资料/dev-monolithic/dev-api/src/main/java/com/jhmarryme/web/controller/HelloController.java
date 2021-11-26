package com.jhmarryme.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * description: hello world
 * @author JiaHao Wang
 * @date 2021/1/26 22:58
 */
@ApiIgnore
@RestController
@Slf4j
public class HelloController {

    @GetMapping("/hello")
    public Object hello() {
        log.info("info hello");
        log.warn("warn hello");
        log.error("error hello");
        log.debug("debug hello");
        return "hello world";
    }

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");
        session.setMaxInactiveInterval(3600);
        session.getAttribute("userInfo");
        //        session.removeAttribute("userInfo");
        return "ok";
    }
}
