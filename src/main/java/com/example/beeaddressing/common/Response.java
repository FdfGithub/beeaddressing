package com.example.beeaddressing.common;

public class Response {
    // 状态 true：代表成功, false:代表失败
    private boolean status;
    // 前端如果需要对错误单独处理，就需要指定状态码（因为发生错误的原因有多种）
    private int code;
    // 响应的数据对象
    private Object data;
    // 消息提示
    private String msg;

    private Response(){
    }

    public static Response createSuccess(){
        return new Response().setStatus(true).setCode(2000).setMsg("成功");
    }

    public static Response createError(String msg){
        return new Response().setStatus(false).setCode(2001).setMsg(msg);
    }


    public int getCode() {
        return code;
    }

    public Response setCode(int code) {
        this.code = code;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Response setData(Object data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Response setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public boolean isStatus() {
        return status;
    }

    public Response setStatus(boolean status) {
        this.status = status;
        return this;
    }
}
