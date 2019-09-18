package com.github.kerraway.springmvc.framework.web.servlet;

import com.github.kerraway.springmvc.framework.web.handler.MappingHandler;
import com.github.kerraway.springmvc.framework.web.handler.MappingHandlerManager;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author kerraway
 * @date 2019/09/15
 */
@Slf4j
public class DispatcherServlet implements Servlet {

    @Override
    public void init(ServletConfig config) {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {
        logger.info("DispatcherServlet#service method.");
        MappingHandler mappingHandler = MappingHandlerManager.getManager()
                .getMappingHandler(((HttpServletRequest) req).getRequestURI());
        if (mappingHandler != null) {
            try {
                mappingHandler.handle(req, res);
            } catch (Exception e) {
                // TODO: 2019/9/18 specific exception
                throw new RuntimeException(String.format("Handle '%s' error.", mappingHandler.getUri()), e);
            }
        } else {
            logger.warn("404, not found.");
            ((HttpServletResponse) res).setStatus(404);
            res.getWriter().write("404, not found.");
            res.getWriter().flush();
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
    }
}
