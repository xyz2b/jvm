package org.xyz.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Java虚拟机栈 栈帧中的集合结构: 操作数栈、局部变量表，它们都是由一个个4字节的槽位组成
 * */
@Data
@Slf4j
public class StackValueCollection {
    // 生成操作数栈
    public StackValueCollection() {}

    // 生成局部变量表
    public StackValueCollection(int localVariableTableSize) {
        maxLocals = localVariableTableSize;
        localVariableTable = new ArrayList<>(maxLocals);
    }


    /**
     * 模拟操作数栈
     * */
    private Stack<StackValue> operandStack = new Stack<>();
    // 出栈
    public StackValue pop() {
        return getOperandStack().pop();
    }
    // 入栈
    public void push(StackValue value) {
        getOperandStack().push(value);
    }
    // 查看栈顶元素
    public StackValue peek() {
        return getOperandStack().peek();
    }


    /**
     * 模拟局部变量表
     * */
    private List<StackValue> localVariableTable;
    private int maxLocals;
    // 设置
    public void set(int index, StackValue value) {
        getLocalVariableTable().set(index, value);
    }
    // 获取
    public StackValue get(int index) {
        return getLocalVariableTable().get(index);
    }

}
