package org.xyz.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.xyz.jvm.hotspot.src.share.tools.DataTranslate;
import org.xyz.jvm.hotspot.src.share.vm.utilities.BasicType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Java虚拟机栈 栈帧中的集合结构: 操作数栈、局部变量表，它们都是由一个个4字节的槽位组成
 * */
@Data
@Slf4j
public class StackValueCollection {
    /************************************************************************
     * 模拟操作数栈
     * */
    // 生成操作数栈
    public StackValueCollection() {}

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

    // 入栈int类型元素
    public void pushInt(int value, JavaVFrame frame) {
        frame.getOperandStack().push(new StackValue(BasicType.T_INT, value));
    }

    // 入栈null对象
    public void pushNull(JavaVFrame frame) {
        frame.getOperandStack().push(new StackValue(BasicType.T_OBJECT, null));
    }

    /**
     * 将double类型的值压入操作数栈中
     * double是8字节，所以使用两个4字节槽位来存储（int类型）
     * @param value 需要存入操作数栈
     * */
    public void pushDouble(double value) {
        byte[] doubleByte = DataTranslate.doubleToByte(value);

        // 低位
        byte[] valueLowByte = new byte[4];
        System.arraycopy(doubleByte, 0, valueLowByte, 0, 4);
        int valueLow = DataTranslate.byteToInt(valueLowByte);
        push(new StackValue(BasicType.T_DOUBLE, valueLow));

        // 高位
        byte[] valueHighByte = new byte[4];
        System.arraycopy(doubleByte, 4, valueHighByte, 0, 4);
        int valueHigh = DataTranslate.byteToInt(valueHighByte);
        push(new StackValue(BasicType.T_DOUBLE, valueHigh));
    }

    /**
     * 从操作数栈中弹出double类型的值
     * 因为double类型的数据占两个槽位（两个int类型的槽位来存储），所以获取时需要连续获取两个槽位的数据，然后再组合成double
     * 注意高低位的顺序，放入时是低位先进，高位后进，取出时是高位先出，低位后出
     * */
    public double popDouble() {
        byte[] doubleByte = new byte[8];

        // 高位
        StackValue valueHigh = pop();
        // 将int转成字节数组
        byte[] valueHighByte = DataTranslate.intToByte(valueHigh.getValue());
        // 将高位字节复制到double字节数组的高位
        System.arraycopy(valueHighByte, 0, doubleByte, 4, 4);

        // 低位
        StackValue valueLow = pop();
        // 将int转成字节数组
        byte[] valueLowByte = DataTranslate.intToByte(valueLow.getValue());
        // 将低位字节复制到double字节数组的低位
        System.arraycopy(valueLowByte, 0, doubleByte, 0, 4);

        if (valueHigh.getType() != BasicType.T_DOUBLE || valueLow.getType() != BasicType.T_DOUBLE) {
            throw new Error("类型检查不通过");
        }

        return DataTranslate.byteToDouble(doubleByte);
    }

    /**
     * 从操作数栈中弹出连续两个元素
     * @return 操作数栈中弹出的两个元素的数组，依次排列，先弹出来的位于0索引，后弹出来的位于1索引
     * */
    public StackValue[] popDouble2() {
        StackValue[] ret = new StackValue[2];

        ret[0] = pop();
        ret[1] = pop();

        return ret;
    }


    /*************************************************************************
     * 模拟局部变量表
     * */
    // 生成局部变量表
    public StackValueCollection(int localVariableTableSize) {
        maxLocals = localVariableTableSize;
        localVariableTable = new StackValue[maxLocals];;
    }

    private StackValue[] localVariableTable;
    private int maxLocals;
    // 设置
    public void set(int index, StackValue value) {
        if (index < 0 || index >= maxLocals) {
            throw new Error("超出localVariableTable的索引范围: [0, " + (maxLocals - 1) + "], give index: " + index);
        }
        getLocalVariableTable()[index] = value;
    }
    // 获取
    public StackValue get(int index) {
        if (index < 0 || index >= maxLocals) {
            throw new Error("超出localVariableTable的索引范围: [0, " + (maxLocals - 1) + "], give index: " + index);
        }
        return getLocalVariableTable()[index];
    }

}
