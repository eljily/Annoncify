package com.sibrahim.annoncify.exceptions;

import com.sibrahim.annoncify.dto.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ResponseMessage> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(GenericException.class)
    @ResponseBody
    public ResponseEntity<ResponseMessage> handleGenericException(GenericException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseMessage> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseBody
    public ResponseEntity<ResponseMessage> handleUserAlreadyExist(UserAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(SubCategoryException.class)
    @ResponseBody
    public ResponseEntity<ResponseMessage> handleSubCategoryException(SubCategoryException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(InvalidKeywordException.class)
    public ResponseEntity<ResponseMessage> handleInvalidKeywordException(InvalidKeywordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseMessage.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .build());
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseMessage> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build());
    }
}
