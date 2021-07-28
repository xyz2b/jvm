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
                case ByteCodes.NOP: {
                    log.info("执行指令: nop，该指令功能: 什么也不做");
                    break;
                }
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
                case ByteCodes.LDC_W: {
                    log.info("执行指令: ldc_w，该指令功能为: 从运行时常量池中提取int类型或float类型的运行时常量、字符串字面量，或者一个指向类、方法类型或方法句柄的符号引用 的数据并压入操作数栈（宽索引）中");
                    ldcW(currentThread, code);
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
                case ByteCodes.I2L: {
                    log.info("执行指令: i2l，该指令功能为: 将栈顶int类型数值强制转换成long类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    i2l(currentThread, code);
                    break;
                }
                case ByteCodes.I2F: {
                    log.info("执行指令: i2f，该指令功能为: 将栈顶int类型数值强制转换成float类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    i2f(currentThread, code);
                    break;
                }
                case ByteCodes.I2D: {
                    log.info("执行指令: i2d，该指令功能为: 将栈顶int类型数值强制转换成double类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    i2d(currentThread, code);
                    break;
                }
                case ByteCodes.I2B: {
                    log.info("执行指令: i2b，该指令功能为: 将栈顶int类型数值强制转换成byte类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    i2b(currentThread, code);
                    break;
                }
                case ByteCodes.I2C: {
                    log.info("执行指令: i2c，该指令功能为: 将栈顶int类型数值强制转换成char类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    i2c(currentThread, code);
                    break;
                }
                case ByteCodes.I2S: {
                    log.info("执行指令: i2s，该指令功能为: 将栈顶int类型数值强制转换成short类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    i2s(currentThread, code);
                    break;
                }
                case ByteCodes.L2I: {
                    log.info("执行指令: l2i，该指令功能为: 将栈顶long类型数值强制转换成int类型数值并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    l2i(currentThread, code);
                    break;
                }
                case ByteCodes.L2F: {
                    log.info("执行指令: l2f，该指令功能为: 将栈顶long类型数值强制转换成float类型数值并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    l2f(currentThread, code);
                    break;
                }
                case ByteCodes.L2D: {
                    log.info("执行指令: l2d，该指令功能为: 将栈顶long类型数值强制转换成double类型数值并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    l2d(currentThread, code);
                    break;
                }
                case ByteCodes.F2I: {
                    log.info("执行指令: f2i，该指令功能为: 将栈顶float类型数值强制转换成int类型数值并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    f2i(currentThread, code);
                    break;
                }
                case ByteCodes.F2L: {
                    log.info("执行指令: f2l，该指令功能为: 将栈顶float类型数值强制转换成long类型数值并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    f2l(currentThread, code);
                    break;
                }
                case ByteCodes.F2D: {
                    log.info("执行指令: f2d，该指令功能为: 将栈顶float类型数值强制转换成double类型数值并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    f2d(currentThread, code);
                    break;
                }
                case ByteCodes.D2I: {
                    log.info("执行指令: d2i，该指令功能为: 将栈顶double类型数值强制转换成int类型数值并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    d2i(currentThread, code);
                    break;
                }
                case ByteCodes.D2L: {
                    log.info("执行指令: d2l，该指令功能为: 将栈顶double类型数值强制转换成long类型数值并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    d2l(currentThread, code);
                    break;
                }
                case ByteCodes.D2F: {
                    log.info("执行指令: d2f，该指令功能为: 将栈顶double类型数值强制转换成float类型数值并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    d2f(currentThread, code);
                    break;
                }
                case ByteCodes.IADD: {
                    log.info("执行指令: iadd，该指令功能为: 将栈顶两个int类型数值相加并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iAdd(currentThread, code);
                    break;
                }
                case ByteCodes.LADD: {
                    log.info("执行指令: ladd，该指令功能为: 将栈顶两个long类型数值相加并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lAdd(currentThread, code);
                    break;
                }
                case ByteCodes.FADD: {
                    log.info("执行指令: fadd，该指令功能为: 将栈顶两个float类型数值相加并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    fAdd(currentThread, code);
                    break;
                }
                case ByteCodes.DADD: {
                    log.info("执行指令: dadd，该指令功能为: 将栈顶两个double类型数值相加并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    dAdd(currentThread, code);
                    break;
                }
                case ByteCodes.ISUB: {
                    log.info("执行指令: isub，该指令功能为: 将栈顶两个int类型数值相减并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iSub(currentThread, code);
                    break;
                }
                case ByteCodes.LSUB: {
                    log.info("执行指令: lsub，该指令功能为: 将栈顶两个long类型数值相减并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lSub(currentThread, code);
                    break;
                }
                case ByteCodes.FSUB: {
                    log.info("执行指令: fsub，该指令功能为: 将栈顶两个float类型数值相减并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    fSub(currentThread, code);
                    break;
                }
                case ByteCodes.DSUB: {
                    log.info("执行指令: dsub，该指令功能为: 将栈顶两个double类型数值相减并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    dSub(currentThread, code);
                    break;
                }
                case ByteCodes.IMUL: {
                    log.info("执行指令: imul，该指令功能为: 将栈顶两个int类型数值相乘并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iMul(currentThread, code);
                    break;
                }
                case ByteCodes.LMUL: {
                    log.info("执行指令: lmul，该指令功能为: 将栈顶两个long类型数值相乘并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lMul(currentThread, code);
                    break;
                }
                case ByteCodes.FMUL: {
                    log.info("执行指令: fmul，该指令功能为: 将栈顶两个float类型数值相乘并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    fMul(currentThread, code);
                    break;
                }
                case ByteCodes.DMUL: {
                    log.info("执行指令: dmul，该指令功能为: 将栈顶两个double类型数值相乘并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    dMul(currentThread, code);
                    break;
                }
                case ByteCodes.IDIV: {
                    log.info("执行指令: idiv，该指令功能为: 将栈顶两个int类型数值相除并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iDiv(currentThread, code);
                    break;
                }
                case ByteCodes.LDIV: {
                    log.info("执行指令: ldiv，该指令功能为: 将栈顶两个long类型数值相除并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lDiv(currentThread, code);
                    break;
                }
                case ByteCodes.FDIV: {
                    log.info("执行指令: fdiv，该指令功能为: 将栈顶两个float类型数值相除并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    fDiv(currentThread, code);
                    break;
                }
                case ByteCodes.DDIV: {
                    log.info("执行指令: ddiv，该指令功能为: 将栈顶两个double类型数值相除并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    dDiv(currentThread, code);
                    break;
                }
                case ByteCodes.IREM: {
                    log.info("执行指令: irem，该指令功能为: 将栈顶两个int类型数值取模并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iRem(currentThread, code);
                    break;
                }
                case ByteCodes.LREM: {
                    log.info("执行指令: lrem，该指令功能为: 将栈顶两个long类型数值取模并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lRem(currentThread, code);
                    break;
                }
                case ByteCodes.FREM: {
                    log.info("执行指令: frem，该指令功能为: 将栈顶两个float类型数值取模并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    fRem(currentThread, code);
                    break;
                }
                case ByteCodes.DREM: {
                    log.info("执行指令: drem，该指令功能为: 将栈顶两个double类型数值取模并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    dRem(currentThread, code);
                    break;
                }
                case ByteCodes.INEG: {
                    log.info("执行指令: ineg，该指令功能为: 将栈顶int类型数值取负并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iNeg(currentThread, code);
                    break;
                }
                case ByteCodes.LNEG: {
                    log.info("执行指令: lneg，该指令功能为: 将栈顶long类型数值取负并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lNeg(currentThread, code);
                    break;
                }
                case ByteCodes.FNEG: {
                    log.info("执行指令: fneg，该指令功能为: 将栈顶float类型数值取负并将结果压入栈顶（需要将float类型先从栈中弹出）");
                    fNeg(currentThread, code);
                    break;
                }
                case ByteCodes.DNEG: {
                    log.info("执行指令: dneg，该指令功能为: 将栈顶double类型数值取负并将结果压入栈顶（需要将double类型先从栈中弹出）");
                    dNeg(currentThread, code);
                    break;
                }
                case ByteCodes.ISHL: {
                    log.info("执行指令: ishl，该指令功能为: 将栈顶int类型数值左移指定位数并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iShl(currentThread, code);
                    break;
                }
                case ByteCodes.LSHL: {
                    log.info("执行指令: lshl，该指令功能为: 将栈顶long类型数值左移指定位数并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lShl(currentThread, code);
                    break;
                }
                case ByteCodes.ISHR: {
                    log.info("执行指令: ishr，该指令功能为: 将栈顶int类型数值（有符号）右移指定位数并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iShr(currentThread, code);
                    break;
                }
                case ByteCodes.LSHR: {
                    log.info("执行指令: lshr，该指令功能为: 将栈顶long类型数值（有符号）右移指定位数并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lShr(currentThread, code);
                    break;
                }
                case ByteCodes.IUSHR: {
                    log.info("执行指令: iushr，该指令功能为: 将栈顶int类型数值（无符号）右移指定位数并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iUShr(currentThread, code);
                    break;
                }
                case ByteCodes.LUSHR: {
                    log.info("执行指令: lushr，该指令功能为: 将栈顶long类型数值（无符号）右移指定位数并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lUShr(currentThread, code);
                    break;
                }
                case ByteCodes.IAND: {
                    log.info("执行指令: iand，该指令功能为: 将栈顶两个int类型数值按位与并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iAnd(currentThread, code);
                    break;
                }
                case ByteCodes.LAND: {
                    log.info("执行指令: land，该指令功能为: 将栈顶两个long类型数值按位与并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lAnd(currentThread, code);
                    break;
                }
                case ByteCodes.IOR: {
                    log.info("执行指令: ior，该指令功能为: 将栈顶两个int类型数值按位或并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iOr(currentThread, code);
                    break;
                }
                case ByteCodes.LOR: {
                    log.info("执行指令: lor，该指令功能为: 将栈顶两个long类型数值按位或并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lOr(currentThread, code);
                    break;
                }
                case ByteCodes.IXOR: {
                    log.info("执行指令: ixor，该指令功能为: 将栈顶两个int类型数值按位异或并将结果压入栈顶（需要将int类型先从栈中弹出）");
                    iXor(currentThread, code);
                    break;
                }
                case ByteCodes.LXOR: {
                    log.info("执行指令: lxor，该指令功能为: 将栈顶两个long类型数值按位异或并将结果压入栈顶（需要将long类型先从栈中弹出）");
                    lXor(currentThread, code);
                    break;
                }
                case ByteCodes.IINC: {
                    log.info("执行指令: iinc，该指令功能为: 将局部变量表中指定的int类型变量增加指定值（i++、i--、i+=2）（操作数1: 局部变量表中的索引，操作数2: 增加的常量值）");
                    iInc(currentThread, code);
                    break;
                }
                case ByteCodes.DUP: {
                    log.info("执行指令: dup，该指令功能为: 复制操作数栈顶的值，并插入到栈顶（不弹出栈顶的值）");
                    dup(currentThread, code);
                    break;
                }
                case ByteCodes.DUP_X1: {
                    log.info("执行指令: dup_x1，该指令功能为: 复制操作数栈顶的值，并插入到栈顶两个元素之后（不弹出栈顶的值）");
                    dupX1(currentThread, code);
                    break;
                }
                case ByteCodes.DUP_X2: {
                    log.info("执行指令: dup_x2，该指令功能为: 复制操作数栈顶的值，并插入到栈顶两个或三个元素之后（不弹出栈顶的值）");
                    dupX2(currentThread, code);
                    break;
                }
                case ByteCodes.DUP2: {
                    log.info("执行指令: dup2，该指令功能为: 复制操作数栈顶1个或2个值，并插入到栈顶（不弹出栈顶的值）");
                    dup2(currentThread, code);
                    break;
                }
                case ByteCodes.DUP2_X1: {
                    log.info("执行指令: dup2_x1，该指令功能为: 复制操作数栈顶1个或2个值，并插入栈顶以下2个或3个值之后（不弹出栈顶的值）");
                    dup2X1(currentThread, code);
                    break;
                }
                case ByteCodes.DUP2_X2: {
                    log.info("执行指令: dup2_x2，该指令功能为: 复制操作数栈顶1个或2个值，并插入栈顶以下2个、3个或4个值之后（不弹出栈顶的值）");
                    dup2X2(currentThread, code);
                    break;
                }
                case ByteCodes.SWAP: {
                    log.info("执行指令: swap，该指令功能为: 交换操作数栈顶的两个值");
                    jSwap(currentThread, code);
                    break;
                }
                case ByteCodes.POP: {
                    log.info("执行指令: pop，该指令功能为: 将栈顶元素出栈");
                    jPop(currentThread, code);
                    break;
                }
                case ByteCodes.POP2: {
                    log.info("执行指令: pop2，该指令功能为: 将栈顶一个或两个元素出栈");
                    jPop2(currentThread, code);
                    break;
                }
                default:
                    throw new Error("暂不支持该指令: " + opcode);
            }
        }
    }

    /**
     * 执行pop2字节码指令
     * 该指令功能为: 将栈顶一个或两个元素出栈
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void jPop2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.peek();

        // double、long的处理方式和其他数据类型不同
        if (value.getType() == BasicType.T_DOUBLE) {    // double类型，在操作数栈中占两个槽位，double需要取两次，value为double类型
            stack.popDouble();
        } else if (value.getType() == BasicType.T_LONG) {   // long只需要取一次，value为long类型
            stack.pop();
        } else {        // value1 为其他类型，value2 也为其他类型
            stack.pop();

            StackValue value2 = stack.peek();
            // 检查操作数类型
            if (value2.getType() == BasicType.T_DOUBLE || value2.getType() == BasicType.T_LONG) {
                log.error("dup2字节码指令: value2 不匹配的数据类型" + value2.getType());
                throw new Error("dup2字节码指令: value2 不匹配的数据类型" + value2.getType());
            }
            stack.pop();
        }
    }

    /**
     * 执行pop字节码指令
     * 该指令功能为: 将栈顶元素出栈
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void jPop(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶两个元素
        StackValue value = stack.peek();
        if (value.getType() == BasicType.T_DOUBLE || value.getType() == BasicType.T_LONG) {
            log.error("swap字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("swap字节码指令: value 不匹配的数据类型" + value.getType());
        }
        stack.pop();
    }

    /**
     * 执行swap字节码指令
     * 该指令功能为: 交换操作数栈顶的两个值
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void jSwap(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶两个元素
        StackValue value1 = stack.peek();
        if (value1.getType() == BasicType.T_DOUBLE || value1.getType() == BasicType.T_LONG) {
            log.error("swap字节码指令: value1 不匹配的数据类型" + value1.getType());
            throw new Error("swap字节码指令: value1 不匹配的数据类型" + value1.getType());
        }
        value1 = stack.pop();

        StackValue value2 = stack.peek();
        // 检查操作数类型
        if (value2.getType() == BasicType.T_DOUBLE || value2.getType() == BasicType.T_LONG) {
            log.error("swap字节码指令: value2 不匹配的数据类型" + value2.getType());
            throw new Error("swap字节码指令: value2 不匹配的数据类型" + value2.getType());
        }
        value2 = stack.pop();

        stack.push(value1);
        stack.push(value2);
    }

        /**
         * 执行dup2_x2字节码指令
         * 该指令功能为: 复制操作数栈顶1个或2个值，并插入栈顶以下2个、3个或4个值之后（不弹出栈顶的值）
         * @param currentThread 当前线程
         * @param code 当前方法的指令段
         * */
    private static void dup2X2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value1 = stack.peek();

        // double、long的处理方式和其他数据类型不同
        if (value1.getType() == BasicType.T_DOUBLE) {    // value1 为 double
            double _value1 = stack.popDouble();

            StackValue value2 = stack.peek();
            // 检查操作数类型
            if (value2.getType() == BasicType.T_DOUBLE) {   // value1 为 double、value2 为 double
                double _value2 = stack.popDouble();

                stack.pushDouble(_value1);
                stack.pushDouble(_value2);
                stack.pushDouble(_value1);

            } else if (value2.getType() == BasicType.T_LONG) {  // value1 为 double、value2 为 long
                value2 = stack.pop();

                stack.pushDouble(_value1);
                stack.push(value2);
                stack.pushDouble(_value1);

            } else {      // value1 为 double、value2 为其他类型，value3 为其他类型
                value2 = stack.pop();

                StackValue value3 = stack.peek();
                if (value3.getType() == BasicType.T_DOUBLE || value3.getType() == BasicType.T_LONG) {
                    log.error("dup2_x1字节码指令: value3 不匹配的数据类型: " + value3.getType());
                    throw new Error("dup2_x1字节码指令: value3 不匹配的数据类型" + value3.getType());
                }
                value3 = stack.pop();

                stack.pushDouble(_value1);
                stack.push(value3);
                stack.push(value2);
                stack.pushDouble(_value1);
            }


        } else if (value1.getType() == BasicType.T_LONG) {   // value1 为 long
            value1 = stack.pop();

            StackValue value2 = stack.peek();
            // 检查操作数类型
            if (value2.getType() == BasicType.T_DOUBLE) {   // value1 为 long、value2 为 double
                double _value2 = stack.popDouble();

                stack.push(value1);
                stack.pushDouble(_value2);
                stack.push(value1);

            } else if (value2.getType() == BasicType.T_LONG) {  // value1 为 long、value2 为 long
                value2 = stack.pop();

                stack.push(value1);
                stack.push(value2);
                stack.push(value1);

            } else {      // value1 为 long、value2 为其他类型，value3 为其他类型
                value2 = stack.pop();

                StackValue value3 = stack.peek();
                if (value3.getType() == BasicType.T_DOUBLE || value3.getType() == BasicType.T_LONG) {
                    log.error("dup2_x1字节码指令: value3 不匹配的数据类型" + value3.getType());
                    throw new Error("dup2_x1字节码指令: value3 不匹配的数据类型" + value3.getType());
                }
                value3 = stack.pop();

                stack.push(value1);
                stack.push(value3);
                stack.push(value2);
                stack.push(value1);
            }
        } else {    // value1 为其他类型，value2 为其他类型
            value1 = stack.pop();

            StackValue value2 = stack.peek();
            // 检查操作数类型
            if (value2.getType() == BasicType.T_DOUBLE || value2.getType() == BasicType.T_LONG) {
                log.error("dup2_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
                throw new Error("dup2_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
            }
            value2 = stack.pop();

            StackValue value3 = stack.peek();
            if (value3.getType() == BasicType.T_DOUBLE) {   // value1 为其他类型，value2 为其他类型，value3 为 double
                double _value3 = stack.popDouble();

                stack.push(value2);
                stack.push(value1);
                stack.pushDouble(_value3);
                stack.push(value2);
                stack.push(value1);
            } else if (value3.getType() == BasicType.T_LONG) {  // value1 为其他类型，value2 为其他类型，value3 为 long
                value3 = stack.pop();

                stack.push(value2);
                stack.push(value1);
                stack.push(value3);
                stack.push(value2);
                stack.push(value1);
            } else {    // value1 为其他类型，value2 为其他类型，value3 为其他类型
                value3 = stack.pop();

                StackValue value4 = stack.peek();
                if (value4.getType() == BasicType.T_DOUBLE || value4.getType() == BasicType.T_LONG) {
                    log.error("dup2_x1字节码指令: value4 不匹配的数据类型" + value4.getType());
                    throw new Error("dup2_x1字节码指令: value4 不匹配的数据类型" + value4.getType());
                }
                value4 = stack.pop();

                stack.push(value2);
                stack.push(value1);
                stack.push(value4);
                stack.push(value3);
                stack.push(value2);
                stack.push(value1);
            }
        }
    }

    /**
     * 执行dup2_x1字节码指令
     * 该指令功能为: 复制操作数栈顶1个或2个值，并插入栈顶以下2个或3个值之后（不弹出栈顶的值）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dup2X1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value1 = stack.peek();

        // double、long的处理方式和其他数据类型不同
        if (value1.getType() == BasicType.T_DOUBLE) {    // double类型，在操作数栈中占两个槽位，double需要取两次
            double _value1 = stack.popDouble();

            StackValue value2 = stack.peek();
            // 检查操作数类型
            if (value2.getType() == BasicType.T_DOUBLE || value2.getType() == BasicType.T_LONG) {
                log.error("dup2_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
                throw new Error("dup2_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
            }
            value2 = stack.pop();

            stack.pushDouble(_value1);
            stack.push(value2);
            stack.pushDouble(_value1);


        } else if (value1.getType() == BasicType.T_LONG) {   // long只需要取一次
            value1 = stack.pop();

            StackValue value2 = stack.peek();
            // 检查操作数类型
            if (value2.getType() == BasicType.T_DOUBLE || value2.getType() == BasicType.T_LONG) {
                log.error("dup2_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
                throw new Error("dup2_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
            }
            value2 = stack.pop();

            stack.push(value1);
            stack.push(value2);
            stack.push(value1);
        } else {
            value1 = stack.pop();

            StackValue value2 = stack.peek();
            // 检查操作数类型
            if (value2.getType() == BasicType.T_DOUBLE || value2.getType() == BasicType.T_LONG) {
                log.error("dup2_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
                throw new Error("dup2_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
            }
            value2 = stack.pop();

            StackValue value3 = stack.peek();
            // 检查操作数类型
            if (value3.getType() == BasicType.T_DOUBLE || value3.getType() == BasicType.T_LONG) {
                log.error("dup2_x1字节码指令: value3 不匹配的数据类型" + value3.getType());
                throw new Error("dup2_x1字节码指令: value3 不匹配的数据类型" + value3.getType());
            }
            value3 = stack.pop();

            stack.push(value2);
            stack.push(value1);
            stack.push(value3);
            stack.push(value2);
            stack.push(value1);
        }
    }

    /**
     * 执行dup2字节码指令
     * 该指令功能为: 复制操作数栈顶1个或2个值，并插入到栈顶（不弹出栈顶的值）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dup2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.peek();

        // double、long的处理方式和其他数据类型不同
        if (value.getType() == BasicType.T_DOUBLE) {    // double类型，在操作数栈中占两个槽位，double需要取两次
            double _value = stack.popDouble();

            stack.pushDouble(_value);
            stack.pushDouble(_value);
        } else if (value.getType() == BasicType.T_LONG) {   // long只需要取一次
            stack.push(value);
        } else {
            StackValue value1 = stack.pop();

            StackValue value2 = stack.peek();
            // 检查操作数类型
            if (value2.getType() == BasicType.T_DOUBLE || value2.getType() == BasicType.T_LONG) {
                log.error("dup2字节码指令: value2 不匹配的数据类型" + value2.getType());
                throw new Error("dup2字节码指令: value2 不匹配的数据类型" + value2.getType());
            }
            value2 = stack.pop();

            stack.push(value2);
            stack.push(value1);
            stack.push(value2);
            stack.push(value1);
        }
    }

    /**
     * 执行dup_x2字节码指令
     * 该指令功能为: 复制操作数栈顶的值，并插入到栈顶两个或三个元素之后（不弹出栈顶的值）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dupX2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() == BasicType.T_DOUBLE || value1.getType() == BasicType.T_LONG) {
            log.error("dup_x2字节码指令: value1 不匹配的数据类型" + value1.getType());
            throw new Error("dup_x2字节码指令: value1 不匹配的数据类型" + value1.getType());
        }
        value1 = stack.pop();

        // value2不能直接弹出，因为value2如果是double类型是占两个槽位的，要弹出两个槽位才行；如果不是double，则只需要弹出一个槽位即可，所以要先看下栈顶元素是什么类型，再去做相应的操作
        StackValue value2 = stack.peek();

        // double、long的处理方式和其他数据类型不同
        // value2是double类型，在操作数栈中占两个槽位，double需要取两次
        if (value2.getType() == BasicType.T_DOUBLE) {
            // 取出栈顶第二个元素（double类型，两个槽位）
            double _value2 = stack.popDouble();

            // 将栈顶元素以及上面取出的栈顶两个元素重新压入栈
            stack.push(value1);
            stack.pushDouble(_value2);
            stack.push(value1);

        } else if (value2.getType() == BasicType.T_LONG) {  // long只需要取一次
            // 取出栈顶第二个元素（long类型）
            value2 = stack.pop();
            // 将栈顶元素以及上面取出的栈顶两个元素重新压入栈
            stack.push(value1);
            stack.push(value2);
            stack.push(value1);

        } else {    // value2是其他类型
            // 取出栈顶第二个元素
            value2 = stack.pop();

            // 取出栈顶第三个元素
            StackValue value3 = stack.peek();
            if (value3.getType() == BasicType.T_DOUBLE || value1.getType() == BasicType.T_LONG) {
                log.error("dup_x2字节码指令: value3 不匹配的数据类型" + value3.getType());
                throw new Error("dup_x2字节码指令: value3 不匹配的数据类型" + value3.getType());
            }
            value3 = stack.pop();

            // 将栈顶元素以及上面取出的栈顶三个元素重新压入栈
            stack.push(value1);
            stack.push(value3);
            stack.push(value2);
            stack.push(value1);
        }
    }

    /**
     * 执行dup_x1字节码指令
     * 该指令功能为: 复制操作数栈顶的值，并插入到栈顶两个元素之后（不弹出栈顶的值）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dupX1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶两个元素
        StackValue value1 = stack.peek();
        if (value1.getType() == BasicType.T_DOUBLE || value1.getType() == BasicType.T_LONG) {
            log.error("dup_x1字节码指令: value1 不匹配的数据类型" + value1.getType());
            throw new Error("dup_x1字节码指令: value1 不匹配的数据类型" + value1.getType());
        }
        value1 = stack.pop();

        StackValue value2 = stack.peek();
        // 检查操作数类型
        if (value2.getType() == BasicType.T_DOUBLE || value2.getType() == BasicType.T_LONG) {
            log.error("dup_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
            throw new Error("dup_x1字节码指令: value2 不匹配的数据类型" + value2.getType());
        }
        value2 = stack.pop();

        // 将栈顶元素以及上面取出的栈顶两个元素重新压入栈
        stack.push(value1);
        stack.push(value2);
        stack.push(value1);
    }

    /**
     * 执行dup字节码指令
     * 该指令功能为: 复制操作数栈顶的值，并插入到栈顶（不弹出栈顶的值）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dup(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.peek();

        // 检查操作数类型
        if (value.getType() == BasicType.T_LONG || value.getType() == BasicType.T_DOUBLE) {
            log.error("dup字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("dup字节码指令: value 不匹配的数据类型" + value.getType());
        }

        // 压入栈
        stack.push(value);
    }

    /**
     * 执行iinc字节码指令
     * 该指令功能为: 将局部变量表中指定的int类型变量增加指定值（i++、i--、i+=2）（操作数1: 局部变量表中的索引，操作数2: 增加的常量值）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iInc(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 第一个操作数: 局部变量表中的索引（无符号byte）
        int index = code.getU1Code();

        // 第二个操作数: 增加多少（有符号byte）
        int step = code.getU1Code2();

        // 运算
        int v = (int) local.get(index).getData();
        v += step;

        // 写回局部变量表
        local.set(index, new StackValue(BasicType.T_INT, v));
    }

    /**
     * 执行lxor字节码指令
     * 该指令功能为: 将栈顶两个long类型数值按位异或并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lXor(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_LONG) {
            log.error("lxor字节码指令: 不匹配的数据类型");
            throw new Error("lxor字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = (long) value1.getData() ^ (long) value2.getData();

        log.info("执行指令: lxor，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行lor字节码指令
     * 该指令功能为: 将栈顶两个long类型数值按位或并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lOr(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_LONG) {
            log.error("lor字节码指令: 不匹配的数据类型");
            throw new Error("lor字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = (long) value1.getData() | (long) value2.getData();

        log.info("执行指令: lor，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行land字节码指令
     * 该指令功能为: 将栈顶两个long类型数值按位与并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lAnd(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_LONG) {
            log.error("land字节码指令: 不匹配的数据类型");
            throw new Error("land字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = (long) value1.getData() & (long) value2.getData();

        log.info("执行指令: land，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行ixor字节码指令
     * 该指令功能为: 将栈顶两个int类型数值按位异或并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iXor(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("xor字节码指令: 不匹配的数据类型");
            throw new Error("xor字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = (int) value1.getData() ^ (int) value2.getData();

        log.info("执行指令: xor，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行ior字节码指令
     * 该指令功能为: 将栈顶两个int类型数值按位或并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iOr(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("ior字节码指令: 不匹配的数据类型");
            throw new Error("ior字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = (int) value1.getData() | (int) value2.getData();

        log.info("执行指令: ior，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行iand字节码指令
     * 该指令功能为: 将栈顶两个int类型数值按位与并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iAnd(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("iand字节码指令: 不匹配的数据类型");
            throw new Error("iand字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = (int) value1.getData() & (int) value2.getData();

        log.info("执行指令: iand，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行lushr字节码指令
     * 该指令功能为: 将栈顶long类型数值右移指定位数（无符号）并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lUShr(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_INT) {
            log.error("lushr字节码指令: 不匹配的数据类型");
            throw new Error("lushr字节码指令: 不匹配的数据类型");
        }

        // 运算
        int s = ((int) value2.getData()) & 0x3F;
        long ret = ((long) value1.getData()) >>> s;

        log.info("执行指令: lushr，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行iushr字节码指令
     * 该指令功能为: 将栈顶int类型数值右移指定位数（无符号）并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iUShr(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("iushr字节码指令: 不匹配的数据类型");
            throw new Error("iushr字节码指令: 不匹配的数据类型");
        }

        // 运算
        int s = ((int) value2.getData()) & 0x1F;
        int ret = ((int) value1.getData()) >>> s;

        log.info("执行指令: iushr，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行lshr字节码指令
     * 该指令功能为: 将栈顶long类型数值右移指定位数（有符号）并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lShr(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_INT) {
            log.error("lshr字节码指令: 不匹配的数据类型");
            throw new Error("lshr字节码指令: 不匹配的数据类型");
        }

        // 运算
        int s = ((int) value2.getData()) & 0x3F;
        long ret = ((long) value1.getData()) >> s;

        log.info("执行指令: lshr，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }


    /**
     * 执行ishr字节码指令
     * 该指令功能为: 将栈顶int类型数值右移指定位数（有符号）并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iShr(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("ishr字节码指令: 不匹配的数据类型");
            throw new Error("ishr字节码指令: 不匹配的数据类型");
        }

        // 运算
        int s = ((int) value2.getData()) & 0x1F;
        int ret = ((int) value1.getData()) >> s;

        log.info("执行指令: ishr，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行lshl字节码指令
     * 该指令功能为: 将栈顶long类型数值左移指定位数并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lShl(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_INT) {
            log.error("lshl字节码指令: 不匹配的数据类型");
            throw new Error("lshl字节码指令: 不匹配的数据类型");
        }

        // 运算
        int s = ((int) value2.getData()) & 0x3F;
        long ret = ((long) value1.getData()) << s;

        log.info("执行指令: lshl，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }


    /**
     * 执行ishl字节码指令
     * 该指令功能为: 将栈顶int类型数值左移指定位数并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iShl(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("ishl字节码指令: 不匹配的数据类型");
            throw new Error("ishl字节码指令: 不匹配的数据类型");
        }

        // 运算
        int s = ((int) value2.getData()) & 0x1F;
        int ret = ((int) value1.getData()) << s;

        log.info("执行指令: ishl，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行dneg字节码指令
     * 该指令功能为: 将栈顶double类型数值取负并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dNeg(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        double value = stack.popDouble();
        // 运算
        double ret = -(value);

        log.info("执行指令: dneg，运行结果: " + ret);

        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行fneg字节码指令
     * 该指令功能为: 将栈顶float类型数值取负并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fNeg(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fneg字节码指令: 不匹配的数据类型");
            throw new Error("fneg字节码指令: 不匹配的数据类型");
        }

        // 运算
        float ret = -((float) value.getData());

        log.info("执行指令: fneg，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
    }

    /**
     * 执行lneg字节码指令
     * 该指令功能为: 将栈顶long类型数值取负并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lNeg(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lneg字节码指令: 不匹配的数据类型");
            throw new Error("lneg字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = -((long) value.getData());

        log.info("执行指令: lneg，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行ineg字节码指令
     * 该指令功能为: 将栈顶int类型数值取负并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iNeg(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("ineg字节码指令: 不匹配的数据类型");
            throw new Error("ineg字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = -((int) value.getData());

        log.info("执行指令: ineg，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行drem字节码指令
     * 该指令功能为: 将栈顶两个double类型数值取模并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dRem(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        double value2 = stack.popDouble();
        double value1 = stack.popDouble();

        // 运算
        double ret = value1 % value2;

        log.info("执行指令: drem，运行结果: " + ret);

        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行frem字节码指令
     * 该指令功能为: 将栈顶两个float类型数值取模并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fRem(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_FLOAT || value2.getType() != BasicType.T_FLOAT) {
            log.error("frem字节码指令: 不匹配的数据类型");
            throw new Error("frem字节码指令: 不匹配的数据类型");
        }

        // 运算
        float ret = (float) value1.getData() % (float) value2.getData();

        log.info("执行指令: frem，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
    }

    /**
     * 执行lrem字节码指令
     * 该指令功能为: 将栈顶两个long类型数值取模并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lRem(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_LONG) {
            log.error("lrem字节码指令: 不匹配的数据类型");
            throw new Error("lrem字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = (long) value1.getData() % (long) value2.getData();

        log.info("执行指令: lrem，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行irem字节码指令
     * 该指令功能为: 将栈顶两个int类型数值取模并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iRem(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("irem字节码指令: 不匹配的数据类型");
            throw new Error("irem字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = (int) value1.getData() % (int) value2.getData();

        log.info("执行指令: irem，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行ddiv字节码指令
     * 该指令功能为: 将栈顶两个double类型数值相除并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dDiv(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        double value2 = stack.popDouble();
        double value1 = stack.popDouble();

        // 运算
        double ret = value1 / value2;

        log.info("执行指令: ddiv，运行结果: " + ret);

        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行fdiv字节码指令
     * 该指令功能为: 将栈顶两个float类型数值相除并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fDiv(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_FLOAT || value2.getType() != BasicType.T_FLOAT) {
            log.error("fdiv字节码指令: 不匹配的数据类型");
            throw new Error("fdiv字节码指令: 不匹配的数据类型");
        }

        // 运算
        float ret = (float) value1.getData() / (float) value2.getData();

        log.info("执行指令: fdiv，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
    }

    /**
     * 执行ldiv字节码指令
     * 该指令功能为: 将栈顶两个long类型数值相除并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lDiv(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_LONG) {
            log.error("ldiv字节码指令: 不匹配的数据类型");
            throw new Error("ldiv字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = (long) value1.getData() / (long) value2.getData();

        log.info("执行指令: ldiv，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行idiv字节码指令
     * 该指令功能为: 将栈顶两个int类型数值相除并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iDiv(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("idiv字节码指令: 不匹配的数据类型");
            throw new Error("idiv字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = (int) value1.getData() / (int) value2.getData();

        log.info("执行指令: idiv，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行dmul字节码指令
     * 该指令功能为: 将栈顶两个double类型数值相乘并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dMul(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        double value2 = stack.popDouble();
        double value1 = stack.popDouble();

        // 运算
        double ret = value1 * value2;

        log.info("执行指令: dmul，运行结果: " + ret);

        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行fmul字节码指令
     * 该指令功能为: 将栈顶两个float类型数值相乘并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fMul(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_FLOAT || value2.getType() != BasicType.T_FLOAT) {
            log.error("fmul字节码指令: 不匹配的数据类型");
            throw new Error("fmul字节码指令: 不匹配的数据类型");
        }

        // 运算
        float ret = (float) value1.getData() * (float) value2.getData();

        log.info("执行指令: fmul，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
    }

    /**
     * 执行lmul字节码指令
     * 该指令功能为: 将栈顶两个long类型数值相乘并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lMul(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_LONG) {
            log.error("lmul字节码指令: 不匹配的数据类型");
            throw new Error("lmul字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = (long) value1.getData() * (long) value2.getData();

        log.info("执行指令: lmul，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行imul字节码指令
     * 该指令功能为: 将栈顶两个int类型数值相乘并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iMul(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("imul字节码指令: 不匹配的数据类型");
            throw new Error("imul字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = (int) value1.getData() * (int) value2.getData();

        log.info("执行指令: imul，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行dsub字节码指令
     * 该指令功能为: 将栈顶两个double类型数值相减并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dSub(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        double value2 = stack.popDouble();
        double value1 = stack.popDouble();

        // 运算
        double ret = value1 - value2;

        log.info("执行指令: dsub，运行结果: " + ret);

        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行fsub字节码指令
     * 该指令功能为: 将栈顶两个float类型数值相减并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fSub(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_FLOAT || value2.getType() != BasicType.T_FLOAT) {
            log.error("fsub字节码指令: 不匹配的数据类型");
            throw new Error("fsub字节码指令: 不匹配的数据类型");
        }

        // 运算
        float ret = (float) value1.getData() - (float) value2.getData();

        log.info("执行指令: fsub，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
    }

    /**
     * 执行lsub字节码指令
     * 该指令功能为: 将栈顶两个long类型数值相减并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lSub(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_LONG) {
            log.error("lsub字节码指令: 不匹配的数据类型");
            throw new Error("lsub字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = (long) value1.getData() - (long) value2.getData();

        log.info("执行指令: lsub，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行isub字节码指令
     * 该指令功能为: 将栈顶两个int类型数值相减并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iSub(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("isub字节码指令: 不匹配的数据类型");
            throw new Error("isub字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = (int) value1.getData() - (int) value2.getData();

        log.info("执行指令: isub，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行dadd字节码指令
     * 该指令功能为: 将栈顶两个double类型数值相加并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dAdd(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        double value2 = stack.popDouble();
        double value1 = stack.popDouble();

        // 运算
        double ret = value1 + value2;

        log.info("执行指令: dadd，运行结果: " + ret);

        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行fadd字节码指令
     * 该指令功能为: 将栈顶两个float类型数值相加并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fAdd(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_FLOAT || value2.getType() != BasicType.T_FLOAT) {
            log.error("fadd字节码指令: 不匹配的数据类型");
            throw new Error("fadd字节码指令: 不匹配的数据类型");
        }

        // 运算
        float ret = (float) value1.getData() + (float) value2.getData();

        log.info("执行指令: fadd，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
    }

    /**
     * 执行ladd字节码指令
     * 该指令功能为: 将栈顶两个long类型数值相加并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lAdd(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG || value2.getType() != BasicType.T_LONG) {
            log.error("ladd字节码指令: 不匹配的数据类型");
            throw new Error("ladd字节码指令: 不匹配的数据类型");
        }

        // 运算
        long ret = (long) value1.getData() + (long) value2.getData();

        log.info("执行指令: ladd，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行iadd字节码指令
     * 该指令功能为: 将栈顶两个int类型数值相加并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iAdd(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value2 = stack.pop();
        StackValue value1 = stack.pop();

        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
            log.error("iadd字节码指令: 不匹配的数据类型");
            throw new Error("iadd字节码指令: 不匹配的数据类型");
        }

        // 运算
        int ret = (int) value1.getData() + (int) value2.getData();

        log.info("执行指令: iadd，运行结果: " + ret);

        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行i2s字节码指令
     * 该指令功能为: 将栈顶int类型数值强制转换成short类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void i2s(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2s字节码指令: 不匹配的数据类型");
            throw new Error("i2s字节码指令: 不匹配的数据类型");
        }

        // 强转
        short ret = (short) ((int) value.getData());
        // 将结果压入栈中
        // 在JVM中，小于4字节的数据类型都是以4字节进行存储的，即小于4字节的数据类型都是以int类型存储的
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行i2c字节码指令
     * 该指令功能为: 将栈顶int类型数值强制转换成char类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void i2c(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2c字节码指令: 不匹配的数据类型");
            throw new Error("i2c字节码指令: 不匹配的数据类型");
        }

        // 强转
        char ret = (char) ((int) value.getData());
        // 将结果压入栈中
        // 在JVM中，小于4字节的数据类型都是以4字节进行存储的，即小于4字节的数据类型都是以int类型存储的
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行i2b字节码指令
     * 该指令功能为: 将栈顶int类型数值强制转换成byte类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void i2b(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2b字节码指令: 不匹配的数据类型");
            throw new Error("i2b字节码指令: 不匹配的数据类型");
        }

        // 强转
        byte ret = (byte) ((int) value.getData());
        // 将结果压入栈中
        // 在JVM中，小于4字节的数据类型都是以4字节进行存储的，即小于4字节的数据类型都是以int类型存储的
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行d2f字节码指令
     * 该指令功能为: 将栈顶double类型数值强制转换成float类型数值并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void d2f(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素，强转
        float value =  (float) stack.popDouble();
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, value));
    }

    /**
     * 执行d2l字节码指令
     * 该指令功能为: 将栈顶double类型数值强制转换成long类型数值并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void d2l(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素，强转
        long value =  (long) stack.popDouble();
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, value));
    }

    /**
     * 执行d2i字节码指令
     * 该指令功能为: 将栈顶double类型数值强制转换成int类型数值并将结果压入栈顶（需要将double类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void d2i(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素，强转
        int value =  (int) stack.popDouble();
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, value));
    }

    /**
     * 执行f2d字节码指令
     * 该指令功能为: 将栈顶float类型数值强制转换成double类型数值并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void f2d(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("f2d字节码指令: 不匹配的数据类型");
            throw new Error("f2d字节码指令: 不匹配的数据类型");
        }

        // 强转
        double ret =  (float) value.getData();
        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行f2l字节码指令
     * 该指令功能为: 将栈顶float类型数值强制转换成long类型数值并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void f2l(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("f2l字节码指令: 不匹配的数据类型");
            throw new Error("f2l字节码指令: 不匹配的数据类型");
        }

        // 强转
        long ret = (long) ((float) value.getData());
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
    }

    /**
     * 执行f2i字节码指令
     * 该指令功能为: 将栈顶float类型数值强制转换成int类型数值并将结果压入栈顶（需要将float类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void f2i(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("f2i字节码指令: 不匹配的数据类型");
            throw new Error("f2i字节码指令: 不匹配的数据类型");
        }

        // 强转
        int ret = (int) ((float) value.getData());
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行l2d字节码指令
     * 该指令功能为: 将栈顶long类型数值强制转换成double类型数值并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void l2d(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("l2d字节码指令: 不匹配的数据类型");
            throw new Error("l2d字节码指令: 不匹配的数据类型");
        }

        // 强转
        double ret = (double) ((long) value.getData());
        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行l2f字节码指令
     * 该指令功能为: 将栈顶long类型数值强制转换成float类型数值并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void l2f(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("l2f字节码指令: 不匹配的数据类型");
            throw new Error("l2f字节码指令: 不匹配的数据类型");
        }

        // 强转
        float ret = (float) ((long) value.getData());
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
    }

    /**
     * 执行l2i字节码指令
     * 该指令功能为: 将栈顶long类型数值强制转换成int类型数值并将结果压入栈顶（需要将long类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void l2i(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("l2i字节码指令: 不匹配的数据类型");
            throw new Error("l2i字节码指令: 不匹配的数据类型");
        }

        // 强转
        int ret = (int) ((long) value.getData());
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_INT, ret));
    }

    /**
     * 执行i2d字节码指令
     * 该指令功能为: 将栈顶int类型数值强制转换成double类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void i2d(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2d字节码指令: 不匹配的数据类型");
            throw new Error("i2d字节码指令: 不匹配的数据类型");
        }

        // 强转
        double ret = (int) value.getData();
        // 将结果压入栈中
        stack.pushDouble(ret);
    }

    /**
     * 执行i2f字节码指令
     * 该指令功能为: 将栈顶int类型数值强制转换成float类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void i2f(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2f字节码指令: 不匹配的数据类型");
            throw new Error("i2f字节码指令: 不匹配的数据类型");
        }

        // 强转
        float ret = (int) value.getData();
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
    }

    /**
     * 执行i2l字节码指令
     * 该指令功能为: 将栈顶int类型数值强制转换成long类型数值并将结果压入栈顶（需要将int类型先从栈中弹出）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void i2l(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶元素
        StackValue value = stack.pop();

        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2l字节码指令: 不匹配的数据类型");
            throw new Error("i2l字节码指令: 不匹配的数据类型");
        }

        // 强转
        long ret = (int) value.getData();
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
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
        // 检查操作数类型
        if (values[0].getType() != BasicType.T_DOUBLE || values[1].getType() != BasicType.T_DOUBLE) {
            log.error("dstore_3字节码指令: 不匹配的数据类型");
            throw new Error("dstore_3字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (values[0].getType() != BasicType.T_DOUBLE || values[1].getType() != BasicType.T_DOUBLE) {
            log.error("dstore_2字节码指令: 不匹配的数据类型");
            throw new Error("dstore_2字节码指令: 不匹配的数据类型");
        }
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

        // 检查操作数类型
        if (values[0].getType() != BasicType.T_DOUBLE || values[1].getType() != BasicType.T_DOUBLE) {
            log.error("dstore_1字节码指令: 不匹配的数据类型");
            throw new Error("dstore_1字节码指令: 不匹配的数据类型");
        }

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

        // 检查操作数类型
        if (values[0].getType() != BasicType.T_DOUBLE || values[1].getType() != BasicType.T_DOUBLE) {
            log.error("dstore_0字节码指令: 不匹配的数据类型");
            throw new Error("dstore_0字节码指令: 不匹配的数据类型");
        }

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

        // 检查操作数类型
        if (values[0].getType() != BasicType.T_DOUBLE || values[1].getType() != BasicType.T_DOUBLE) {
            log.error("dstore字节码指令: 不匹配的数据类型");
            throw new Error("dstore字节码指令: 不匹配的数据类型");
        }

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

        // 检查操作数类型
        if (valueLow.getType() != BasicType.T_DOUBLE || valueHigh.getType() != BasicType.T_DOUBLE) {
            log.error("dload_3字节码指令: 不匹配的数据类型");
            throw new Error("dload_3字节码指令: 不匹配的数据类型");
        }

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

        // 检查操作数类型
        if (valueLow.getType() != BasicType.T_DOUBLE || valueHigh.getType() != BasicType.T_DOUBLE) {
            log.error("dload_2字节码指令: 不匹配的数据类型");
            throw new Error("dload_2字节码指令: 不匹配的数据类型");
        }

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

        // 检查操作数类型
        if (valueLow.getType() != BasicType.T_DOUBLE || valueHigh.getType() != BasicType.T_DOUBLE) {
            log.error("dload_1字节码指令: 不匹配的数据类型");
            throw new Error("dload_1字节码指令: 不匹配的数据类型");
        }

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

        // 检查操作数类型
        if (valueLow.getType() != BasicType.T_DOUBLE || valueHigh.getType() != BasicType.T_DOUBLE) {
            log.error("dload_0字节码指令: 不匹配的数据类型");
            throw new Error("dload_0字节码指令: 不匹配的数据类型");
        }

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

        // 检查操作数类型
        if (valueLow.getType() != BasicType.T_DOUBLE || valueHigh.getType() != BasicType.T_DOUBLE) {
            log.error("dload字节码指令: 不匹配的数据类型");
            throw new Error("dload字节码指令: 不匹配的数据类型");
        }

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
        stack.push(new StackValue(BasicType.T_LONG, (long) 0));
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
        stack.push(new StackValue(BasicType.T_LONG, (long) 1));
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lload_3字节码指令: 不匹配的数据类型");
            throw new Error("lload_3字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lload_2字节码指令: 不匹配的数据类型");
            throw new Error("lload_2字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lload_1字节码指令: 不匹配的数据类型");
            throw new Error("lload_1字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lload_0字节码指令: 不匹配的数据类型");
            throw new Error("lload_0字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lload字节码指令: 不匹配的数据类型");
            throw new Error("lload字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore_3字节码指令: 不匹配的数据类型");
            throw new Error("lstore_3字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore_2字节码指令: 不匹配的数据类型");
            throw new Error("lstore_2字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore_1字节码指令: 不匹配的数据类型");
            throw new Error("lstore_1字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore_0字节码指令: 不匹配的数据类型");
            throw new Error("lstore_0字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore字节码指令: 不匹配的数据类型");
            throw new Error("lstore字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore_0字节码指令: 不匹配的数据类型");
            throw new Error("fstore_0字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore_1字节码指令: 不匹配的数据类型");
            throw new Error("fstore_1字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore_2字节码指令: 不匹配的数据类型");
            throw new Error("fstore_2字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore_3字节码指令: 不匹配的数据类型");
            throw new Error("fstore_3字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore字节码指令: 不匹配的数据类型");
            throw new Error("fstore字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fload_0字节码指令: 不匹配的数据类型");
            throw new Error("fload_0字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fload_1字节码指令: 不匹配的数据类型");
            throw new Error("fload_1字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fload_2字节码指令: 不匹配的数据类型");
            throw new Error("fload_2字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fload_3字节码指令: 不匹配的数据类型");
            throw new Error("fload_3字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fload字节码指令: 不匹配的数据类型");
            throw new Error("fload字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("iload字节码指令: 不匹配的数据类型");
            throw new Error("iload字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("iload_0字节码指令: 不匹配的数据类型");
            throw new Error("iload_0字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("iload_1字节码指令: 不匹配的数据类型");
            throw new Error("iload_1字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("iload_2字节码指令: 不匹配的数据类型");
            throw new Error("iload_2字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("iload_3字节码指令: 不匹配的数据类型");
            throw new Error("iload_3字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore字节码指令: 不匹配的数据类型");
            throw new Error("istore字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore_0字节码指令: 不匹配的数据类型");
            throw new Error("istore_0字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore_1字节码指令: 不匹配的数据类型");
            throw new Error("istore_1字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore_2字节码指令: 不匹配的数据类型");
            throw new Error("istore_2字节码指令: 不匹配的数据类型");
        }
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
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore_3字节码指令: 不匹配的数据类型");
            throw new Error("istore_3字节码指令: 不匹配的数据类型");
        }
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
