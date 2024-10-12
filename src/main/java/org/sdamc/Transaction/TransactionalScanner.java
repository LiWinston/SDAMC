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

    private static final Map<Class<?>, Boolean> transactionalClassCache = new HashMap<>();

    public static void scanAndCacheTransactionalClasses(String packageName) throws Exception {
        // 扫描包中的类并为带有 @Transactional 注解的方法生成代理对象
        for (Class<?> clazz : ClassScanner.getClasses(packageName)) {
            logger.info("Scanning class: {}", clazz.getName());
            boolean hasTransactionalMethod = false;

            // 检查类中的每个方法是否有 @Transactional 注解
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Transactional.class)) {
                    hasTransactionalMethod = true;
                    logger.info("Found @Transactional method: {} in class: {}", method.getName(), clazz.getName());
                    break;
                }
            }

            // 缓存是否有 @Transactional 方法的类
            transactionalClassCache.put(clazz, hasTransactionalMethod);
            logger.info("Cached transactional status for class: {} as: {}", clazz.getName(), hasTransactionalMethod);
        }
    }

    // 只有在需要的时候才实例化并生成代理对象
    public static Object getProxy(Class<?> clazz) {
        Boolean hasTransactionalMethod = transactionalClassCache.get(clazz);
        if (hasTransactionalMethod == null) {
            logger.warn("Class: {} was not scanned. Attempting to create a direct instance without proxy.",
                    clazz.getName());
            try {
                return clazz.getDeclaredConstructor().newInstance();
            }
            catch (Exception e) {
                logger.error("Failed to create new instance for class: {}", clazz.getName(), e);
                return null;
            }
        }

        if (hasTransactionalMethod) {
            logger.info("Creating CGLIB proxy for class: {}", clazz.getName());
            try {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(clazz);
                enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> TransactionAspect
                    .executeTransactional(method, args, clazz.getDeclaredConstructor().newInstance()));
                return enhancer.create();
            }
            catch (Exception e) {
                logger.error("Failed to create CGLIB proxy for class: {}", clazz.getName(), e);
                return null;
            }
        }
        else {
            logger.info("Class: {} has no @Transactional methods. Creating a direct instance.", clazz.getName());
            try {
                return clazz.getDeclaredConstructor().newInstance();
            }
            catch (Exception e) {
                logger.error("Failed to create new instance for class: {}", clazz.getName(), e);
                return null;
            }
        }
    }

}
