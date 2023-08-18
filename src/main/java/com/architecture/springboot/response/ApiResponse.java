package com.architecture.springboot.response;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
public class ApiResponse<T> {
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static final String ERROR = "error";

    private String success;
    private T data;
    private List<ResponseError> errors;

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(SUCCESS, null);
    }

    public static <T> ApiResponse<T> success(final T data) {
        if (data instanceof ResMessage) {
            return (ApiResponse<T>) new ApiResponse<>(SUCCESS, ((ResMessage) data).getHashMap());
        } else {
            return new ApiResponse<>(SUCCESS, data);
        }
    }

    public static <T> ApiResponse<T> fail(ResponseError error) {
        return new ApiResponse<>(FAIL, List.of(error));
    }

    public static <T> ApiResponse<T> fail(List<ResponseError> errors) {
        return new ApiResponse<>(FAIL, errors);
    }

    public static <T> ApiResponse<T> error(ResponseError error) {
        return new ApiResponse<>(ERROR, List.of(error));
    }

    public static <T> ApiResponse<T> error(List<ResponseError> errors) {
        return new ApiResponse<>(ERROR, errors);
    }

    private ApiResponse(String success, T data, List<ResponseError> errors) {
        this.success = success;
        this.data = data;
        this.errors = errors;
    }

    private ApiResponse(String success, T data) {
        this.success = success;
        this.data = data;
        this.errors = new ArrayList<>();
    }

    private ApiResponse(String success, List<ResponseError> errors) {
        this.success = success;
        this.data = null;
        this.errors = errors;
    }
}
