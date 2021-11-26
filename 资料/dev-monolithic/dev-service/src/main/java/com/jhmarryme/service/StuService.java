package com.jhmarryme.service;

import com.jhmarryme.pojo.Stu;

/**
 * description: 
 * @author JiaHao Wang
 * @date 2021/2/16 10:43
 */
public interface StuService {

    Stu getStu(int id);

    void saveStu();

    void deleteStu(int id);

    void updateStu(int id);

    public void saveParent();

    public void saveChildren();
}
