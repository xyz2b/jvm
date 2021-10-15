package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;

/**
 * 类的属性，一个ClassFile最多只能包含一个该属性
 * */
@Data
public class SourceFileAttribute extends Attribute {
    // 文件名在常量池中的索引
    private int sourceFileIndex;
}
