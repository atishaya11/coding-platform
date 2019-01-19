package com.dscjss.codingplatform.util;

public class Constants {

    public static final String JUDGE_API_ENDPOINT = "http://localhost:8084/api/";

    public enum Status{
        QUEUED,
        EXECUTED,
        CORRECT,
        TIME_LIMIT_EXCEEDED,
        WRONG,
        FAILED,
        RUNNING;
    }
}
