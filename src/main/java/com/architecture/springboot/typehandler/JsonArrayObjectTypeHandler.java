package com.architecture.springboot.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.json.JSONArray;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Log4j2
public class JsonArrayObjectTypeHandler<T> extends BaseTypeHandler<T> {

    private Class<T> type;

    public JsonArrayObjectTypeHandler(Class<T> type) {
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
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            Class<?> findClass = type;
            if (jsonArray.length() != 0) {
                ArrayList arrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    arrayList.add((T) new ObjectMapper().readValue(jsonArray.getJSONObject(i).toString(), findClass));
                }
                return (T) arrayList;
            } else {
                return (T) new ArrayList<T>();
            }
        } catch (JsonProcessingException e) {
            log.error("JsonArrayObjectTypeHandler failed to casting jsonString to List<Object>, JSON String : " + jsonString, e);
            return (T) new ArrayList<T>();
        }
    }
}
