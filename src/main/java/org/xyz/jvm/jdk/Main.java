package org.xyz.jvm.jdk;

import org.xyz.jvm.hotspot.src.share.vm.prims.JavaNativeInterface;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaThread;
import org.xyz.jvm.hotspot.src.share.vm.runtime.Threads;
import org.xyz.jvm.hotspot.src.share.vm.classfile.BootClassLoader;
import org.xyz.jvm.hotspot.src.share.vm.oops.InstanceKlass;
import org.xyz.jvm.hotspot.src.share.vm.oops.MethodInfo;

public class Main {
    public static void main(String[] args) {
        startJvm();
    }

    public static void startJvm() {
        // 通过BootClassLoader加载main函数所在的类
        InstanceKlass klass = BootClassLoader.loadMainClass("org.xyz.jvm.example.loop.While");

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
