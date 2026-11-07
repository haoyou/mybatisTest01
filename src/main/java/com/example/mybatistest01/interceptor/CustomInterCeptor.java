package com.example.mybatistest01.interceptor;

import com.example.mybatistest01.annotation.CreateTime;
import com.example.mybatistest01.annotation.IdKeyGenerator;
import com.example.mybatistest01.annotation.UpdateTime;
import com.example.mybatistest01.idworker.IdWorker;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

@Intercepts({@Signature(type = Executor.class,method = "update",args = {MappedStatement.class,Object.class})})
public class CustomInterCeptor implements Interceptor {
    private  IdWorker worker = new IdWorker(19,1,1);
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        //获取SQL命令
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        //获取参数
        Object parameterObject = invocation.getArgs()[1];
        //获取私有变量
        Field[] declaredFields = parameterObject.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getAnnotation(IdKeyGenerator.class) != null) {
                if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                    field.setAccessible(true);
                    field.set(parameterObject, worker.nextId());
                }
            }

            if (field.getAnnotation(CreateTime.class) != null) {
                if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                    field.setAccessible(true);
                    field.set(parameterObject, new Date());

                }
            }

            if (field.getAnnotation(UpdateTime.class) != null) {
                if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
                    field.setAccessible(true);
                    field.set(parameterObject, new Date());
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
