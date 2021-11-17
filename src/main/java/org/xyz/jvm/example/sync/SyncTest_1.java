package org.xyz.jvm.example.sync;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

// 测试偏向延迟之前创建的对象会不会被修改，由无锁变成偏向锁
public class SyncTest_1 {
    public static void main(String[] args) throws InterruptedException {
        Object obj1 = new Object();

        TimeUnit.SECONDS.sleep(5);

        Object obj2 = new Object();

        System.out.println(ClassLayout.parseInstance(obj1).toPrintable());
        System.out.println(ClassLayout.parseInstance(obj2).toPrintable());
    }
}
