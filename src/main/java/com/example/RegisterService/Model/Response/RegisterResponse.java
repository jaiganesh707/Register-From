package com.example.RegisterService.Model.Response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegisterResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public RegisterResponse(LocalDateTime timestamp, int status, String message, T data) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
