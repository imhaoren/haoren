package com.haoren.ioc.util;

import com.haoren.ioc.annotation.Component;
import com.haoren.ioc.proxy.IocInterceptor;
import net.sf.cglib.proxy.Enhancer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationScannerUtil {

    private static final String EXT = "class";

    public static void main(String[] args) throws ClassNotFoundException {
        String packageName = "com.haoren";
        String packagePath = getPackagePath(packageName);
        List<Class<? extends Annotation>> annotations = new ArrayList<>();
        annotations.add(Component.class);
        Map<Class<? extends Annotation>, Set<Class<?>>> classSetMap = scanClassesByAnnotations(packageName, packagePath, true, annotations);
        for (Map.Entry<Class<? extends Annotation>, Set<Class<?>>> classSetEntry : classSetMap.entrySet()) {
            Class<? extends Annotation> key = classSetEntry.getKey();
            Set<Class<?>> value = classSetEntry.getValue();
            for (Class<?> aClass : value) {
                System.out.println(key + ":" + aClass.getName());
            }
        }
    }

    /**
     * 根据包名获取包路径
     *
     * @param packageName 包名
     * @return 包路径
     */
    public static String getPackagePath(String packageName) {
        String packageDirName = packageName.replace('.', '/');
        URL url = Thread.currentThread().getContextClassLoader().getResource(packageDirName);
        return null == url ? null : url.getFile();
    }

    /**
     * @param packageName 包名
     * @param packagePath 包路径
     * @param recursive   是否递归遍历子目录
     * @return 类集合
     */
    public static Set<Class<?>> scanClass(String packageName, String packagePath, boolean recursive) throws ClassNotFoundException {
        Set<Class<?>> classSet = new HashSet<>();
        Collection<File> allClassFile = getAllClassFile(packagePath, recursive);
        for (File file : allClassFile) {
            classSet.add(getClassObject(file, packagePath, packageName));
        }
        return classSet;
    }

    /**
     * 获取指定包下包含指定注解的所有类对象的集合
     *
     * @param packageName 包名
     * @param packagePath 包路径
     * @param recursive   是否递归遍历子目录
     * @param annotations 注解集合
     * @return 注解和对应类构成的map
     */
    public static Map<Class<? extends Annotation>, Set<Class<?>>> scanClassesByAnnotations(
            String packageName, String packagePath, boolean recursive, List<Class<? extends Annotation>> annotations) {
        Map<Class<? extends Annotation>, Set<Class<?>>> resultMap = new ConcurrentHashMap<>();
        Collection<File> allClassFile = getAllClassFile(packagePath, recursive);
        for (File file : allClassFile) {
            try {
                Class<?> classObject = getClassObject(file, packagePath, packageName);
                for (Class<? extends Annotation> annotation : annotations) {
                    if (classObject.isAnnotationPresent(annotation)) {
                        if (!resultMap.containsKey(annotation)) {
                            resultMap.put(annotation, new HashSet<>());
                        }
                        resultMap.get(annotation).add(classObject);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }

    private static Class<?> getClassObject(File file, String packagePath, String packageName) throws ClassNotFoundException {
        // 考虑class文件在子目录中的情况
        String absPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - EXT.length() - 1);
        String className = absPath.substring(packagePath.length()).replace(File.separatorChar, '.');
        className = className.startsWith(".") ? packageName + className : packageName + "." + className;
        Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(className);
        if (aClass.isAnnotation()) {
            return aClass;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(aClass);
        enhancer.setCallback(new IocInterceptor());
        return enhancer.create().getClass();
    }

    private static Collection<File> getAllClassFile(String packagePath, boolean recursive) {
        File fPkgDir = new File(packagePath);

        if (!(fPkgDir.exists() && fPkgDir.isDirectory())) {
            System.out.println("the directory to package is empty: " + packagePath);

            return null;
        }

        return FileUtils.listFiles(fPkgDir, new String[]{EXT}, recursive);
    }


    /**
     * 查找指定注解的Method
     *
     * @param classes           查找范围
     * @param targetAnnotations 指定的注解
     * @return 以注解和对应Method类集合构成的键值对
     */
    public static Map<Class<? extends Annotation>, Set<Method>> scanMethodsByAnnotations(Set<Class<?>> classes,
                                                                                         List<Class<? extends Annotation>> targetAnnotations) {
        Map<Class<? extends Annotation>, Set<Method>> resultMap = new HashMap<>(16);

        for (Class<?> cls : classes) {
            Method[] methods = cls.getMethods();

            for (Class<? extends Annotation> annotation : targetAnnotations) {
                for (Method method : methods) {
                    if (method.isAnnotationPresent(annotation)) {
                        if (!resultMap.containsKey(annotation)) {
                            resultMap.put(annotation, new HashSet<Method>());
                        }
                        resultMap.get(annotation).add(method);
                    }
                }
            }
        }
        return resultMap;
    }
}
