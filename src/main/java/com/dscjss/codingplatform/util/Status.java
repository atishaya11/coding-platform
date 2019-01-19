package com.dscjss.codingplatform.util;


public enum Status {

    TIME_LIMIT_EXCEEDED(0),
    COMPILATION_ERROR(1),
    RUNTIME_ERROR(2),
    INTERNAL_ERROR(3),
    EXECUTED(4),
    CORRECT(5),
    WRONG(6),
    QUEUED(7);
    private final int code;

    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}