package com.dscjss.codingplatform.error;

public class InvalidRequestException extends RuntimeException{


    public InvalidRequestException() {
    }

    public InvalidRequestException(String msg) {
        super(msg);
    }

}
