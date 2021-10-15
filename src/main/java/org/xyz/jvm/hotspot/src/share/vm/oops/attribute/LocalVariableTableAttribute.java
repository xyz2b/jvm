package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Code 的属性
 * */
@Data
public class LocalVariableTableAttribute extends Attribute {
    // LocalVariableTable 表项数       u2
    private int localVariableTableLength;
    // LocalVariableTable
    private List<LocalVariable> localVariableTable;

    public void initLocalVariableTable() {
        localVariableTable = new ArrayList<>(localVariableTableLength);
    }

    /**
     * LocalVariableTable 中表项的结构
     * */
    @Data
    public static class LocalVariable{
        // 局部变量在code[]中的有效范围，即局部变量的作用域，[startPc, startPc+length)      u2
        private int startPc;
        private int length;

        // 局部变量名称在常量池中的索引       u2
        private int nameIndex;

        // 局部变量的描述符在常量池中的索引     u2
        private int descriptorIndex;

        // 该局部变量在局部变量表中的索引      u2
        private int index;
    }
}
