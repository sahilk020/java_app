package com.crmws.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    
    public ResponseEnvelope(HttpStatus httpStatus, String error) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.httpStatus = httpStatus;
        this.error = error;
    }

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getTotalTimeTakenInMillis() {
		return totalTimeTakenInMillis;
	}

	public void setTotalTimeTakenInMillis(Long totalTimeTakenInMillis) {
		this.totalTimeTakenInMillis = totalTimeTakenInMillis;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(Integer totalcount) {
		this.totalcount = totalcount;
	}

	public T getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(T payLoad) {
		this.payLoad = payLoad;
	}
    
    
    


}
