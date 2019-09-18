package com.github.kerraway.springmvc.framework.starter;

import com.github.kerraway.springmvc.framework.core.ClassScanner;
import com.github.kerraway.springmvc.framework.web.handler.MappingHandlerManager;
import com.github.kerraway.springmvc.framework.web.server.TomcatServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author kerraway
 * @date 2019/09/14
 */
@Slf4j
public class StartApp {

    public static void run(Class<?> clazz, String[] args) {
        logger.info("Hello, StartApp#run method, clazz: {}, args: {}.", clazz.getName(), Arrays.toString(args));
        try {
            //加载类
            ClassScanner scanner = ClassScanner.getScanner();
            List<Class<?>> classes = scanner.scanClassesFromJar(clazz.getPackage());

            //加载 Mapping 处理器
            MappingHandlerManager mappingHandlerManager = MappingHandlerManager.getManager();
            mappingHandlerManager.resolveMappingHandler(classes);

            //启动 Tomcat 服务器
            TomcatServer tomcatServer = new TomcatServer(args);
            tomcatServer.start();
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
        }
    }

}
