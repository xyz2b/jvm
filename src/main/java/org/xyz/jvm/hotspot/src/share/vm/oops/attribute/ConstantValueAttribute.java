package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;

/**
 * Filed的属性
 * */
@Data
public class ConstantValueAttribute extends Attribute {
    // 常量池中的索引，常量池表在该索引处的成员给出了该属性所表示的常量值        u2
    private int constantValueIndex;
}
