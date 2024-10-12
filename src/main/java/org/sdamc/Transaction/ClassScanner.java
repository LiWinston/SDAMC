package org.sdamc.Transaction;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        String path = packageName.replace('.', '/');
        URL root = Thread.currentThread().getContextClassLoader().getResource(path);
        File[] files = new File(root.getFile()).listFiles((dir, name) -> name.endsWith(".class"));
        List<Class<?>> classes = new ArrayList<>();
        for (File file : files) {
            String className = packageName + '.' + file.getName().replaceAll(".class$", "");
            classes.add(Class.forName(className));
        }
        return classes;
    }

}
