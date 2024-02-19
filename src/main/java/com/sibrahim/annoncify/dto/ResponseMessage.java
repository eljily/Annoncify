package com.sibrahim.annoncify.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseMessage {

    private Integer status;
    private String message;
    private Object data;
    private PaginationData meta;

    public ResponseMessage(Integer status, String message, Object data, PaginationData meta) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.meta = meta;
    }

    public ResponseMessage(Integer status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseMessage(Integer status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public ResponseMessage(Object data, Integer status) {
        this.status = status;
        this.message = null;
        this.data = data;
    }
}
