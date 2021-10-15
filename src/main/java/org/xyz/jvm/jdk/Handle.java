package org.xyz.jvm.jdk;

import lombok.Data;

@Data
final public class Handle {
    // 申请的内存地址
    private long p;

    // 这块内存所存储的数据类型
    // klass、oop、method
    private int type;

    // 对应Java类的全限定名
    private String className;
}
