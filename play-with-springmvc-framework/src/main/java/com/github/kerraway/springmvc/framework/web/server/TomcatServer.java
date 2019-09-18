package com.github.kerraway.springmvc.framework.web.server;

import com.github.kerraway.springmvc.framework.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

/**
 * @author kerraway
 * @date 2019/09/14
 */
public class TomcatServer {

    private final Tomcat tomcat;
    private final String[] args;

    public TomcatServer(int port, String[] args) {
        this.tomcat = new Tomcat();
        this.tomcat.setPort(port);
        this.args = args;
    }

    public TomcatServer(String[] args) {
        this(8080, args);
    }

    /**
     * 启动 Tomcat 服务
     *
     * @throws LifecycleException
     */
    public void start() throws LifecycleException {
        tomcat.start();

        Context context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());

        //just test, disabled
        //fooServlet
        /*FooServlet fooServlet = new FooServlet();
        Tomcat.addServlet(context, "fooServlet", fooServlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/foo", "fooServlet");
        tomcat.getHost().addChild(context);*/

        //dispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/", "dispatcherServlet");
        tomcat.getHost().addChild(context);

        Thread thread = new Thread("tomcat-await-thread") {
            @Override
            public void run() {
                tomcat.getServer().await();
            }
        };
        thread.setDaemon(false);
        thread.start();
    }
}
