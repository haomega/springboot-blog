package com.example.blog.entity;

public class Result {
    String status;
    Object msg;
    Object data;

    public static Result failure(Object msg) {
        return new Result("fail", msg);
    }
    public static Result success(Object msg) {
        return new Result("ok", msg);
    }
    public static Result success(Object msg, Object data) {
        return new Result("ok", msg, data);
    }

    public Result(String status, Object msg) {
        this(status, msg, null);
    }

    public Result(String status, Object msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public Object getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}