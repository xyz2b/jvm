package org.xyz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

@Data
public class Attribute {
    public static final String JVM_ATTRIBUTE_ConstantValue = "ConstantValue";
    public static final String JVM_ATTRIBUTE_Code = "Code";
    public static final String JVM_ATTRIBUTE_Exception = "Exception";
    public static final String JVM_ATTRIBUTE_LineNumberTable = "LineNumberTable";
    public static final String JVM_ATTRIBUTE_LocalVariableTable = "LocalVariableTable";
    public static final String JVM_ATTRIBUTE_SourceFile = "SourceFile";
    public static final String JVM_ATTRIBUTE_StackMapTable = "StackMapTable";
    public static final String JVM_ATTRIBUTE_BootstrapMethods = "BootstrapMethods";
    public static final String JVM_ATTRIBUTE_InnerClasses = "InnerClasses";

    // 属性名称在常量池中的索引(CONSTANT_Utf8_info，属性名称的字符串)     u2
    protected int attributeNameIndex;

    // 属性中除了 attributeNameIndex 和 attributeLength 之外所有字段的长度(单位: byte)   u4
    protected int attributeLength;
}
