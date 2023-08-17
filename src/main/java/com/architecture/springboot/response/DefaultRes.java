package com.architecture.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DefaultRes<T> {
    private int status;
    private T data;

    public DefaultRes(final int status) {
        this.status = status;
        this.data = null;
    }

    public static <T> DefaultRes<T> res(final int status) {
        return res(status, null);
    }

    public static <T> DefaultRes<T> res(final int status, final T object) {
        return DefaultRes.<T>builder()
                .data(object)
                .status(status)
                .build();
    }
}
