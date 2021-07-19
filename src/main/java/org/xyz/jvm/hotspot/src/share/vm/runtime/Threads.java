package org.xyz.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.memory.AllStatic;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程管理器
 * */
@Data
public class Threads extends AllStatic {
    // 所有Java基本线程（真实线程）都存储在这个List中
    private static List<Thread> threadList;

    // 当前线程
    private static Thread currentThread;

    static {
        threadList = new ArrayList<>();
    }

    public static JavaThread currentThread() { return (JavaThread) currentThread; }
    public static void setCurrentThread(Thread thread) { currentThread = thread; }

    public static List<Thread> getThreadList() { return threadList; }

}
