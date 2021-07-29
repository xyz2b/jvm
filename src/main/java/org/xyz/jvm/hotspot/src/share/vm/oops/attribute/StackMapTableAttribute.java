package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;

/**
 * Method的属性
 * */
@Data
public class StackMapTableAttribute extends Attribute {
    // StackMapTable 表项数  u2
    private int numberOfEntries;

}
