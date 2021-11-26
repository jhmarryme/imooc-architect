package com.jhmarryme.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 * description: 自定义响应数据结构
 *      本类可提供给 H5/ios/安卓/公众号/小程序 使用
 * 		前端接受此类数据（json object)后，可自行根据业务去实现相关功能
 *
 * 		200：表示成功
 * 		500：表示错误，错误信息在msg字段中
 * 		501：bean验证错误，不管多少个错误都以map形式返回
 * 		502：拦截器拦截到用户token出错
 * 		555：异常抛出信息
 * 		556: 用户qq校验异常
 * @author Jiahao Wang
 * @date 2021/2/18 22:11
 */
@Data
public class CommonResult {

    /**
     * 定义jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 响应业务状态
     */
    private Integer status;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应中的数据
     */
    private Object data;

    /**
     * 不使用
     */
    @JsonIgnore
    private String ok;

    public static CommonResult build(Integer status, String msg, Object data) {
        return new CommonResult(status, msg, data);
    }

    public static CommonResult build(Integer status, String msg, Object data, String ok) {
        return new CommonResult(status, msg, data, ok);
    }

    public static CommonResult ok(Object data) {
        return new CommonResult(data);
    }

    public static CommonResult ok() {
        return new CommonResult(null);
    }

    public static CommonResult errorMsg(String msg) {
        return new CommonResult(500, msg, null);
    }

    public static CommonResult errorMap(Object data) {
        return new CommonResult(501, "error", data);
    }

    public static CommonResult errorTokenMsg(String msg) {
        return new CommonResult(502, msg, null);
    }

    public static CommonResult errorException(String msg) {
        return new CommonResult(555, msg, null);
    }

    public static CommonResult errorUserQQ(String msg) {
        return new CommonResult(556, msg, null);
    }

    public CommonResult() {

    }

    public CommonResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public CommonResult(Integer status, String msg, Object data, String ok) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.ok = ok;
    }

    public CommonResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }


}
