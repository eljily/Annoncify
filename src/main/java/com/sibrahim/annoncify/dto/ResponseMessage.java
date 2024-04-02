package com.sibrahim.annoncify.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@Setter
public class ResponseMessage {

    private Integer status;
    private String message;
    private Object data;
    private PaginationData meta;

    public ResponseMessage() {
        // Constructeur sans paramètres
    }

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

    public ResponseMessage(String message) {
        this.status = HttpStatus.OK.value(); // Supposons que vous utilisiez HttpStatus.OK pour indiquer une opération réussie
        this.message = message;
        this.data = null; // Aucune donnée supplémentaire à inclure
    }
}

