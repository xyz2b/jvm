package org.xyz.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.tools.DataTranslate;
import org.xyz.jvm.hotspot.src.share.vm.utilities.BasicType;

/**
 * 模拟操作数栈或局部变量中一个槽位的数据，并不是和JVM中一样一个槽位4个字节，这里槽位的大小是4字节或者8字节，根据数据类型决定
 * float、long使用byte数组存储
 * double类型的数据使用两个4字节的槽位来存储
 * 其他类型的数据使用4字节的槽位来存储
 * */
@Data
public class StackValue {
    // 类型
    private int type;

    // 真实存储数据的地方
    // 存储float、long类型数据
    private byte[] data;
    // 存储boolean、byte、char、short、int类型数据（4字节以及4字节以下的数据类型）
    private int value;
    // 存储引用类型数据（数据类型的数据？）
    private Object object;

    /**
     * 存储4字节数据已经4字节以下的数据类型
     * */
    public StackValue(int type, int value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 存储引用类型数据
     * */
    public StackValue(int type, Object value) {
        this.type = type;
        this.object = value;
    }

    /**
     * 存储float，大端字节序
     * */
    public StackValue(int type, float value) {
        this.type = type;
        this.data = DataTranslate.floatToByte(value);
    }

    /**
     * 存储long，大端字节序
     * */
    public StackValue(int type, long value) {
        this.type = type;
        this.data = DataTranslate.longToByte(value);
    }

    /**
     * 获取某一个槽位中的数据，由于double使用两个槽位存储，所以不在该方法处理范围内
     * */
    public Object getData() {
        switch (type) {
            case BasicType.T_FLOAT:
                return DataTranslate.byteToFloat(data);
            case BasicType.T_LONG:
                return DataTranslate.byteToLong(data);
            case BasicType.T_INT:
                return value;
            case BasicType.T_BOOLEAN:
                return 1 == value;
            case BasicType.T_BYTE:
                return (byte) value;
            case BasicType.T_CHAR:
                return (char) value;
            case BasicType.T_SHORT:
                return (short) value;
            case BasicType.T_OBJECT:
                return object;
            case BasicType.T_ARRAY:
                // TODO: 支持从操作数栈中弹出数组类型的数据
                return null;
        }
        return null;
    }
}
