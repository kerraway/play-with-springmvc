package com.github.kerraway.springmvc.framework.web.handler;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Mapping 处理器
 * 根据 mapping 关系，将具体的请求转发给具体的 controller 方法处理
 *
 * @author kerraway
 * @date 2019/09/15
 */
@Slf4j
@Getter
@Builder
public class MappingHandler {

    private final String uri;
    private final Object target;
    private final Method method;
    private final List<String> paramNames;
    private final List<Class<?>> paramTypes;

    /**
     * 根据 mapping 关系，将具体的请求转发给具体的 controller 方法处理
     *
     * @param req 请求
     * @param res 响应
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws IOException
     */
    public void handle(ServletRequest req, ServletResponse res)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        if (!uri.equals(((HttpServletRequest) req).getRequestURI())) {
            return;
        }
        logger.info("Handle {}", uri);
        Object[] params = new Object[paramNames.size()];
        for (int i = 0; i < params.length; i++) {
            params[i] = parseParam(req.getParameter(paramNames.get(i)), paramTypes.get(i));
        }
        Object result = method.invoke(target, params);
        res.getWriter().write(String.valueOf(result));
        res.getWriter().flush();
    }

    /**
     * 解析参数
     *
     * @param param     String 类型参数
     * @param paramType 参数类型
     * @return 对应类型参数
     */
    private Object parseParam(String param, Class<?> paramType) {
        if (param == null || String.class.equals(paramType)) {
            return param;
        }
        if (Boolean.class.equals(paramType) || boolean.class.equals(paramType)) {
            return Boolean.valueOf(param);
        }
        if (Byte.class.equals(paramType) || byte.class.equals(paramType)) {
            return Byte.valueOf(param);
        }
        if (Short.class.equals(paramType) || short.class.equals(paramType)) {
            return Short.valueOf(param);
        }
        if (Integer.class.equals(paramType) || int.class.equals(paramType)) {
            return Integer.valueOf(param);
        }
        if (Long.class.equals(paramType) || long.class.equals(paramType)) {
            return Long.valueOf(param);
        }
        // TODO: 2019/9/15 complete
        throw new IllegalArgumentException(String.format("Unsupported param type '%s'.", paramType.getName()));
    }
}
