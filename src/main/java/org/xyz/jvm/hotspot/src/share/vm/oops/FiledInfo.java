package org.xyz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FiledInfo {
    // 成员字段的访问权限和基本属性，通过多个访问权限和属性 与操作 计算出来        u2
    private int accessFlag;
    // 成员字段名在常量池中的索引  u2
    private int nameIndex;
    // 成员字段描述符在常量池中的索引，字段描述符如I    u2
    private int descriptorIndex;
    // 成员字段的属性个数，如 final    u2
    private int attributesCount;
    // 成员字段的属性详情表
    private Map<String, Attribute> attributes;

    public void initAttributeContainer() {
        attributes = new HashMap<>(attributesCount);
    }
}
