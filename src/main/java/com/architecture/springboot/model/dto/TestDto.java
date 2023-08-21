package com.architecture.springboot.model.dto;

public record TestDto(int no, String name) {
    public TestDto(String name) {
        this(0, name);
    }
}
