package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Code 的属性
 * */
@Data
public class LineNumberTableAttribute extends Attribute {
    // LineNumberTable 表项数       u2
    private int lineNumberTableLength;
    // LineNumberTable
    private List<LineNumber> lineNumberTables;

    public void initLineNumberTables() {
        lineNumberTables = new ArrayList<>(lineNumberTableLength);
    }

    /**
     * LineNumberTable中的记录结构
     * */
    @Data
    public static class LineNumber {
        // code[]中虚拟机指令的索引      u2
        private int startPc;
        // 源文件中对应code[]该索引处指令的行号
        private int lineNumber;
    }
}
