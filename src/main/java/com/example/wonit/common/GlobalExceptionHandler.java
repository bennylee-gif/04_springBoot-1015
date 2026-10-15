package com.example.wonit.common;

import com.example.wonit.dto.ErrorResponse;
import com.example.wonit.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 손님 잘못 → WARN 한 줄. 스택트레이스까지 찍으면 로그가 쓰레기로 가득 찬다
    @ExceptionHandler(AccountFrozenException.class)
    public ResponseEntity<ErrorResponse> handleAccountFrozen(AccountFrozenException e) {
        log.warn("지급정지 계좌 출금 시도: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("ACCOUNT_FROZEN", e.getMessage()));
    }

    @ExceptionHandler(InvalidAccountRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountRequest(
            InvalidAccountRequestException e
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_ACCOUNT_REQUEST", e.getMessage()));
    }

    // 맨 마지막 그물. 🚨 e.getMessage() 를 그대로 내보내면 SQL·클래스명이 손님 화면에 찍힌다s
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        log.error("처리하지 못한 예외", e);   // ← e 를 {} 없이 마지막에 넘기면 스택트레이스 전체가 찍힌다
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "잠시 후 다시 시도해 주세요."));
    }
}