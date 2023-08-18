package com.architecture.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@Deprecated
public class DefaultRes<T> {
    private T data;
    private boolean success;
    private List<ResponseError> errors;

    public DefaultRes(final boolean success) {
        this.success = success;
        this.data = null;
    }

    public static <T> DefaultRes<T> res(final boolean success) {
        return res(success, null,null);
    }

    public static <T> DefaultRes<T> res(final boolean success, final T object) {
        return DefaultRes.<T>builder()
                .data(object)
                .success(success)
                .build();
    }

    public static <T> DefaultRes<T> res(final boolean success, final T object, final List<ResponseError> errors) {
        return DefaultRes.<T>builder()
                .data(object)
                .success(success)
                .errors(errors)
                .build();
    }

    public static <T> DefaultRes<T> res(final boolean success, final List<ResponseError> errors) {
        return DefaultRes.<T>builder()
                .success(success)
                .errors(errors)
                .build();
    }
}
