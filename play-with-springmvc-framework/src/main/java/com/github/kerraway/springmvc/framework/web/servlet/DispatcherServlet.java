package com.github.kerraway.springmvc.framework.web.servlet;

import com.github.kerraway.springmvc.framework.web.handler.MappingHandler;
import com.github.kerraway.springmvc.framework.web.handler.MappingHandlerManager;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author kerraway
 * @date 2019/09/15
 */
@Slf4j
public class DispatcherServlet implements Servlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        logger.info("DispatcherServlet#service method.");
        for (MappingHandler mappingHandler : MappingHandlerManager.getManager().getMappingHandlers()) {
            try {
                if (mappingHandler.handle(req, res)) {
                    return;
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        logger.warn("404, not found.");
        ((HttpServletResponse) res).setStatus(404);
        res.getWriter().write("404, not found.");
        res.getWriter().flush();
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
    }
}
