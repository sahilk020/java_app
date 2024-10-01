package com.pay10.webhook.vo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ResponseEnvelope<T> {

    private Timestamp timestamp;

    private Integer status;

    private Long totalTimeTakenInMillis;

    private String error;

    private String exception;

    private HttpStatus httpStatus;

    private String message;

    private Integer count;

    private Integer totalcount;

    private T payLoad;


    public ResponseEnvelope() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.httpStatus = HttpStatus.OK;
    }

    public ResponseEnvelope(HttpStatus httpStatus, T payLoad) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.httpStatus = httpStatus;
        this.payLoad = payLoad;
    }


    public ResponseEnvelope(HttpStatus httpStatus, String message, T payLoad) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.httpStatus = httpStatus;
        this.message = message;
        this.payLoad = payLoad;
    }

    public ResponseEnvelope(HttpStatus httpStatus,T payLoad, String error) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.httpStatus = httpStatus;
        this.payLoad = payLoad;
        this.error = error;
    }


}
