package org.xyz.jvm.jdk.classes;

final public class Handle {
    // oop/klass的内存地址
    private long p;

    // 这块内存所存储的数据类型
    // klass、oop、method
    private int type;

    // 对应Java类的全限定名
    private String className;

    public long getP() {
        return p;
    }

    public void setP(long p) {
        this.p = p;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
