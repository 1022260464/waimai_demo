package com.sky.aspect;


import com.sky.annoation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Aspect
@Slf4j
@Component
public class AutoFillAspect {
    // 切入点
    // 匹配com.sky.mapper包下的所有方法,同时匹配为上了@AutoFill注解的方法
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annoation.AutoFill)")
    public void pointcut() {

    }


    //前置通知：在目标方法执行之前执行,运行时，会自动执行该方法,需要传入一个参数，该参数为JoinPoint，该参数封装了方法调用的参数
    //JoinPoint：封装了目标方法的参数，目标方法的签名，目标方法所属的对象等
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) throws Exception {
        log.info("开始进行公共字段的自动填充");
        //获取当前被拦截到的方法上的数据操作类型
      MethodSignature Signature = (MethodSignature) joinPoint.getSignature();
      AutoFill autoFill = Signature.getMethod().getAnnotation(AutoFill.class);
      OperationType operationType = autoFill.value();
        //获取当前被拦截的方法参数
        Object[] args = joinPoint.getArgs();
        if (args.length == 0||args[0]==null){
            return;
        }
        Object object = args[0];
                //获取当前被拦截的方法的参数类型
        //准备赋值的数据
        LocalDateTime NOW = LocalDateTime.now();
        Long CURRENT_ID = BaseContext.getCurrentId();
        //进行赋值
        if(operationType == OperationType.INSERT){
            try {
                //为创建时间赋值
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class)
                        .invoke(object, NOW);
                //为更新时间赋值
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class)
                        .invoke(object, NOW);
                //为创建人赋值
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class)
                        .invoke(object, CURRENT_ID);
                //为更新人赋值
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class)
                        .invoke(object, CURRENT_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(operationType == OperationType.UPDATE){
            try {
                //为更新时间赋值
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class)
                        .invoke(object, NOW);
                //为更新人赋值
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class)
                        .invoke(object, CURRENT_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

}
