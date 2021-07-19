package org.xyz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.utilities.AccessFlags;

import java.util.HashMap;
import java.util.Map;

@Data
public class MethodInfo {
    // 成员方法的访问权限和基本属性，通过多个访问权限和属性 与操作 计算出来        u2
    private AccessFlags accessFlags;
    // 成员方法名在常量池中的索引  u2
    private int nameIndex;
    // 成员方法描述符在常量池中的索引，方法描述符如([Ljava/lang/String;)V    u2
    private int descriptorIndex;
    // 成员方法的属性个数，如 final    u2
    private int attributesCount;
    // 成员方法的属性详情表
    private Map<String, Attribute> attributes;

    // 方法名称，从常量池中获取
    private String methodName;

    // 方法所属类
    private InstanceKlass belongKlass;

    public void initAttributeContainer() {
        attributes = new HashMap<>(attributesCount);
    }
}
