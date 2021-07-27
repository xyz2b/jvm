package org.xyz.jvm.hotspot.src.share.vm.intepreter;

import lombok.extern.slf4j.Slf4j;
import org.xyz.jvm.hotspot.src.share.vm.classfile.DescriptorStream;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;
import org.xyz.jvm.hotspot.src.share.vm.oops.ConstantPool;
import org.xyz.jvm.hotspot.src.share.vm.oops.MethodInfo;
import org.xyz.jvm.hotspot.src.share.vm.oops.attribute.CodeAttribute;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaThread;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaVFrame;
import org.xyz.jvm.hotspot.src.share.vm.runtime.StackValue;
import org.xyz.jvm.hotspot.src.share.vm.runtime.StackValueCollection;
import org.xyz.jvm.hotspot.src.share.vm.utilities.BasicType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 字节码解释器
 * */
@Slf4j
public class BytecodeInterpreter {

    /**
     * 执行字节码指令
     * @param currentThread 当前线程
     * @param method 方法信息
     * */
    public static void run(JavaThread currentThread, MethodInfo method) {
        // 获取字节码指令
        CodeAttribute codeAttribute = (CodeAttribute) method.getAttributes().get(Attribute.JVM_ATTRIBUTE_Code);
        ByteCodeStream code = codeAttribute.getCode();

        while (!code.end()) {
            // 获取操作码，操作码都是一个字节
            int opcode = code.getU1Code();

            switch (opcode) {
                case ByteCodes.LDC: {
                    log.info("执行指令: ldc，该指令功能为: 从运行时常量池中提取数据并压入操作数栈");
                    ldc(currentThread, code);
                    break;
                }
                case ByteCodes.RETURN:  {
                    log.info("执行指令: return，该指令功能为: 从方法中返回void，恢复调用者的栈帧，并且把程序的控制权交回调用者");
                    jReturn(currentThread);
                    break;
                }
                case ByteCodes.GETSTATIC: {
                    log.info("执行指令: getstatic，该指令功能为: 获取类的静态字段值并压入操作数栈");
                    getStatic(currentThread, code);
                    break;
                }
                case ByteCodes.INVOKEVIRTUAL: {
                    log.info("执行指令: invokevirtual，该指令功能为: 调用实例方法，依据实例的类型进行分派，这个方法不能使实例初始化方法也不能是类或接口的初始化方法（静态初始化方法）");
                    invokeVirtual(currentThread, code);
                    break;
                }
                case ByteCodes.BIPUSH: {
                    log.info("执行指令: bipush，该指令功能为: 将立即数byte带符号扩展为一个int类型的值，然后压入操作数栈中");
                    bIPush(currentThread, code);
                    break;
                }
                case ByteCodes.SIPUSH: {
                    log.info("执行指令: sipush，该指令功能为: 将无符号立即数byte1和byte2组合成一个short类型整数，然后再带符号扩展为一个int类型的值，然后压入操作数栈中");
                    sIPush(currentThread, code);
                    break;
                }
                case ByteCodes.ISTORE: {
                    log.info("执行指令: istore，该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置");
                    iStore(currentThread, code);
                    break;
                }
                case ByteCodes.ISTORE_0: {
                    log.info("执行指令: istore_0，该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中索引为0的位置");
                    iStore0(currentThread, code);
                    break;
                }
                case ByteCodes.ISTORE_1: {
                    log.info("执行指令: istore_1，该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中索引为1的位置");
                    iStore1(currentThread, code);
                    break;
                }
                case ByteCodes.ISTORE_2: {
                    log.info("执行指令: istore_2，该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中索引为2的位置");
                    iStore2(currentThread, code);
                    break;
                }
                case ByteCodes.ISTORE_3: {
                    log.info("执行指令: istore_3，该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中索引为3的位置");
                    iStore3(currentThread, code);
                    break;
                }
                case ByteCodes.ILOAD: {
                    log.info("执行指令: iload，该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（int类型）压入操作数栈中");
                    iLoad(currentThread, code);
                    break;
                }
                case ByteCodes.ILOAD_0: {
                    log.info("执行指令: iload_0，该指令功能为: 将局部变量表中索引为0的值（int类型）压入操作数栈中");
                    iLoad0(currentThread, code);
                    break;
                }
                case ByteCodes.ILOAD_1: {
                    log.info("执行指令: iload_1，该指令功能为: 将局部变量表中索引为1的值（int类型）压入操作数栈中");
                    iLoad1(currentThread, code);
                    break;
                }
                case ByteCodes.ILOAD_2: {
                    log.info("执行指令: iload_2，该指令功能为: 将局部变量表中索引为2的值（int类型）压入操作数栈中");
                    iLoad2(currentThread, code);
                    break;
                }
                case ByteCodes.ILOAD_3: {
                    log.info("执行指令: iload_3，该指令功能为: 将局部变量表中索引为3的值（int类型）压入操作数栈中");
                    iLoad3(currentThread, code);
                    break;
                }
                case ByteCodes.ICONST_M1: {
                    log.info("执行指令: iconst_m1，该指令功能为: 将int类型的常量-1压入操作数栈中");
                    iConstM1(currentThread, code);
                    break;
                }
                case ByteCodes.ICONST_0: {
                    log.info("执行指令: iconst_0，该指令功能为: 将int类型的常量0压入操作数栈中");
                    iConst0(currentThread, code);
                    break;
                }
                case ByteCodes.ICONST_1: {
                    log.info("执行指令: iconst_1，该指令功能为: 将int类型的常量1压入操作数栈中");
                    iConst1(currentThread, code);
                    break;
                }
                case ByteCodes.ICONST_2: {
                    log.info("执行指令: iconst_2，该指令功能为: 将int类型的常量2压入操作数栈中");
                    iConst2(currentThread, code);
                    break;
                }
                case ByteCodes.ICONST_3: {
                    log.info("执行指令: iconst_3，该指令功能为: 将int类型的常量3压入操作数栈中");
                    iConst3(currentThread, code);
                    break;
                }
                case ByteCodes.ICONST_4: {
                    log.info("执行指令: iconst_4，该指令功能为: 将int类型的常量4压入操作数栈中");
                    iConst4(currentThread, code);
                    break;
                }
                case ByteCodes.ICONST_5: {
                    log.info("执行指令: iconst_5，该指令功能为: 将int类型的常量5压入操作数栈中");
                    iConst5(currentThread, code);
                    break;
                }
                case ByteCodes.FCONST_0: {
                    log.info("执行指令: fconst_0，该指令功能为: 将float类型的常量0压入操作数栈中");
                    fConst0(currentThread, code);
                    break;
                }
                case ByteCodes.FCONST_1: {
                    log.info("执行指令: fconst_1，该指令功能为: 将float类型的常量1压入操作数栈中");
                    fConst1(currentThread, code);
                    break;
                }
                case ByteCodes.FCONST_2: {
                    log.info("执行指令: fconst_2，该指令功能为: 将float类型的常量2压入操作数栈中");
                    fConst2(currentThread, code);
                    break;
                }
                case ByteCodes.FLOAD: {
                    log.info("执行指令: fload，该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（float类型）压入操作数栈中");
                    fLoad(currentThread, code);
                    break;
                }
                case ByteCodes.FLOAD_0: {
                    log.info("执行指令: fload_0，该指令功能为: 将局部变量表中索引为0的值（float类型）压入操作数栈中");
                    fLoad0(currentThread, code);
                    break;
                }
                case ByteCodes.FLOAD_1: {
                    log.info("执行指令: fload_1，该指令功能为: 将局部变量表中索引为1的值（float类型）压入操作数栈中");
                    fLoad1(currentThread, code);
                    break;
                }
                case ByteCodes.FLOAD_2: {
                    log.info("执行指令: fload_2，该指令功能为: 将局部变量表中索引为2的值（float类型）压入操作数栈中");
                    fLoad2(currentThread, code);
                    break;
                }
                case ByteCodes.FLOAD_3: {
                    log.info("执行指令: fload_3，该指令功能为: 将局部变量表中索引为3的值（float类型）压入操作数栈中");
                    fLoad3(currentThread, code);
                    break;
                }
                case ByteCodes.FSTORE: {
                    log.info("执行指令: fstore，该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置");
                    fStore(currentThread, code);
                    break;
                }
                case ByteCodes.FSTORE_0: {
                    log.info("执行指令: fstore_0，该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中索引为0的位置");
                    fStore0(currentThread, code);
                    break;
                }
                case ByteCodes.FSTORE_1: {
                    log.info("执行指令: fstore_1，该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中索引为1的位置");
                    fStore1(currentThread, code);
                    break;
                }
                case ByteCodes.FSTORE_2: {
                    log.info("执行指令: fstore_2，该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中索引为2的位置");
                    fStore2(currentThread, code);
                    break;
                }
                case ByteCodes.FSTORE_3: {
                    log.info("执行指令: fstore_3，该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中索引为3的位置");
                    fStore3(currentThread, code);
                    break;
                }
                case ByteCodes.LDC2_W: {
                    log.info("执行指令: ldc2_w，该指令功能为: 从运行时常量池中提取long或double数据并压入操作数栈（宽索引）中");
                    ldc2W(currentThread, code);
                    break;
                }
                case ByteCodes.LLOAD: {
                    log.info("执行指令: lload，该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（long类型）压入操作数栈中");
                    lLoad(currentThread, code);
                    break;
                }
                case ByteCodes.LLOAD_0: {
                    log.info("执行指令: lload_0，该指令功能为: 将局部变量表中索引为0的值（long类型）压入操作数栈中");
                    lLoad0(currentThread, code);
                    break;
                }
                case ByteCodes.LLOAD_1: {
                    log.info("执行指令: lload_1，该指令功能为: 将局部变量表中索引为1的值（long类型）压入操作数栈中");
                    lLoad1(currentThread, code);
                    break;
                }
                case ByteCodes.LLOAD_2: {
                    log.info("执行指令: lload_2，该指令功能为: 将局部变量表中索引为2的值（long类型）压入操作数栈中");
                    lLoad2(currentThread, code);
                    break;
                }
                case ByteCodes.LLOAD_3: {
                    log.info("执行指令: lload_3，该指令功能为: 将局部变量表中索引为3的值（long类型）压入操作数栈中");
                    lLoad3(currentThread, code);
                    break;
                }
                case ByteCodes.LSTORE: {
                    log.info("执行指令: lstore，该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置");
                    lStore(currentThread, code);
                    break;
                }
                case ByteCodes.LSTORE_0: {
                    log.info("执行指令: lstore_0，该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中索引为0的位置");
                    lStore0(currentThread, code);
                    break;
                }
                case ByteCodes.LSTORE_1: {
                    log.info("执行指令: lstore_1，该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中索引为1的位置");
                    lStore1(currentThread, code);
                    break;
                }
                case ByteCodes.LSTORE_2: {
                    log.info("执行指令: lstore_2，该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中索引为2的位置");
                    lStore2(currentThread, code);
                    break;
                }
                case ByteCodes.LSTORE_3: {
                    log.info("执行指令: lstore_3，该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中索引为3的位置");
                    lStore3(currentThread, code);
                    break;
                }
                case ByteCodes.LCONST_0: {
                    log.info("执行指令: lconst_0，该指令功能为: 将long类型的常量0压入操作数栈中");
                    lConst0(currentThread, code);
                    break;
                }
                case ByteCodes.LCONST_1: {
                    log.info("执行指令: lconst_1，该指令功能为: 将long类型的常量1压入操作数栈中");
                    lConst1(currentThread, code);
                    break;
                }
                case ByteCodes.DCONST_0: {
                    log.info("执行指令: dconst_0，该指令功能为: 将double类型的常量0压入操作数栈中");
                    dConst0(currentThread, code);
                    break;
                }
                case ByteCodes.DCONST_1: {
                    log.info("执行指令: dconst_1，该指令功能为: 将double类型的常量1压入操作数栈中");
                    dConst1(currentThread, code);
                    break;
                }
                case ByteCodes.DLOAD: {
                    log.info("执行指令: dload，该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（double类型）压入操作数栈中");
                    dLoad(currentThread, code);
                    break;
                }
                case ByteCodes.DLOAD_0: {
                    log.info("执行指令: dload_0，该指令功能为: 将局部变量表中索引为0、1所组合成的值（double类型）压入操作数栈中");
                    dLoad0(currentThread, code);
                    break;
                }
                case ByteCodes.DLOAD_1: {
                    log.info("执行指令: dload_1，该指令功能为: 将局部变量表中索引为1、2所组合成的值（double类型）压入操作数栈中");
                    dLoad1(currentThread, code);
                    break;
                }
                case ByteCodes.DLOAD_2: {
                    log.info("执行指令: dload_2，该指令功能为: 将局部变量表中索引为2、3所组合成的值（double类型）压入操作数栈中");
                    dLoad2(currentThread, code);
                    break;
                }
                case ByteCodes.DLOAD_3: {
                    log.info("执行指令: dload_3，该指令功能为: 将局部变量表中索引为3、4所组合成的值（double类型）压入操作数栈中");
                    dLoad3(currentThread, code);
                    break;
                }
                case ByteCodes.DSTORE: {
                    log.info("执行指令: dstore，该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置");
                    dStore(currentThread, code);
                    break;
                }
                case ByteCodes.DSTORE_0: {
                    log.info("执行指令: dstore_0，该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中索引为0、1的位置");
                    dStore0(currentThread, code);
                    break;
                }
                case ByteCodes.DSTORE_1: {
                    log.info("执行指令: dstore_1，该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中索引为1、2的位置");
                    dStore1(currentThread, code);
                    break;
                }
                case ByteCodes.DSTORE_2: {
                    log.info("执行指令: dstore_2，该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中索引为2、3的位置");
                    dStore2(currentThread, code);
                    break;
                }
                case ByteCodes.DSTORE_3: {
                    log.info("执行指令: dstore_3，该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中索引为3、4的位置");
                    dStore3(currentThread, code);
                    break;
                }
                default:
                    throw new Error("暂不支持该指令: " + opcode);
            }
        }
    }

