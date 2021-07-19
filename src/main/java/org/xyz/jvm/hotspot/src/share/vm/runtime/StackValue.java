package org.xyz.jvm.hotspot.src.share.vm.runtime;

/**
 * 操作数栈或局部变量中一个槽位（4字节）的数据
 * */
public class StackValue {
    // 类型
    private int type;

    // 真实存储数据的地方
    private int value;
    private Object object;

    // 存储4字节数据
    public StackValue(int type, int value) {
        this.type = type;
        this.value = value;
    }

    // 存储Object对象数据
    public StackValue(int type, Object val) {
        this.type = type;
        this.object = val;
    }
}
