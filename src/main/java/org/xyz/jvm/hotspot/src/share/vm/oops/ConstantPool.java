package org.xyz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量池由5种结构的类型
 * 1.CONSTANT_Utf8_info:                            tag(u1)     length(u2)      byte[length]                解析时解析成String，获取时再进行类型强转
 * 2.CONSTANT_Integer_info、CONSTANT_Float_info      tag(u1)     bytes[4](u4)                               解析时解析成Integer、Float，获取时再进行类型强转
 * 3.CONSTANT_Long_info、CONSTANT_Double_info:       tag(u1)     bytes[8](u8)                               解析时解析成Long、Double，获取时再进行类型强转
 * 4.CONSTANT_String_info、CONSTANT_Class_info:      tag(u1)     index(u2)                                  获取时获取真正的内容，比如字符串值、类名
 * 5.CONSTANT_NameAndType_info、CONSTANT_Filedref_info、CONSTANT_Methodref_info、CONSTANT_InterfaceMethodref_info:     tag(u1)     index(u2)      index(u2)    解析时两个u2存储在一个int中，获取时进行处理
 * */
@Data
public class ConstantPool {
    public static final int JVM_CONSTANT_Utf8 = 1;
    public static final int JVM_CONSTANT_Unicode = 2;   /* unused */
    public static final int JVM_CONSTANT_Integer = 3;
    public static final int JVM_CONSTANT_Float = 4;
    public static final int JVM_CONSTANT_Long = 5;
    public static final int JVM_CONSTANT_Double = 6;
    public static final int JVM_CONSTANT_Class = 7;
    public static final int JVM_CONSTANT_String = 8;
    public static final int JVM_CONSTANT_Fieldref = 9;
    public static final int JVM_CONSTANT_Methodref = 10;
    public static final int JVM_CONSTANT_InterfaceMethodref = 11;
    public static final int JVM_CONSTANT_NameAndType = 12;
    public static final int JVM_CONSTANT_MethodHandle = 15; /* JSR 292 */
    public static final int JVM_CONSTANT_MethodType = 16;   /* JSR 292 */
    public static final int JVM_CONSTANT_InvokeDynamic = 18;    /* JSR 292 */
    public static final int JVM_CONSTANT_ExternalMax = 18;  /* Last tag found in classfiles */

    // 该常量池所属的 Klass 信息
    private Klass klass;

    // 常量池的大小
    private int length;

    // 常量池的索引-->常量池项的类型tag
    private int[] tag;
    // 常量池的索引-->常量池项
    private Map<Integer, Object> dataMap;

    public ConstantPool(Klass klass) {
        this.klass = klass;
    }

    public void initContainer() {
        tag = new int[length];
        dataMap = new HashMap<>(length);
    }

    /**
     * @param index Utf8 结构在常量池中的索引
     * @return Utf8 结构存储的字符串值
     * */
    public String getUtf8(int index) {
        if (!checkIndex(index)) return null;
        return (String) dataMap.get(index);
    }

    /**
     * @param index Integer 结构在常量池中的索引
     * @return Integer 结构的值
     * */
    public Integer getInteger(int index) {
        if (!checkIndex(index)) return null;
        return (int) dataMap.get(index);
    }

    /**
     * @param index Float 结构在常量池中的索引
     * @return Float 结构的值
     * */
    public Float getFloat(int index) {
        if (!checkIndex(index)) return null;
        return (float) dataMap.get(index);
    }

    /**
     * @param index Long 结构在常量池中的索引
     * @return Long 结构的值
     * */
    public Long getLong(int index) {
        if (!checkIndex(index)) return null;
        return (long) dataMap.get(index);
    }

    /**
     * @param index Double 结构在常量池中的索引
     * @return Double 结构的值
     * */
    public Double getDouble(int index) {
        if (!checkIndex(index)) return null;
        return (double) dataMap.get(index);
    }

    /**
     * 解释：
     *  1.参数index对应的是 JVM_CONSTANT_String
     *  2.JVM_CONSTANT_String 中的信息是字符串值在常量池中的索引，真的值存储在 JVM_CONSTANT_Utf8 中
     * @param index String 结构在常量池中的索引
     * @return String字段的值
     */
    public String getString(int index) {
        if (!checkIndex(index)) return null;
        return getUtf8((int) dataMap.get(index));
    }

    /**
     * @param index Class 结构在常量池中的索引
     * @return Class 的名字
     * */
    public String getClassName(int index) {
        if (!checkIndex(index)) return null;
        return getUtf8((int) dataMap.get(index));
    }