    /**
     * 执行dstore_3字节码指令
     * 该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中索引为3、4的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dStore3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue[] values = stack.popDouble2();
        // 存入局部变量表索引为3、4的位置
        local.set(3, values[0]);
        local.set(4, values[1]);
    }

    /**
     * 执行dstore_2字节码指令
     * 该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中索引为2、3的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dStore2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue[] values = stack.popDouble2();
        // 存入局部变量表索引为2、3的位置
        local.set(2, values[0]);
        local.set(3, values[1]);
    }

    /**
     * 执行dstore_1字节码指令
     * 该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中索引为1、2的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dStore1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue[] values = stack.popDouble2();
        // 存入局部变量表索引为1、2的位置
        local.set(1, values[0]);
        local.set(2, values[1]);
    }

    /**
     * 执行dstore_0字节码指令
     * 该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中索引为0、1的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dStore0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue[] values = stack.popDouble2();
        // 存入局部变量表索引为0、1的位置
        local.set(0, values[0]);
        local.set(1, values[1]);
    }

    /**
     * 执行dstore字节码指令
     * 该指令功能为: 将操作数栈栈顶的double类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从操作数栈中弹出栈顶元素
        StackValue[] values = stack.popDouble2();
        // 存入局部变量表对应索引的位置
        local.set(index, values[0]);
        local.set(++index, values[1]);
    }

    /**
     * 执行dload_3字节码指令
     * 该指令功能为: 将局部变量表中索引为3、4所组合成的值（double类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dLoad3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为3、4所组合成的值（double占两个槽位）
        StackValue valueLow = local.get(3);
        StackValue valueHigh = local.get(4);

        // 压入操作数栈中
        stack.push(valueHigh);
        stack.push(valueLow);
    }

    /**
     * 执行dload_2字节码指令
     * 该指令功能为: 将局部变量表中索引为2、3所组合成的值（double类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dLoad2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为2、3所组合成的值（double占两个槽位）
        StackValue valueLow = local.get(2);
        StackValue valueHigh = local.get(3);

        // 压入操作数栈中
        stack.push(valueHigh);
        stack.push(valueLow);
    }

    /**
     * 执行dload_1字节码指令
     * 该指令功能为: 将局部变量表中索引为1、2所组合成的值（double类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dLoad1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为1、2所组合成的值（double占两个槽位）
        StackValue valueLow = local.get(1);
        StackValue valueHigh = local.get(2);

        // 压入操作数栈中
        stack.push(valueHigh);
        stack.push(valueLow);
    }

    /**
     * 执行dload_0字节码指令
     * 该指令功能为: 将局部变量表中索引为0、1所组合成的值（double类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dLoad0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为0、1所组合成的值（double占两个槽位）
        StackValue valueLow = local.get(0);
        StackValue valueHigh = local.get(1);

        // 压入操作数栈中
        stack.push(valueHigh);
        stack.push(valueLow);
    }

    /**
     * 执行dload字节码指令
     * 该指令功能为: /*将局部变量表中对应索引（操作数中给出）位置的值（double类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dLoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从局部变量表中取出对应索引位置的值
        StackValue valueLow = local.get(index);
        StackValue valueHigh = local.get(++index);

        // 压入操作数栈中
        stack.push(valueHigh);
        stack.push(valueLow);
    }

    /**
     * 执行dconst_0字节码指令
     * 该指令功能为: 将double类型的常量0压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dConst0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将0（double）压入操作数栈中
        stack.pushDouble(0);
    }

    /**
     * 执行dconst_1字节码指令
     * 该指令功能为: 将double类型的常量1压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dConst1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将1（double）压入操作数栈中
        stack.pushDouble(1);
    }

    /**
     * 执行lconst_0字节码指令
     * 该指令功能为: 将long类型的常量0压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lConst0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将0（long）压入操作数栈中
        stack.push(new StackValue(BasicType.T_LONG, 0));
    }

    /**
     * 执行lconst_1字节码指令
     * 该指令功能为: 将long类型的常量1压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lConst1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将1（long）压入操作数栈中
        stack.push(new StackValue(BasicType.T_LONG, 1));
    }

    /**
     * 执行lload_3字节码指令
     * 该指令功能为: 将局部变量表中索引为3的值（long类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lLoad3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中获取索引为3的值
        StackValue value = local.get(3);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行lload_2字节码指令
     * 该指令功能为: 将局部变量表中索引为2的值（long类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lLoad2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为2的值
        StackValue value = local.get(2);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行lload_1字节码指令
     * 该指令功能为: 将局部变量表中索引为1的值（long类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lLoad1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为1的值
        StackValue value = local.get(1);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行lload_0字节码指令
     * 该指令功能为: 将局部变量表中索引为0的值（long类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lLoad0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为0的值
        StackValue value = local.get(0);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行lload字节码指令
     * 该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（long类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lLoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从局部变量表中取出对应索引位置的值
        StackValue value = local.get(index);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行lstore_3字节码指令
     * 该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中索引为0的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lStore3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为3的位置
        local.set(3, value);
    }

    /**
     * 执行lstore_2字节码指令
     * 该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中索引为2的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lStore2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为2的位置
        local.set(2, value);
    }

    /**
     * 执行lstore_1字节码指令
     * 该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中索引为1的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lStore1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为1的位置
        local.set(1, value);
    }

    /**
     * 执行lstore_0字节码指令
     * 该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中索引为0的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lStore0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为0的位置
        local.set(0, value);
    }

    /**
     * 执行lstore字节码指令
     * 该指令功能为: 将操作数栈栈顶的long类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从操作数栈中弹出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表对应索引的位置
        local.set(index, value);
    }

    /**
     * 执行ldc_w字节码指令
     * 该指令功能为: 从运行时常量池中提取int类型或float类型的运行时常量、字符串字面量，或者一个指向类、方法类型或方法句柄的符号引用 的数据并压入操作数栈（宽索引）中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ldcW(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型整数，合并之后的值就是 运行时常量池中的索引号，占两个字节
        int operand = code.getUnsignedShort();

        int tag = constantPool.getTag(operand);

        switch (tag) {
            case ConstantPool.JVM_CONSTANT_Integer: {
                long i = constantPool.getInteger(operand);
                stack.push(new StackValue(BasicType.T_INT, i));
                break;
            }
            case ConstantPool.JVM_CONSTANT_Float: {
                double f = constantPool.getFloat(operand);
                stack.push(new StackValue(BasicType.T_FLOAT, f));
                break;
            }
            case ConstantPool.JVM_CONSTANT_String: {
                String s = constantPool.getString(operand);
                stack.push(new StackValue(BasicType.T_OBJECT, s));
            }
            case ConstantPool.JVM_CONSTANT_Class: {
                String className = constantPool.getClassName(operand);
                // JVM加载器加载的类（java开头），通过反射
                if (className.startsWith("java")) {
                    try {
                        Class<?> clazz = Class.forName(className.replace('/', '.'));
                        // TODO: 如何找到一个对象
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { // TODO: 从自己的类加载器的缓存中获取Klass信息

                }

                break;
            }
            default:
                throw new Error("无法识别的格式: " + tag);
        }
    }

    /**
     * 执行ldc2_w字节码指令
     * 该指令功能为: 从运行时常量池中提取long或double数据并压入操作数栈（宽索引）中
     * 自己实现的JVM因为在解析常量池中将long或double类型的值合并了，所以读一次就可以了，Hotspot中解析常量池的时候没合，而是分两个槽来存储，所以会读两次，push两次
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ldc2W(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型整数，合并之后的值就是 运行时常量池中的索引号，占两个字节
        int operand = code.getUnsignedShort();

        int tag = constantPool.getTag(operand);

        /**
         * 数值入栈，这边实现方式略有差别
         *      long是用8字节的byte数组存储的（直接入栈）
         *      double是用两个slot存储的（分两次入栈）
         * */
        switch (tag) {
            case ConstantPool.JVM_CONSTANT_Long: {
                long l = constantPool.getLong(operand);
                stack.push(new StackValue(BasicType.T_LONG, l));
                break;
            }
            case ConstantPool.JVM_CONSTANT_Double: {
                double d = constantPool.getDouble(operand);
                stack.pushDouble(d);
                break;
            }
            default:
                throw new Error("无法识别的格式: " + tag);
        }
    }

    /**
     * 执行fstore_0字节码指令
     * 该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中索引为0的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fStore0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为0的位置
        local.set(0, value);
    }

    /**
     * 执行fstore_1字节码指令
     * 该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中索引为1的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fStore1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为1的位置
        local.set(1, value);
    }

    /**
     * 执行fstore_2字节码指令
     * 该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中索引为2的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fStore2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为2的位置
        local.set(2, value);
    }

    /**
     * 执行fstore_3字节码指令
     * 该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中索引为3的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fStore3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为3的位置
        local.set(3, value);
    }

    /**
     * 执行fstore字节码指令
     * 该指令功能为: 将操作数栈栈顶的float类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从操作数栈中弹出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表对应索引的位置
        local.set(index, value);
    }

    /**
     * 执行fload_0字节码指令
     * 该指令功能为: 将局部变量表中索引为0的值（float类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fLoad0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为0的值
        StackValue value = local.get(0);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行fload_1字节码指令
     * 该指令功能为: 将局部变量表中索引为1的值（float类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fLoad1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为0的值
        StackValue value = local.get(1);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行fload_2字节码指令
     * 该指令功能为: 将局部变量表中索引为2的值（float类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fLoad2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为0的值
        StackValue value = local.get(2);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行fload_3字节码指令
     * 该指令功能为: 将局部变量表中索引为3的值（float类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fLoad3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为0的值
        StackValue value = local.get(3);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行fload字节码指令
     * 该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（float类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fLoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从局部变量表中取出对应索引位置的值
        StackValue value = local.get(index);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行fconst_0字节码指令
     * 该指令功能为: 将float类型的常量0压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fConst0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将0（float）压入操作数栈中
        stack.push(new StackValue(BasicType.T_FLOAT, 0f));
    }

    /**
     * 执行fconst_1字节码指令
     * 该指令功能为: 将float类型的常量1压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fConst1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将1（float）压入操作数栈中
        stack.push(new StackValue(BasicType.T_FLOAT, 1f));
    }

    /**
     * 执行fconst_2字节码指令
     * 该指令功能为: 将float类型的常量2压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fConst2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将2（float）压入操作数栈中
        stack.push(new StackValue(BasicType.T_FLOAT, 2f));
    }

    /**
     * 执行iconst_0字节码指令
     * 该指令功能为: 将int类型的常量0压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iConst0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将0（int）压入操作数栈中
        stack.push(new StackValue(BasicType.T_INT, 0));
    }

    /**
     * 执行iconst_1字节码指令
     * 该指令功能为: 将int类型的常量1压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iConst1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将1（int）压入操作数栈中
        stack.push(new StackValue(BasicType.T_INT, 1));
    }

    /**
     * 执行iconst_2字节码指令
     * 该指令功能为: 将int类型的常量2压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iConst2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将2（int）压入操作数栈中
        stack.push(new StackValue(BasicType.T_INT, 2));
    }

    /**
     * 执行iconst_3字节码指令
     * 该指令功能为: 将int类型的常量3压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iConst3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将3（int）压入操作数栈中
        stack.push(new StackValue(BasicType.T_INT, 3));
    }

    /**
     * 执行iconst_4字节码指令
     * 该指令功能为: 将int类型的常量4压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iConst4(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将4（int）压入操作数栈中
        stack.push(new StackValue(BasicType.T_INT, 4));
    }

    /**
     * 执行iconst_5字节码指令
     * 该指令功能为: 将int类型的常量5压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iConst5(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将5（int）压入操作数栈中
        stack.push(new StackValue(BasicType.T_INT, 5));
    }

    /**
     * 执行iconst_m1字节码指令
     * 该指令功能为: 将int类型的常量-1压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iConstM1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 将-1（int）压入操作数栈中
        stack.push(new StackValue(BasicType.T_INT, -1));
    }

    /**
     * 执行iload字节码指令
     * 该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（int类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iLoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从局部变量表中取出对应索引位置的值
        StackValue value = local.get(index);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行iload_0字节码指令
     * 该指令功能为: 将局部变量表中索引为0的值（int类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iLoad0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为0的值
        StackValue value = local.get(0);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行iload_1字节码指令
     * 该指令功能为: 将局部变量表中索引为1的值（int类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iLoad1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为1的值
        StackValue value = local.get(1);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行iload_2字节码指令
     * 该指令功能为: 将局部变量表中索引为2的值（int类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iLoad2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为2的值
        StackValue value = local.get(2);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行iload_3字节码指令
     * 该指令功能为: 将局部变量表中索引为3的值（int类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iLoad3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为3的值
        StackValue value = local.get(3);
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行istore字节码指令
     * 该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从操作数栈中弹出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表对应索引的位置
        local.set(index, value);
    }

    /**
     * 执行istore_0字节码指令
     * 该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中索引为0的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iStore0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为0的位置
        local.set(0, value);
    }

    /**
     * 执行istore_1字节码指令
     * 该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中索引为1的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iStore1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为1的位置
        local.set(1, value);
    }

    /**
     * 执行istore_2字节码指令
     * 该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中索引为2的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iStore2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为2的位置
        local.set(2, value);
    }

    /**
     * 执行istore_3字节码指令
     * 该指令功能为: 将操作数栈栈顶的int类型的元素存入局部变量表中索引为3的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iStore3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 取出栈顶元素
        StackValue value = stack.pop();
        // 存入局部变量表索引为3的位置
        local.set(3, value);
    }

    /**
     * 执行bipush字节码指令
     * 该指令功能为: 将立即数byte带符号扩展为一个int类型的值，然后压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void bIPush(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，byte类型的立即数，占一个字节，然后直接转成int类型
        int value = code.getU1Code();

        // 压入操作数栈中，注意类型为int，因为该字节码指令是要求将byte转成int类型入栈的
        stack.push(new StackValue(BasicType.T_INT, value));
    }

    /**
     * 执行sipush字节码指令
     * 该指令功能为: 将无符号立即数byte1和byte2组合成一个short类型整数，然后再带符号扩展为一个int类型的值，然后压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void sIPush(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型的立即数，占两个字节，直接读取两个字节并组合成short类型，然后直接转成int类型
        int value = code.getUnsignedShort();

        // 压入操作数栈中，注意类型为int，因为该字节码指令是要求将short转成int类型入栈的
        stack.push(new StackValue(BasicType.T_INT, value));
    }

    /**
     * 如何找到一个类
     * 1.系统类（java开头）通过反射找到对应的类（反射原理就是从类加载器的缓存空间中找到对应类的klass模型然后通过klass模型再找到Class对象）
     * 2.自己加载的类通过在类加载器的缓存空间中找对应的klass（类对应的klass模型会存储在加载它的那个类加载器的缓存空间中，所以只需要在类加载器的缓存空间中通过类的全限定名找到对应的klass模型即可）
     *
     * 执行invokevirtual字节码指令
     * 该指令功能为: 调用实例方法，依据实例的类型进行分派，这个方法不能使实例初始化方法也不能是类或接口的初始化方法（静态初始化方法）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void invokeVirtual(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 取出操作数，INVOKEVIRTUAL指令的操作数是常量池的索引（Methodref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand);
        String methodName = constantPool.getMethodName(operand);
        String descriptorName = constantPool.getFiledDescriptor(operand);

        // 系统加载的类走反射
        if (className.startsWith("java")) {
            // 解析方法描述符
            DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
            descriptorStream.parseMethod();
            // 获取方法所有形参类型的Class对象按照形参顺序组成的数组
            Class<?>[] paramsClass = descriptorStream.getParamsType();

            // 从操作数栈中根据形类型 顺序 获取实参，即从操作数栈中弹出实参
            Object[] params = descriptorStream.getParamsVal(frame);

            // 从操作数栈中弹出 被调方法所属类的对象（GETSTATIC压入栈中的），即this指针
            Object obj = frame.getOperandStack().pop().getObject();

            try {
                Method fun = obj.getClass().getMethod(methodName, paramsClass);

                /**
                 * 处理：
                 *  1.无返回值
                 *  2.有返回值，需要将返回值压入操作数中
                 */
                if (BasicType.T_VOID == descriptorStream.getReturnElement().getType()) {
                    fun.invoke(obj, params);
                } else {
                    descriptorStream.pushField(fun.invoke(obj, params), frame);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {    // TODO: 自己加载的类自己处理

        }
    }

    /**
     * 执行getstatic字节码指令
     * 该指令功能为: 获取类的静态字段值并压入操作数栈
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void getStatic(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();

        // 取出操作数，GETSTATIC指令的操作数是常量池的索引（Fieldref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand);
        String fieldName = constantPool.getFiledName(operand);

        // 系统加载的类走反射
        if (className.startsWith("java")) {
            try {
                Class<?> clazz = Class.forName(className.replace('/', '.'));
                Field field = clazz.getField(fieldName);

                // 如果字段不是静态字段的话，Field.get(Object)要传入反射类的对象（实例属性和对象绑定）。如果传null会报: java.lang.NullPointerException
                // 如果字段是静态字段的话，Field.get(Object)传入任何对象都是可以的（类属性和类绑定），包括null
                stack.push(new StackValue(BasicType.T_OBJECT, field.get(null)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {    // TODO: 自己加载的类自己处理

        }
    }

    /**
     * 执行return字节码指令
     * 该指令功能为: 从方法中返回void，恢复调用者的栈帧，并且把程序的控制权交回调用者
     * @param currentThread 当前线程
     * */
    private static void jReturn(JavaThread currentThread) {
        // pop出栈帧
        currentThread.getStack().pop();
        log.info("\t 剩余栈帧数量: " + currentThread.getStack().size());
    }

    /**
     * 执行ldc字节码指令
     * 该指令功能为: 从运行时常量池中提取数据并压入操作数栈
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ldc(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();

        // 取出操作数，LDC指令的操作数是常量池的索引，一个字节
        int operand = code.getU1Code();

        // 从常量池中取出操作数的类型tag，即常量池项的类型tag
        int tag = constantPool.getTag(operand);

        switch (tag) {
            case ConstantPool.JVM_CONSTANT_Integer: {
                int content = constantPool.getInteger(operand);
                stack.push(new StackValue(BasicType.T_INT, content));
                break;
            }
            case ConstantPool.JVM_CONSTANT_Float: {
                float content = constantPool.getFloat(operand);
                stack.push(new StackValue(BasicType.T_FLOAT, content));
                break;
            }
            case ConstantPool.JVM_CONSTANT_String: {
                String content = constantPool.getString(operand);
                stack.push(new StackValue(BasicType.T_OBJECT, content));
                break;
            }
            case ConstantPool.JVM_CONSTANT_Class: {
                String className = constantPool.getClassName(operand);
                // JVM加载器加载的类（java开头），通过反射
                if (className.startsWith("java")) {
                    try {
                        Class<?> clazz = Class.forName(className.replace('/', '.'));
                        // TODO: 如何找到一个对象
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { // TODO: 从自己的类加载器的缓存中获取Klass信息

                }

                break;
            }
            default:
                throw new Error("无法识别的常量池类型: " + tag);
        }

    }
}
