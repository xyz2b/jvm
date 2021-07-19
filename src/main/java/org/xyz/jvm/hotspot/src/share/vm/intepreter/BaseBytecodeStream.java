package org.xyz.jvm.hotspot.src.share.vm.intepreter;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.tools.DataTranslate;
import org.xyz.jvm.hotspot.src.share.tools.Stream;
import org.xyz.jvm.hotspot.src.share.vm.memory.StackObj;
import org.xyz.jvm.hotspot.src.share.vm.oops.attribute.CodeAttribute;
import org.xyz.jvm.hotspot.src.share.vm.oops.MethodInfo;

// 字节码流的基类，属性定义为 protected 是因为该类是基类，是要其他类继承的
@Data
public class BaseBytecodeStream extends StackObj {
    // 该字节码流属于哪个方法
    protected MethodInfo belongMethod;
    // 该字节码流属于哪个Code属性
    protected CodeAttribute belongCode;

    // 该方法的字节码长度(Byte，存储字节码流的字节数组的大小)
    protected int length;
    // 字节码流的当前读取索引
    protected int index;
    // 存储字节码流的字节数组
    protected byte[] codes;

    /**
     * 一次读取一字节的数据，读取指针自动累加
     * 字节码指令都是单字节的，所以每个字节码指令都对应一个255以内的数值，如 ldc(助记符) 为 18
     * @return 读取的数据
     * */
    public int getU1Code() {
        checkIndex();

        return Byte.toUnsignedInt(codes[index++]);
    }

    /**
     * 一次读取两字节的数据，读取指针自动累加
     * @return 读取的数据
     * */
    public short getUnsignedShort() {
        checkIndex();

        byte[] u2Arr = new byte[2];
        Stream.readU2Simple(codes, index, u2Arr);
        index += 2;

        return (short) DataTranslate.byteToUnsignedShort(u2Arr);
    }

    /**
     * 检查字节流的读取索引是否超出范围
     * */
    private void checkIndex() throws Error {
        if (index < 0 || index > length) {
            throw new Error("字节码指令的索引超过了最大值");
        }
    }

    /**
     * 重置字节流的读取索引
     * */
    public void reset() {
        index = 0;
    }

    /**
     * 判断是否读到了字节流的结尾
     * @return 到达结尾为true，没有为false
     * */
    public boolean end() {
        return index >= length;
    }

    /**
     * 向后移动字节流读取索引
     * @param step 向后移动的步长
     * */
    public void inc(int step) {
        index +=  step;
    }
}