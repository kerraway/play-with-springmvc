package com.github.kerraway.springmvc.framework.web.handler;

import com.github.kerraway.springmvc.framework.web.mvc.Controller;
import com.github.kerraway.springmvc.framework.web.mvc.RequestMapping;
import com.github.kerraway.springmvc.framework.web.mvc.RequestParam;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping 处理器工厂
 *
 * @author kerraway
 * @date 2019/09/15
 */
@Slf4j
public class MappingHandlerManager {

    private static volatile MappingHandlerManager manager;

    private final Map<String, MappingHandler> mappingHandlerMap;

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
        this.mappingHandlerMap = new HashMap<>(256);
    }

    /**
     * 根据 uri 获取 Mapping 处理器
     *
     * @param uri
     * @return Mapping 处理器
     */
    public MappingHandler getMappingHandler(String uri) {
        return mappingHandlerMap.get(uri);
    }

    /**
     * 解析并添加 Mapping 处理器
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
            String uri = method.getDeclaredAnnotation(RequestMapping.class).value();
            List<String> paramNames = new ArrayList<>(32);
            List<Class<?>> paramTypes = new ArrayList<>(32);
            for (Parameter param : method.getParameters()) {
                if (param.isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = param.getDeclaredAnnotation(RequestParam.class);
                    paramNames.add(requestParam.value());
                    paramTypes.add(param.getType());
                }
            }
            logger.info("Load request mapping '{}' from {}#{}.", uri, clazz.getName(), method.getName());
            MappingHandler mappingHandler = MappingHandler.builder()
                    .uri(uri)
                    .method(method)
                    .clazz(clazz)
                    .paramNames(paramNames)
                    .paramTypes(paramTypes)
                    .build();
            this.mappingHandlerMap.put(uri, mappingHandler);
        }
    }
}
