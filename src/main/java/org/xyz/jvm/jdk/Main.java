package org.xyz.jvm.jdk;

import org.xyz.jvm.jdk.classes.JniEnv;
import org.xyz.jvm.jdk.classes.sun.misc.AppClassLoader;
import org.xyz.jvm.hotspot.src.share.vm.prims.JavaNativeInterface;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaThread;
import org.xyz.jvm.hotspot.src.share.vm.runtime.Threads;
import org.xyz.jvm.hotspot.src.share.vm.classfile.BootClassLoader;
import org.xyz.jvm.hotspot.src.share.vm.oops.InstanceKlass;
import org.xyz.jvm.hotspot.src.share.vm.oops.MethodInfo;
import org.xyz.jvm.jdk.classes.Handle;

public class Main {
    public static void main(String[] args) {
        // 以下是直接调用Java写的jvm代码
        // class文件的搜索路径请修改 org.xyz.jvm.hotspot.src.share.vm.classfile.BootClassLoader.searchPath 属性
        startJvm();

        // 以下是通过JNI手段，调用C++写的jvmplus代码               
//        System.out.println(System.getProperty("java.library.path"));
//        System.loadLibrary("jni");

//        org.xyz.jvm.jdk.classes.Threads.createVm();

//        Handle klassHandle = AppClassLoader.loadKlass("org/xyz/jvm/example/HelloWorld");
//        Handle klassHandle = AppClassLoader.loadKlass("org/xyz/jvm/example/test/Test");
//        Handle klassHandle = AppClassLoader.loadKlass("org/xyz/jvm/example/HelloWorld");

//        Handle methodHandle = JniEnv.getMethodId(klassHandle, "main", "([Ljava/lang/String;)V");

//        JniEnv.callStaticVoidMethod(klassHandle, methodHandle);

//        System.out.println("执行结束");
}

    public static void startJvm() {
        // 通过BootClassLoader加载main函数所在的类
        InstanceKlass klass = BootClassLoader.loadMainClass("org.xyz.jvm.example.exception.ExceptionHappen");

        // 找到main方法
        MethodInfo main = JavaNativeInterface.getMethod(klass, "main", "([Ljava/lang/String;)V");

        if (main == null) {
            return;
        }

        // 创建线程，此处仅为模拟
        JavaThread thread = new JavaThread();

        // 将新创建的线程存放到线程管理器中
        Threads.getThreadList().add(thread);
        // 设置线程管理器的当前线程
        Threads.setCurrentThread(thread);

        // 执行main方法
        JavaNativeInterface.callStaticMethod(main);
    }
}
