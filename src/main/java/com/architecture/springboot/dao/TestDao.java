package com.architecture.springboot.dao;

import com.architecture.springboot.mapper.TestMapper;
import com.architecture.springboot.model.dto.TestDto;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class TestDao {
    private final TestMapper mapper;
    public TestDao(SqlSession sqlSession) {
        this.mapper = sqlSession.getMapper(TestMapper.class);
    }
    public void insertTest(TestDto testDto) {
        mapper.insertTest(testDto);
    }

    public List<TestDto> getTests() {
        return mapper.getTests();
    }

}
