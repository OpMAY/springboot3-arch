package com.architecture.springboot.typehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Log4j2
public class JsonObjectTypeHandler<T> extends BaseTypeHandler<T> {

    private final Class<T> type;

    public JsonObjectTypeHandler(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, new Gson().toJson(t));
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
        //log.info("getNullableResult +s: " + s + resultSet.getString(s));
        return convertToObject(resultSet.getString(s));
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
        //log.info("getNullableResult +i: " + i + resultSet.getString(i));
        return convertToObject(resultSet.getString(i));
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        //log.info("getNullableResult +CallableStatement: " + i + callableStatement.getString(i));
        return convertToObject(callableStatement.getString(i));
    }

    private T convertToObject(String jsonString) {
        if (jsonString != null) {
            try {
                Class<?> findClass = type;
                return (T) new ObjectMapper().readValue(jsonString, findClass);
            } catch (Exception e) {
                log.error("JSONTypeHandler failed to casting jsonString to Object, JSON String : " + jsonString, e);
                return null;
            }
        } else {
            return null;
        }
    }
}
