package org.xyz.jvm.example.sync;

// 测试偏向延迟之前创建的对象会不会被修改，由无锁变成偏向锁
public class SyncTest_1 {
    public static void main(String[] args) {
        Object obj1 = new Object();

        System.out.println();
    }
}
