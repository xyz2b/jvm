package org.xyz.jvm.jdk.classes;

import lombok.Data;

@Data
final public class Handle {
    // oop/klass的内存地址
    private long p;

    // 这块内存所存储的数据类型
    // klass、oop、method
    private int type;

    // 对应Java类的全限定名
    private String className;
}
