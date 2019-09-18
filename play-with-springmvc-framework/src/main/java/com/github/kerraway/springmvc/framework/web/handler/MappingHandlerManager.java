package com.github.kerraway.springmvc.framework.web.handler;

import com.github.kerraway.springmvc.framework.web.mvc.Controller;
import com.github.kerraway.springmvc.framework.web.mvc.RequestMapping;
import com.github.kerraway.springmvc.framework.web.mvc.RequestParam;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping 处理器工厂
 *
 * @author kerraway
 * @date 2019/09/15
 */
@Slf4j
public class MappingHandlerManager {

    private static volatile MappingHandlerManager manager;

    @Getter
    private final List<MappingHandler> mappingHandlers;

    public static MappingHandlerManager getManager() {
        if (manager == null) {
            synchronized (MappingHandlerManager.class) {
                if (manager == null) {
                    manager = new MappingHandlerManager();
                }
            }
        }
        return manager;
    }

    private MappingHandlerManager() {
        this.mappingHandlers = new ArrayList<>(256);
    }

    /**
     * 解析并添加  Mapping 处理器
     *
     * @param classes 一些类
     */
    public void resolveMappingHandler(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                resolveMappingHandler(clazz);
            }
        }
    }

    /**
     * 解析并添加 Mapping 处理器
     *
     * @param clazz 类
     */
    private void resolveMappingHandler(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(RequestMapping.class)) {
                continue;
            }
            RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
            List<String> paramNames = new ArrayList<>(32);
            List<Class<?>> paramTypes = new ArrayList<>(32);
            for (Parameter param : method.getParameters()) {
                if (param.isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = param.getDeclaredAnnotation(RequestParam.class);
                    paramNames.add(requestParam.value());
                    paramTypes.add(param.getType());
                }
            }
            MappingHandler mappingHandler = MappingHandler.builder()
                    .uri(requestMapping.value())
                    .method(method)
                    .clazz(clazz)
                    .paramNames(paramNames)
                    .paramTypes(paramTypes)
                    .build();
            logger.info("Load request mapping '{}' from {}#{}.", mappingHandler.getUri(),
                    mappingHandler.getClazz().getName(), mappingHandler.getMethod().getName());
            this.mappingHandlers.add(mappingHandler);
        }
    }
}
