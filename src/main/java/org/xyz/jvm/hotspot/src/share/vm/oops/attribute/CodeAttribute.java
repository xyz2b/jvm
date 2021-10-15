package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.intepreter.ByteCodeStream;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Method 的属性
 * */
@Data
public class CodeAttribute extends Attribute {
    // 当前方法的操作数栈最大深度(槽数)    u2
    private int maxStack;
    // 当前方法的局部变量表最大槽数   u2
    private int maxLocals;

    // 该方法中的指令长度(Byte)  u4
    private int codeLength;
    // 字节码流
    private ByteCodeStream code;

    // 方法异常表的表项数    u2
    private int exceptionTableLength;
    // 异常表(try...catch的异常)，每个表项代表一个异常处理器，该表内的表项需要注意顺序
    private List<ExceptionHandler> exceptionTables;

    // Code的属性个数    u2
    private int attributesCount;
    // Code的属性详情表
    private Map<String, Attribute> attributes;


    public void initExceptionTables() {
        exceptionTables = new ArrayList<>(exceptionTableLength);
    }

    public void initAttributes() {
        attributes = new HashMap<>(attributesCount);
    }

    // 获取异常处理handle
    public CodeAttribute.ExceptionHandler findExceptionHandle(int codeIndex) {
        if (exceptionTableLength == 0) {
            return null;
        }
        for (CodeAttribute.ExceptionHandler e : exceptionTables) {
            if (codeIndex >= e.startPc && codeIndex <= e.endPc) {
                return e;
            }
        }
        return null;
    }

    /**
     * 异常处理器
     * */
    @Data
    public static class ExceptionHandler {
        // 该异常处理器在code[]中的有效范围  u2
        private int startPc;
        private int endPc;

        // 异常处理器的起点(异常处理Handler)    u2
        private int handlerPc;

        // 异常处理器捕捉的异常类型在常量池中的索引(catch 捕获的异常类型)，常量池在该索引处的成员必须是CONSTANT_Class_info结构     u2
        private int catchType;
    }
}
