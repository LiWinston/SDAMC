package org.sdamc.Transaction;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TransactionalScanner {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalScanner.class);
    private static final Map<Class<?>, Object> proxyCache = new HashMap<>();

    public static void scanAndCreateProxies(String packageName) throws Exception {
        // 扫描包中的类并为带有 @Transactional 注解的方法生成代理对象
        for (Class<?> clazz : ClassScanner.getClasses(packageName)) {
            logger.info("Scanning class: {}", clazz.getName());
            Object target = clazz.getDeclaredConstructor().newInstance();
            boolean hasTransactionalMethod = false;

            // 检查类中的每个方法是否有 @Transactional 注解
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Transactional.class)) {
                    hasTransactionalMethod = true;
                    break;
                }
            }

            // 如果类中的方法带有 @Transactional 注解，生成代理
            if (hasTransactionalMethod) {
                logger.info("Creating CGLIB proxy for class: {}", clazz.getName());
                // 使用 CGLIB 创建代理对象
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(clazz);
                enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                    return TransactionAspect.executeTransactional(method, args, target);
                });
                Object proxy = enhancer.create();
                proxyCache.put(clazz, proxy);
                logger.info("Proxy created and cached for class: {}", clazz.getName());
            }
            else {
                logger.info("No @Transactional methods found in class: {}. Caching original instance.", clazz.getName());
                proxyCache.put(clazz, target);
            }
        }
    }

    // 获取生成的代理对象
    public static Object getProxy(Class<?> clazz) {
        Object proxy = proxyCache.get(clazz);
        if (proxy == null) {
            logger.warn("No proxy or instance found for class: {}. Attempting to create a new instance.", clazz.getName());
            try {
                proxy = clazz.getDeclaredConstructor().newInstance();
                proxyCache.put(clazz, proxy);
                logger.info("Created and cached new instance for class: {}", clazz.getName());
            } catch (Exception e) {
                logger.error("Failed to create new instance for class: {}", clazz.getName(), e);
            }
        } else {
            logger.info("Retrieved proxy or instance for class: {}", clazz.getName());
        }
        return proxy;
    }

}
