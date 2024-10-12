package org.sdamc.Transaction;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("AppInitializer: Context initialized");
        try {
            TransactionalScanner.scanAndCacheTransactionalClasses("org.sdamc");
            logger.info("AppInitializer: Proxy creation completed");
        }
        catch (Exception e) {
            logger.error("AppInitializer: Error during proxy creation", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("AppInitializer: Context destroyed");
    }

}
