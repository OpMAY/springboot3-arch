package com.architecture.springboot.typehandler;

import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class StringArrayListTypeHandler<T> extends BaseTypeHandler<T> {

    private final Class<T> type;

    public StringArrayListTypeHandler(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, t.toString());
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return convertToArrayList(resultSet.getString(s));
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return convertToArrayList(resultSet.getString(i));
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return convertToArrayList(callableStatement.getString(i));
    }

    private T convertToArrayList(String arrayString) {
        if (arrayString != null) {
            List<String> result = Arrays.asList(arrayString.substring(1, arrayString.length() - 1).split(",\\s*"));
            ArrayList<String> strings = new ArrayList<>(result);
            return (T) strings;
        } else {
            return (T) new ArrayList<String>();
        }
    }
}
