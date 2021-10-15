package org.xyz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

/**
 * 解析过的描述符信息
 * */
@Data
public class DescriptorInfo {
    //是否完成解析并赋值，默认false
    private boolean isResolved = false;

    // 本描述符在本JVM内部的类型
    private int type;

    // 数组维度，只有数组才有该属性
    private int arrayDimension;

    // 引用类型的类型，只有引用类型才有该属性
    private String typeDesc;

    // 数组元素的类型，只有数组类型才有该属性
    private DescriptorInfo arrayElementType;

    public void incArrayDimension() {
        arrayDimension++;
    }

    public DescriptorInfo() {
    }

    public DescriptorInfo(boolean isResolved, int type) {
        this.isResolved = isResolved;
        this.type = type;
    }

    public DescriptorInfo(boolean isResolved, int type, String typeDesc) {
        this.isResolved = isResolved;
        this.type = type;
        this.typeDesc = typeDesc;
    }
}
