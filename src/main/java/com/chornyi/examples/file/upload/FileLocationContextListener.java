package com.chornyi.examples.file.upload;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
@Slf4j
public class FileLocationContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String rootPath = System.getProperty("catalina.home");
        ServletContext ctx = servletContextEvent.getServletContext();
        String relativePath = ctx.getInitParameter("tempfile.dir");
        File file = new File(rootPath + File.separator + relativePath);
        if(!file.exists()) file.mkdirs();
        log.debug("File Directory created to be used for storing files");
        ctx.setAttribute("FILES_DIR_FILE", file);
        ctx.setAttribute("FILES_DIR", rootPath + File.separator + relativePath);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
    
}
