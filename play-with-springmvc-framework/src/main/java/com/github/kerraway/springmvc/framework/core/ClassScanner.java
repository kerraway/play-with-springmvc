package com.github.kerraway.springmvc.framework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描器
 *
 * @author kerraway
 * @date 2019/09/15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassScanner {

    private static volatile ClassScanner scanner;

    public static ClassScanner getScanner() {
        if (scanner == null) {
            synchronized (ClassScanner.class) {
                if (scanner == null) {
                    scanner = new ClassScanner();
                }
            }
        }
        return scanner;
    }

    /**
     * 根据包扫描类
     *
     * @param pkg 包
     * @return 类集合
     * @throws IOException
     */
    public List<Class<?>> scanClassesFromJar(Package pkg) throws IOException {
        List<Class<?>> classes = new ArrayList<>(256);
        String pkgPath = pkg.getName().replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(pkgPath);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            List<Class<?>> partClasses = null;
            //处理 jar 包，适用于打包后运行的情况
            if (resource.getProtocol().equals("jar")) {
                JarFile jarFile = ((JarURLConnection) resource.openConnection()).getJarFile();
                partClasses = scanClassesFromJar(jarFile.getName(), pkgPath);
            }
            //处理文件，适用于在 IDE 中直接启动的情况
            else if (resource.getProtocol().equals("file")) {
                partClasses = scanClassesFromDir(resource.getPath(), pkgPath);
            }
            if (partClasses != null && !partClasses.isEmpty()) {
                classes.addAll(partClasses);
            }
        }
        return classes;
    }

    /**
     * 从文件夹中扫描类
     * pkgDirPath: ${buildDirPath}/classes/java/main/${pkgPath}
     * pkgPath: com/github/kerraway/springmvc/example/controller
     *
     * @param pkgDirPath 包文件夹路径
     * @param pkgPath    包路径
     * @return 类集合
     * @throws IOException
     */
    private List<Class<?>> scanClassesFromDir(String pkgDirPath, String pkgPath) throws IOException {
        File pkgDir = new File(pkgDirPath);
        if (!pkgDir.isDirectory()) {
            return Collections.emptyList();
        }
        List<Class<?>> classes = new ArrayList<>(256);
        Files.walkFileTree(pkgDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                //filePath: ${buildDirPath}/classes/java/main/${pkgPath}/${fileName}.class
                String filePath = file.toString();
                String pkgFileName = pkgPath + filePath.substring(pkgDirPath.length());
                Class<?> clazz = loadClass(pkgFileName, pkgPath);
                if (clazz != null) {
                    classes.add(clazz);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classes;
    }

    /**
     * 从 jar 包中扫描类
     *
     * @param jarFilePath jar 包路径
     * @param pkgPath     包路径
     * @return 类集合
     * @throws IOException
     */
    private List<Class<?>> scanClassesFromJar(String jarFilePath, String pkgPath) throws IOException {
        List<Class<?>> classes = new ArrayList<>(256);
        JarFile jarFile = new JarFile(jarFilePath);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            Class<?> clazz = loadClass(jarEntry.getName(), pkgPath);
            if (clazz != null) {
                classes.add(clazz);
            }
        }
        return classes;
    }

    /**
     * 加载类
     *
     * @param pkgFileName 包+文件名，${pkgPath}/${fileName}.class
     * @param pkgPath     包路径
     * @return 加载的类
     * @throws RuntimeException 加载类失败时，抛出该异常
     */
    private Class<?> loadClass(String pkgFileName, String pkgPath) {
        Class<?> clazz = null;
        if (pkgFileName.startsWith(pkgPath) && pkgFileName.endsWith(".class")) {
            String className = pkgFileName.replace("/", ".")
                    .substring(0, pkgFileName.length() - 6);
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                // TODO: 2019/9/17 specific exception
                throw new RuntimeException(String.format("Load class '%s' error.", className), e);
            }
        }
        return clazz;
    }

}