    /**
     * CONSTANT_NameAndType_info: name_index + descriptor_index
     * @param index NameAndType 结构在常量池中的索引
     * @return NameAndType 字段中 name 字段的值
     * */
    public String getName(int index) {
        if (!checkIndex(index)) return null;

        // 获取 NameAndType 在常量池中的信息(name_index + descriptor_index)
        int data = (int) dataMap.get(index);

        // 获取 name_index，int的前2个字节
        int nameIndex = data >> 16;

        return getUtf8(nameIndex);
    }

    /**
     * CONSTANT_NameAndType_info: name_index + descriptor_index
     * @param index NameAndType 结构在常量池中的索引
     * @return NameAndType 字段中 descriptor 字段的值
     * */
    public String getDescriptor(int index) {
        if (!checkIndex(index)) return null;

        // 获取 NameAndType 在常量池中的信息(name_index + descriptor_index)
        int data = (int) dataMap.get(index);

        // 获取 descriptor_index，int的后2个字节
        int descriptorIndex = data & 0xFF;

        return getUtf8(descriptorIndex);
    }

    /**
     * CONSTANT_Filedref_info: class_index + nameAndType_index
     * @param index Filedref 结构在常量池中的索引
     * @return 字段的名字
     * */
    public String getFiledName(int index) {
        if (!checkIndex(index)) return null;

        // 获取 Filedref 在常量池中的信息(class_index + nameAndType_index)
        int data = (int) dataMap.get(index);

        // 获取 nameAndType_index，int的后2个字节
        int nameAndTypeIndex = data & 0xFF;

        return getName(nameAndTypeIndex);
    }

    /**
     * CONSTANT_Filedref_info: class_index + nameAndType_index
     * @param index Filedref 结构在常量池中的索引
     * @return 字段的描述符
     * */
    public String getFiledDescriptor(int index) {
        if (!checkIndex(index)) return null;

        // 获取 Filedref 在常量池中的信息(class_index + nameAndType_index)
        int data = (int) dataMap.get(index);

        // 获取 nameAndType_index，int的后2个字节
        int nameAndTypeIndex = data & 0xFF;

        return getDescriptor(nameAndTypeIndex);
    }

    /**
     * CONSTANT_Filedref_info: class_index + nameAndType_index
     * @param index Filedref 结构在常量池中的索引
     * @return 字段所属类的类名
     * */
    public String getClassNameByFieldInfo(int index) {
        if (!checkIndex(index)) return null;

        // 获取 Filedref 在常量池中的信息(class_index + nameAndType_index)
        int data = (int) dataMap.get(index);

        // 获取 class_index，int的前2个字节
        int classIndex = data >> 16;

        return getClassName(classIndex);
    }

    /**
     * CONSTANT_Methodref_info: class_index + nameAndType_index
     * @param index Methodref 结构在常量池中的索引
     * @return 方法的名字
     * */
    public String getMethodName(int index) {
        if (!checkIndex(index)) return null;

        // 获取 Methodref 在常量池中的信息(class_index + nameAndType_index)
        int data = (int) dataMap.get(index);

        // 获取 nameAndType_index，int的后2个字节
        int nameAndTypeIndex = data & 0xFF;

        return getName(nameAndTypeIndex);
    }

    /**
     * CONSTANT_Methodref_info: class_index + nameAndType_index
     * @param index Methodref 结构在常量池中的索引
     * @return 方法的描述符
     * */
    public String getMethodDescriptor(int index) {
        if (!checkIndex(index)) return null;

        // 获取 Methodref 在常量池中的信息(class_index + nameAndType_index)
        int data = (int) dataMap.get(index);

        // 获取 nameAndType_index，int的后2个字节
        int nameAndTypeIndex = data & 0xFF;

        return getDescriptor(nameAndTypeIndex);
    }

    /**
     * CONSTANT_Methodref_info: class_index + nameAndType_index
     * @param index Methodref 结构在常量池中的索引
     * @return 方法所属类的类名
     * */
    public String getClassNameByMethodInfo(int index) {
        if (!checkIndex(index)) return null;

        // 获取 Methodref 在常量池中的信息(class_index + nameAndType_index)
        int data = (int) dataMap.get(index);

        // 获取 class_index，int的前2个字节
        int classIndex = data >> 16;

        return getClassName(classIndex);
    }

    /**
     * 检查常量池的索引是否超出范围
     * @param index 常量池的索引
     * @return true 没有超出范围，false 超出范围
     * */
    private boolean checkIndex(int index) throws Error {
        return index > 0 && index <= length;
    }
}
