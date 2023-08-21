package com.architecture.springboot.mapper;

import com.architecture.springboot.model.dto.TestDto;

import java.util.List;

public interface TestMapper {
    void insertTest(TestDto testDto);

    List<TestDto> getTests();
}
