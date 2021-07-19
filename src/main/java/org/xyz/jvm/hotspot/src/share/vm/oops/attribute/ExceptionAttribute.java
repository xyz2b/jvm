package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Method 的属性
 * */
@Data
public class ExceptionAttribute extends Attribute {
    // 方法抛出的异常数量    u2
    private int numberOfExceptions;
    // 方法抛出的所有异常的类型在常量池中索引的列表
    private List<Integer> exceptionIndexTable;

    public void initExceptionIndexTable() {
        exceptionIndexTable = new ArrayList<>(numberOfExceptions);
    }
}
