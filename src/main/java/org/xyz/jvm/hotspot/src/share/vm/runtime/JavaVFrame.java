package org.xyz.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.MethodInfo;

/**
 * Java线程虚拟机栈中的方法栈帧
 * */
@Data
public class JavaVFrame extends VFrame {
    // 栈帧中的局部变量表
    private StackValueCollection localVariableTable;
    // 栈帧中的操作数栈
    private StackValueCollection operandStack;

    // 该栈帧所属的方法
    private MethodInfo methodInfo;

    // 创建栈帧
    public JavaVFrame(int maxLocals, MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
        localVariableTable = new StackValueCollection(maxLocals);
        operandStack = new StackValueCollection();
    }
}
