package com.jhmarryme.service;

import com.jhmarryme.pojo.Stu;
import com.jhmarryme.pojo.Users;
import com.jhmarryme.pojo.bo.UserBO;

/**
 * description: 
 * @author JiaHao Wang
 * @date 2021/2/16 10:43
 */
public interface UsersService {

    /**
     * 查询用户名是否存在
     * <br/>
     * @param username 用户名
     * @return boolean
     */
    public boolean queryUsernameIsExist(String username);


    /**
     * 用户注册
     * <br/>
     * @param userBO 用户
     * @return com.jhmarryme.pojo.Users
     */
    Users createUser(UserBO userBO);

    /**
     * 用户登录
     * <br/>
     * @param username 用户名
     * @param password 加密后的密码
     * @return com.jhmarryme.pojo.Users
     */
    Users queryUserForLogin(String username, String password);
}
