package com.test;

import com.jhmarryme.Application;
import com.jhmarryme.service.TestTransService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * description: 
 * @author JiaHao Wang
 * @date 2021/2/16 16:32
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
public class TransTest {

    @Autowired
    private TestTransService testTransService;

//    @Test
    public void whenSuccess() {
        testTransService.testPropagationTrans();
    }
}
