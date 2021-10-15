package org.xyz.jvm.hotspot.src.share.vm.classfile;

import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.xyz.jvm.hotspot.src.share.vm.oops.InstanceKlass;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 根类加载器
 * */
@Data
@Slf4j
public class BootClassLoader {
    // Class文件的扩展名
    public static final String SUFFIX = ".class";

    // 该类加载器的加载路径，多个路径以分号分隔，只有一个的话，分号可省略。注意路径后面的斜杠不可丢
    private static String searchPath = "/home/xyzjiao/Desktop/project/jvm/target/classes/";

    // 缓存该类加载器加载的所有类
    private static Map<String, InstanceKlass> classLoaderData = new HashMap<>();

    // main函数所在类在此保存一份引用，方便快速定位
    private static InstanceKlass mainKlass = null;

    public static InstanceKlass getMainKlass() {
        return mainKlass;
    }

    public static void setMainKlass(InstanceKlass mainKlass) {
        BootClassLoader.mainKlass = mainKlass;
    }

    /**
     * 判断某个类是否加载过
     * @param className 需要查找的类全限定名
     * */
    public static boolean isLoadedKlass(String className) {
        return classLoaderData.containsKey(className);
    }

    /**
     * 从类加载的缓存中寻找加载过的类
     * @param className 需要查找的类全限定名
     * */
    public static InstanceKlass findLoadedKlass(String className) {
        return classLoaderData.get(className);
    }

    /**
     * 将加载过的类存入类加载器的缓存
     * @param className 类的全限定名
     * @param klass 类的Klass模型
     * */
    public static InstanceKlass saveLoadedKlass(String className, InstanceKlass klass) {
        return classLoaderData.put(className, klass);
    }

    /**
     * 加载main函数所在的Class
     * @param className main函数所在类的全限定名
     * @return 加载完成后生成的Klass模型InstanceKlass
     * */
    public static InstanceKlass loadMainClass(String className) {
        if (mainKlass != null) {
            return mainKlass;
        }

        return loadKlass(className);
    }

    /**
     * 加载并解析类（加载阶段）
     * @param className 类的全限定名
     * @return 加载完成后生成的Klass模型InstanceKlass
     * */
    public static InstanceKlass loadKlass(String className) {
        return loadKlass(className, true);
    }

    /**
     * 加载类（加载阶段）
     * @param className 类的全限定名
     * @param resolve 加载之后是否要立刻解析
     * @return 加载完成后生成的Klass模型InstanceKlass
     * */
    public static InstanceKlass loadKlass(String className, boolean resolve) {
        // 查询缓存是否已经加载过了，如果是已经加载过的类直接返回
        InstanceKlass klass = findLoadedKlass(className);
        if (klass != null) {
            return klass;
        }

        // 读取并解析Class文件
        klass = readAndParse(className);

        // 是否立刻解析
        if (resolve) {
            // 解析
            resolveKlass();
        }

        return klass;
    }

    /**
     * 读取并解析Class文件
     * */
    private static InstanceKlass readAndParse(String className) {
        String tmpName = className.replace(".", "/");
        String classFilePath = searchPath + tmpName + SUFFIX;

        // 读取字节码文件
        File classFile = new File(classFilePath);
        byte[] content = FileUtil.readBytes(classFile);

        // 解析字节码文件
        InstanceKlass klass = ClassFileParser.parseClassFile(content);

        // 将加载过的类存入类加载器的缓存
        saveLoadedKlass(className, klass);

        return klass;
    }

    /**
     * 解析类（解析阶段）
     * 暂未实现
     * */
    private static void resolveKlass() {
    }
}
