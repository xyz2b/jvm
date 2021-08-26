package org.xyz.jvm.hotspot.src.share.vm.intepreter;

import lombok.extern.slf4j.Slf4j;
import org.xyz.jvm.hotspot.src.share.vm.classfile.BootClassLoader;
import org.xyz.jvm.hotspot.src.share.vm.classfile.DescriptorStream;
import org.xyz.jvm.hotspot.src.share.vm.oops.*;
import org.xyz.jvm.hotspot.src.share.vm.oops.attribute.CodeAttribute;
import org.xyz.jvm.hotspot.src.share.vm.prims.JavaNativeInterface;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaThread;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaVFrame;
import org.xyz.jvm.hotspot.src.share.vm.runtime.StackValue;
import org.xyz.jvm.hotspot.src.share.vm.runtime.StackValueCollection;
import org.xyz.jvm.hotspot.src.share.vm.utilities.BasicType;

import java.lang.reflect.Constructor;
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
                case ByteCodes.ACONST_NULL: {
                    log.info("执行指令: aconst_null，该指令功能: 将一个null对象引用压入栈顶");
                    aConstNull(currentThread, code);
                    break;
                }
                case ByteCodes.LDC: {
                    log.info("执行指令: ldc，该指令功能为: 从运行时常量池中提取数据并压入操作数栈");
                    ldc(currentThread, code);
                    break;
                }
                case ByteCodes.NEW: {
                    log.info("执行指令: new，该指令功能为: 创建一个对象，并将其引用压入栈顶");
                    jNew(currentThread, code);
                    break;
                }
                case ByteCodes.RETURN:  {
                    log.info("执行指令: return，该指令功能为: 从方法中返回void，恢复调用者的栈帧，并且把程序的控制权交回调用者");
                    jReturn(currentThread);
                    break;
                }
                case ByteCodes.IRETURN:  {
                    log.info("执行指令: ireturn，该指令功能为: 从方法中返回int类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者");
                    iReturn(currentThread);
                    break;
                }
                case ByteCodes.LRETURN:  {
                    log.info("执行指令: lreturn，该指令功能为: 从方法中返回long类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者");
                    lReturn(currentThread);
                    break;
                }
                case ByteCodes.FRETURN:  {
                    log.info("执行指令: freturn，该指令功能为: 从方法中返回float类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者");
                    fReturn(currentThread);
                    break;
                }
                case ByteCodes.DRETURN:  {
                    log.info("执行指令: dreturn，该指令功能为: 从方法中返回double类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者");
                    dReturn(currentThread);
                    break;
                }
                case ByteCodes.ARETURN:  {
                    log.info("执行指令: areturn，该指令功能为: 从方法中返回引用类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者");
                    aReturn(currentThread);
                    break;
                }
                case ByteCodes.GETSTATIC: {
                    log.info("执行指令: getstatic，该指令功能为: 获取类的静态字段值并压入操作数栈");
                    getStatic(currentThread, code);
                    break;
                }
                case ByteCodes.PUTSTATIC: {
                    log.info("执行指令: putstatic，该指令功能为: 为指定类的静态字段赋值");
                    putStatic(currentThread, code);
                    break;
                }
                case ByteCodes.GETFIELD: {
                    log.info("执行指令: getField，该指令功能为: 获取对象的属性值并压入操作数栈");
                    getField(currentThread, code);
                    break;
                }
                case ByteCodes.PUTFIELD: {
                    log.info("执行指令: putField，该指令功能为: 设置对象的属性值");
                    putField(currentThread, code);
                    break;
                }
                case ByteCodes.INVOKEVIRTUAL: {
                    log.info("执行指令: invokevirtual，该指令功能为: 调用实例方法，依据实例的类型进行分派，这个方法不能使实例初始化方法也不能是类或接口的初始化方法（静态初始化方法）");
                    invokeVirtual(currentThread, code);
                    break;
                }
                case ByteCodes.INVOKESTATIC: {
                    log.info("执行指令: invokestatic，该指令功能为: 调用静态方法，即static修饰的方法");
                    invokeStatic(currentThread, code);
                    break;
                }
                case ByteCodes.INVOKESPECIAL: {
                    log.info("执行指令: invokespecial，该指令功能为: 调用实例方法，专门用来调用父类方法、私有方法和实例初始化方法");
                    invokeSpecial(currentThread, code);
                    break;
                }
                case ByteCodes.INVOKEINTERFACE: {
                    log.info("执行指令: invokeinterface，该指令功能为: 调用接口方法");
                    invokeInterface(currentThread, code);
                    break;
                }
                case ByteCodes.INVOKEDYNAMIC: {
                    log.info("执行指令: invokedynamic，该指令功能为: 调用动态方法");
                    invokeDynamic(currentThread, code);
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
                case ByteCodes.ASTORE: {
                    log.info("执行指令: astore，该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置");
                    aStore(currentThread, code);
                    break;
                }
                case ByteCodes.ASTORE_0: {
                    log.info("执行指令: astore_0，该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中索引为0的位置");
                    aStore0(currentThread, code);
                    break;
                }
                case ByteCodes.ASTORE_1: {
                    log.info("执行指令: astore_1，该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中索引为1的位置");
                    aStore1(currentThread, code);
                    break;
                }
                case ByteCodes.ASTORE_2: {
                    log.info("执行指令: astore_2，该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中索引为2的位置");
                    aStore2(currentThread, code);
                    break;
                }
                case ByteCodes.ASTORE_3: {
                    log.info("执行指令: astore_3，该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中索引为3的位置");
                    aStore3(currentThread, code);
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
                case ByteCodes.ALOAD: {
                    log.info("执行指令: aload，该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（引用类型）压入操作数栈中");
                    aLoad(currentThread, code);
                    break;
                }
                case ByteCodes.ALOAD_0: {
                    log.info("执行指令: aload_0，该指令功能为: 将局部变量表中索引为0的值（引用类型）压入操作数栈中");
                    aLoad0(currentThread, code);
                    break;
                }
                case ByteCodes.ALOAD_1: {
                    log.info("执行指令: aload_1，该指令功能为: 将局部变量表中索引为1的值（引用类型）压入操作数栈中");
                    aLoad1(currentThread, code);
                    break;
                }
                case ByteCodes.ALOAD_2: {
                    log.info("执行指令: aload_2，该指令功能为: 将局部变量表中索引为2的值（引用类型）压入操作数栈中");
                    aLoad2(currentThread, code);
                    break;
                }
                case ByteCodes.ALOAD_3: {
                    log.info("执行指令: aload_3，该指令功能为: 将局部变量表中索引为3的值（引用类型）压入操作数栈中");
                    aLoad3(currentThread, code);
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
                case ByteCodes.LCMP: {
                    log.info("执行指令: lcmp，该指令功能为: 比较栈顶两个long类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈）");
                    lCmp(currentThread, code);
                    break;
                }
                case ByteCodes.FCMPL: {
                    log.info("执行指令: lcmpl，该指令功能为: 比较栈顶两个float类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈），当其中一个值为NaN时，将-1压入栈顶");
                    fCmpL(currentThread, code);
                    break;
                }
                case ByteCodes.FCMPG: {
                    log.info("执行指令: lcmpg，该指令功能为: 比较栈顶两个float类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈），当其中一个值为NaN时，将1压入栈顶");
                    fCmpG(currentThread, code);
                    break;
                }
                case ByteCodes.DCMPL: {
                    log.info("执行指令: dcmpl，该指令功能为: 比较栈顶两个double类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈），当其中一个值为NaN时，将-1压入栈顶");
                    dCmpL(currentThread, code);
                    break;
                }
                case ByteCodes.DCMPG: {
                    log.info("执行指令: dcmpg，该指令功能为: 比较栈顶两个double类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈），当其中一个值为NaN时，将1压入栈顶");
                    dCmpG(currentThread, code);
                    break;
                }
                case ByteCodes.IFEQ: {
                    log.info("执行指令: ifeq，该指令功能为: 当栈顶int类型数值等于0时跳转");
                    ifEq(currentThread, code);
                    break;
                }
                case ByteCodes.IFNE: {
                    log.info("执行指令: ifne，该指令功能为: 当栈顶int类型数值不等于0时跳转");
                    ifNe(currentThread, code);
                    break;
                }
                case ByteCodes.IFLT: {
                    log.info("执行指令: iflt，该指令功能为: 当栈顶int类型数值小于0时跳转");
                    ifLt(currentThread, code);
                    break;
                }
                case ByteCodes.IFLE: {
                    log.info("执行指令: ifle，该指令功能为: 当栈顶int类型数值小于等于0时跳转");
                    ifLe(currentThread, code);
                    break;
                }
                case ByteCodes.IFGE: {
                    log.info("执行指令: ifge，该指令功能为: 当栈顶int类型数值大于等于0时跳转");
                    ifGe(currentThread, code);
                    break;
                }
                case ByteCodes.IFGT: {
                    log.info("执行指令: ifgt，该指令功能为: 当栈顶int类型数值大于0时跳转");
                    ifGt(currentThread, code);
                    break;
                }
                case ByteCodes.IF_ICMPEQ: {
                    log.info("执行指令: if_icmpeq，该指令功能为: 比较栈顶两个int类型数值大小，当前者等于后者时跳转");
                    ifICmpEq(currentThread, code);
                    break;
                }
                case ByteCodes.IF_ICMPNE: {
                    log.info("执行指令: if_icmpne，该指令功能为: 比较栈顶两个int类型数值大小，当前者不等于后者时跳转");
                    ifICmpNe(currentThread, code);
                    break;
                }
                case ByteCodes.IF_ICMPLT: {
                    log.info("执行指令: if_icmplt，该指令功能为: 比较栈顶两个int类型数值大小，当前者小于后者时跳转");
                    ifICmpLt(currentThread, code);
                    break;
                }
                case ByteCodes.IF_ICMPLE: {
                    log.info("执行指令: if_icmple，该指令功能为: 比较栈顶两个int类型数值大小，当前者小于等于后者时跳转");
                    ifICmpLe(currentThread, code);
                    break;
                }
                case ByteCodes.IF_ICMPGT: {
                    log.info("执行指令: if_icmpgt，该指令功能为: 比较栈顶两个int类型数值大小，当前者大于后者时跳转");
                    ifICmpGt(currentThread, code);
                    break;
                }
                case ByteCodes.IF_ICMPGE: {
                    log.info("执行指令: if_icmpge，该指令功能为: 比较栈顶两个int类型数值大小，当前者大于等于后者时跳转");
                    ifICmpGe(currentThread, code);
                    break;
                }
                case ByteCodes.GOTO: {
                    log.info("执行指令: goto，该指令功能为: 无条件跳转");
                    jGoto(currentThread, code);
                    break;
                }
                case ByteCodes.IF_ACMPEQ: {
                    log.info("执行指令: if_acmpeq，该指令功能为: 比较栈顶两个引用类型数值，当前结果相等时跳转");
                    ifACmpEq(currentThread, code);
                    break;
                }
                case ByteCodes.IF_ACMPNE: {
                    log.info("执行指令: if_acmpne，该指令功能为: 比较栈顶两个引用类型数值，当前结果不相等时跳转");
                    ifACmpNe(currentThread, code);
                    break;
                }
                case ByteCodes.IFNULL: {
                    log.info("执行指令: ifnull，该指令功能为: 当栈顶饮用类型数值为null时跳转");
                    ifNull(currentThread, code);
                    break;
                }
                case ByteCodes.IFNONNULL: {
                    log.info("执行指令: ifnonnull，该指令功能为: 当栈顶饮用类型数值不为null时跳转");
                    ifNonNull(currentThread, code);
                    break;
                }
                case ByteCodes.NEWARRAY: {
                    log.info("执行指令: newarray，该指令功能为: 创建一个新的一维数组（数组元素为基本类型），并将该数组的引用压入操作数栈中");
                    newArray(currentThread, code);
                    break;
                }
                case ByteCodes.ANEWARRAY: {
                    log.info("执行指令: anewarray，该指令功能为: 创建一个新的一维数组（数组元素为引用类型），并将该数组的引用压入操作数栈中");
                    aNewArray(currentThread, code);
                    break;
                }
                case ByteCodes.ARRAYLENGTH: {
                    log.info("执行指令: arraylength，该指令功能为: 取数组长度并压入栈顶");
                    arrayLength(currentThread, code);
                    break;
                }
                case ByteCodes.IALOAD: {
                    log.info("执行指令: iaload，该指令功能为: 从int类型数组中加载一个int类型数据至操作数栈顶");
                    iALoad(currentThread, code);
                    break;
                }
                case ByteCodes.LALOAD: {
                    log.info("执行指令: laload，该指令功能为: 从long类型数组中加载一个long类型数据至操作数栈顶");
                    lALoad(currentThread, code);
                    break;
                }
                case ByteCodes.FALOAD: {
                    log.info("执行指令: faload，该指令功能为: 从float类型数组中加载一个float类型数据至操作数栈顶");
                    fALoad(currentThread, code);
                    break;
                }
                case ByteCodes.DALOAD: {
                    log.info("执行指令: daload，该指令功能为: 从double类型数组中加载一个double类型数据至操作数栈顶");
                    dALoad(currentThread, code);
                    break;
                }
                case ByteCodes.AALOAD: {
                    log.info("执行指令: aaload，该指令功能为: 从引用类型数组中加载一个引用类型数据至操作数栈顶");
                    aALoad(currentThread, code);
                    break;
                }
                case ByteCodes.BALOAD: {
                    log.info("执行指令: baload，该指令功能为: 从boolean或byte类型数组中加载一个boolean或byte类型数据至操作数栈顶");
                    bALoad(currentThread, code);
                    break;
                }
                case ByteCodes.CALOAD: {
                    log.info("执行指令: caload，该指令功能为: 从char类型数组中加载一个char类型数据至操作数栈顶");
                    cALoad(currentThread, code);
                    break;
                }
                case ByteCodes.SALOAD: {
                    log.info("执行指令: saload，该指令功能为: 从short类型数组中加载一个short类型数据至操作数栈顶");
                    sALoad(currentThread, code);
                    break;
                }
                case ByteCodes.IASTORE: {
                    log.info("执行指令: iastore，该指令功能为: 从操作数栈中读取一个int类型数据并存入数组中");
                    iAStore(currentThread, code);
                    break;
                }
                case ByteCodes.LASTORE: {
                    log.info("执行指令: lastore，该指令功能为: 从操作数栈中读取一个long类型数据并存入数组中");
                    lAStore(currentThread, code);
                    break;
                }
                case ByteCodes.FASTORE: {
                    log.info("执行指令: fastore，该指令功能为: 从操作数栈中读取一个float类型数据并存入数组中");
                    fAStore(currentThread, code);
                    break;
                }
                case ByteCodes.DASTORE: {
                    log.info("执行指令: dastore，该指令功能为: 从操作数栈中读取一个double类型数据并存入数组中");
                    dAStore(currentThread, code);
                    break;
                }
                case ByteCodes.AASTORE: {
                    log.info("执行指令: aastore，该指令功能为: 从操作数栈中读取一个引用类型数据并存入数组中");
                    aAStore(currentThread, code);
                    break;
                }
                case ByteCodes.BASTORE: {
                    log.info("执行指令: bastore，该指令功能为: 从操作数栈中读取一个byte类型数据并存入数组中");
                    bAStore(currentThread, code);
                    break;
                }
                case ByteCodes.CASTORE: {
                    log.info("执行指令: castore，该指令功能为: 从操作数栈中读取一个char类型数据并存入数组中");
                    cAStore(currentThread, code);
                    break;
                }
                case ByteCodes.SASTORE: {
                    log.info("执行指令: sastore，该指令功能为: 从操作数栈中读取一个short类型数据并存入数组中");
                    sAStore(currentThread, code);
                    break;
                }
                default:
                    throw new Error("暂不支持该指令: " + opcode);
            }
        }
    }

    /**
     * 执行invokedymaic字节码指令
     * 该指令功能为: 调用动态方法
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void invokeDynamic(JavaThread currentThread, ByteCodeStream code) {
        // TODO: invokeDynamic字节码执行执行
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 方法信息
        MethodInfo method = code.getBelongMethod();

        int code1 = code.getU1Code();
        int code2 = code.getU1Code();
        int code3 = code.getU1Code();
        int code4 = code.getU1Code();

        int index = code1 << 8 | code2;

        Object object = new LambdaEngine(method, index).createObject();

        stack.push(new StackValue(BasicType.T_OBJECT, object));
    }


    /**
     * 执行sastore字节码指令
     * 该指令功能为: 从操作数栈中读取一个short类型数据并存入数组中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void sAStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中弹出栈顶元素（value）
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("sastore字节码指令: value 不匹配的数据类型: " + value.getType());
            throw new Error("sastore字节码指令: value 不匹配的数据类型" + value.getType());
        }
        short _value = (short) ((int) stack.pop().getData());

        // 从操作数栈中弹出栈顶元素（index）
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("sastore字节码指令: index 不匹配的数据类型" + index.getType());
            throw new Error("sastore字节码指令: index 不匹配的数据类型" + index.getType());
        }
        int _index = (int) stack.pop().getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("sastore字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_SHORT) {
            log.error("sastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("sastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        arrayRef.set(_index, _value);
    }

    /**
     * 执行castore字节码指令
     * 该指令功能为: 从操作数栈中读取一个char类型数据并存入数组中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void cAStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中弹出栈顶元素（value）
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("castore字节码指令: value 不匹配的数据类型: " + value.getType());
            throw new Error("castore字节码指令: value 不匹配的数据类型" + value.getType());
        }
        char _value = (char) ((int) stack.pop().getData());

        // 从操作数栈中弹出栈顶元素（index）
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("castore字节码指令: index 不匹配的数据类型" + index.getType());
            throw new Error("castore字节码指令: index 不匹配的数据类型" + index.getType());
        }
        int _index = (int) stack.pop().getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("castore字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_CHAR) {
            log.error("castore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("castore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        arrayRef.set(_index, _value);
    }

    /**
     * 执行bastore字节码指令
     * 该指令功能为: 从操作数栈中读取一个byte或boolean类型数据并存入数组中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void bAStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中弹出栈顶元素（value）
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("bastore字节码指令: value 不匹配的数据类型: " + value.getType());
            throw new Error("bastore字节码指令: value 不匹配的数据类型" + value.getType());
        }
        byte _value = (byte) ((int) stack.pop().getData());

        // 从操作数栈中弹出栈顶元素（index）
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("bastore字节码指令: index 不匹配的数据类型" + index.getType());
            throw new Error("bastore字节码指令: index 不匹配的数据类型" + index.getType());
        }
        int _index = (int) stack.pop().getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("bastore字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_BYTE && arrayRef.getType() != BasicType.T_BOOLEAN) {
            log.error("bastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("bastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        arrayRef.set(_index, _value);
    }

    /**
     * 执行aastore字节码指令
     * 该指令功能为: 从操作数栈中读取一个引用类型数据并存入数组中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aAStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中弹出栈顶元素（value）
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY) {
            log.error("aastore字节码指令: value 不匹配的数据类型: " + value.getType());
            throw new Error("aastore字节码指令: value 不匹配的数据类型" + value.getType());
        }
        Object _value = (Object) stack.pop().getData();

        // 从操作数栈中弹出栈顶元素（index）
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("aastore字节码指令: index 不匹配的数据类型" + index.getType());
            throw new Error("aastore字节码指令: index 不匹配的数据类型" + index.getType());
        }
        int _index = (int) stack.pop().getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("aastore字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_OBJECT) {
            log.error("aastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("aastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        arrayRef.set(_index, _value);
    }

    /**
     * 执行dastore字节码指令
     * 该指令功能为: 从操作数栈中读取一个double类型数据并存入数组中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dAStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中弹出栈顶元素（value）
        double _value = stack.popDouble();

        // 从操作数栈中弹出栈顶元素（index）
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("dastore字节码指令: index 不匹配的数据类型" + index.getType());
            throw new Error("dastore字节码指令: index 不匹配的数据类型" + index.getType());
        }
        int _index = (int) stack.pop().getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("dastore字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_DOUBLE) {
            log.error("dastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("dastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        arrayRef.set(_index, _value);
    }

    /**
     * 执行fastore字节码指令
     * 该指令功能为: 从操作数栈中读取一个float类型数据并存入数组中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fAStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中弹出栈顶元素（value）
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fastore字节码指令: value 不匹配的数据类型: " + value.getType());
            throw new Error("fastore字节码指令: value 不匹配的数据类型" + value.getType());
        }
        float _value = (float) stack.pop().getData();

        // 从操作数栈中弹出栈顶元素（index）
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("fastore字节码指令: index 不匹配的数据类型" + index.getType());
            throw new Error("fastore字节码指令: index 不匹配的数据类型" + index.getType());
        }
        int _index = (int) stack.pop().getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("fastore字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_FLOAT) {
            log.error("fastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("fastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        arrayRef.set(_index, _value);
    }

    /**
     * 执行lastore字节码指令
     * 该指令功能为: 从操作数栈中读取一个long类型数据并存入数组中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lAStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中弹出栈顶元素（value）
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lastore字节码指令: value 不匹配的数据类型: " + value.getType());
            throw new Error("lastore字节码指令: value 不匹配的数据类型" + value.getType());
        }
        long _value = (long) stack.pop().getData();

        // 从操作数栈中弹出栈顶元素（index）
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("lastore字节码指令: index 不匹配的数据类型" + index.getType());
            throw new Error("lastore字节码指令: index 不匹配的数据类型" + index.getType());
        }
        int _index = (int) stack.pop().getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("lastore字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_LONG) {
            log.error("lastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("lastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        arrayRef.set(_index, _value);
    }

    /**
     * 执行iastore字节码指令
     * 该指令功能为: 从操作数栈中读取一个int类型数据并存入数组中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iAStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中弹出栈顶元素（value）
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("iastore字节码指令: value 不匹配的数据类型: " + value.getType());
            throw new Error("iastore字节码指令: value 不匹配的数据类型" + value.getType());
        }
        int _value = (int) stack.pop().getData();

        // 从操作数栈中弹出栈顶元素（index）
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("iastore字节码指令: index 不匹配的数据类型" + index.getType());
            throw new Error("iastore字节码指令: index 不匹配的数据类型" + index.getType());
        }
        int _index = (int) stack.pop().getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("iastore字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_INT) {
            log.error("iastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("iastore字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        arrayRef.set(_index, _value);
    }

    /**
     * 执行saload字节码指令
     * 该指令功能为: 从short类型数组中加载一个short类型数据至操作数栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void sALoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出索引
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("saload字节码指令: index 不匹配的数据类型: " + index.getType());
            throw new Error("saload字节码指令: index 不匹配的数据类型: " + index.getType());
        }
        index = stack.pop();
        int _index = (int) index.getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("saload字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_SHORT) {
            log.error("saload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("saload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        // 取出数组对应索引的元素
        int value = (int) ((short) arrayRef.get(_index));

        // 将数组元素压入栈中
        stack.pushInt(value, frame);
    }

    /**
     * 执行caload字节码指令
     * 该指令功能为: 从char类型数组中加载一个char类型数据至操作数栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void cALoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出索引
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("caload字节码指令: index 不匹配的数据类型: " + index.getType());
            throw new Error("caload字节码指令: index 不匹配的数据类型: " + index.getType());
        }
        index = stack.pop();
        int _index = (int) index.getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("caload字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_CHAR) {
            log.error("caload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("caload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        // 取出数组对应索引的元素
        int value = (int) ((char) arrayRef.get(_index));

        // 将数组元素压入栈中
        stack.pushInt(value, frame);
    }

    /**
     * 执行baload字节码指令
     * 该指令功能为: 从boolean或byte类型数组中加载一个boolean或byte类型数据至操作数栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void bALoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出索引
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("baload字节码指令: index 不匹配的数据类型: " + index.getType());
            throw new Error("baload字节码指令: index 不匹配的数据类型: " + index.getType());
        }
        index = stack.pop();
        int _index = (int) index.getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("baload字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_BYTE && arrayRef.getType() != BasicType.T_BOOLEAN) {
            log.error("baload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("baload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        // 取出数组对应索引的元素
        int value = (int) ((byte) arrayRef.get(_index));

        // 将数组元素压入栈中
        stack.pushInt(value, frame);
    }

    /**
     * 执行aaload字节码指令
     * 该指令功能为: 从引用类型数组中加载一个引用类型数据至操作数栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aALoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出索引
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("aaload字节码指令: index 不匹配的数据类型: " + index.getType());
            throw new Error("aaload字节码指令: index 不匹配的数据类型: " + index.getType());
        }
        index = stack.pop();
        int _index = (int) index.getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("aaload字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_OBJECT) {
            log.error("aaload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("aaload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        // 取出数组对应索引的元素
        Object value = (Object) arrayRef.get(_index);

        // 将数组元素压入栈中
        stack.push(new StackValue(BasicType.T_OBJECT, value));
    }

    /**
     * 执行daload字节码指令
     * 该指令功能为: 从double类型数组中加载一个double类型数据至操作数栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dALoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出索引
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("daload字节码指令: index 不匹配的数据类型: " + index.getType());
            throw new Error("daload字节码指令: index 不匹配的数据类型: " + index.getType());
        }
        index = stack.pop();
        int _index = (int) index.getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("daload字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_DOUBLE) {
            log.error("daload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("daload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        // 取出数组对应索引的元素
        double value = (double) arrayRef.get(_index);

        // 将数组元素压入栈中
        stack.pushDouble(value);
    }

    /**
     * 执行faload字节码指令
     * 该指令功能为: 从float类型数组中加载一个float类型数据至操作数栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fALoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出索引
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("faload字节码指令: index 不匹配的数据类型: " + index.getType());
            throw new Error("faload字节码指令: index 不匹配的数据类型: " + index.getType());
        }
        index = stack.pop();
        int _index = (int) index.getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("faload字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_FLOAT) {
            log.error("faload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("faload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        // 取出数组对应索引的元素
        float value = (float) arrayRef.get(_index);

        // 将数组元素压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, value));
    }

    /**
     * 执行laload字节码指令
     * 该指令功能为: 从long类型数组中加载一个long类型数据至操作数栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lALoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出索引
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("laload字节码指令: index 不匹配的数据类型: " + index.getType());
            throw new Error("laload字节码指令: index 不匹配的数据类型: " + index.getType());
        }
        index = stack.pop();
        int _index = (int) index.getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("laload字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_LONG) {
            log.error("laload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("laload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        // 取出数组对应索引的元素
        long value = (long) arrayRef.get(_index);

        // 将数组元素压入栈中
        stack.push(new StackValue(BasicType.T_LONG, value));
    }

    /**
     * 执行iaload字节码指令
     * 该指令功能为: 从int类型数组中加载一个int类型数据至操作数栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void iALoad(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出索引
        StackValue index = stack.peek();
        // 检查操作数类型
        if (index.getType() != BasicType.T_INT) {
            log.error("iaload字节码指令: index 不匹配的数据类型: " + index.getType());
            throw new Error("iaload字节码指令: index 不匹配的数据类型: " + index.getType());
        }
        index = stack.pop();
        int _index = (int) index.getData();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("iaload字节码指令: arrayRef 为 null");
        }

        if (arrayRef.getType() != BasicType.T_INT) {
            log.error("iaload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
            throw new Error("iaload字节码指令: arrayRef 不匹配的数据类型: " + arrayRef.getType());
        }

        // 取出数组对应索引的元素
        int value = (int) arrayRef.get(_index);

        // 将数组元素压入栈中
        stack.pushInt(value, frame);
    }

    /**
     * 执行arraylength字节码指令
     * 该指令功能为: 取数组长度并压入栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void arrayLength(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 从操作数栈中取出数组引用
        ArrayOop arrayRef = stack.popArray(frame);
        if (arrayRef == null) {
            throw new NullPointerException("arraylength字节码指令: arrayRef 为 null");
        }

        stack.pushInt(arrayRef.getSize(), frame);
    }

    /**
     * 执行anewarray字节码指令
     * 该指令功能为: 创建一个新的一维数组（数组元素为引用类型），并将该数组的引用压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aNewArray(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成 引用类型在常量池中的索引
        int operate = code.getUnsignedShort();
        // 引用类型名称
        String referenceName = constantPool.getClassName(operate);

        // 从栈顶取出要创建数组的大小
        StackValue arrSize = stack.peek();
        if (arrSize.getType() != BasicType.T_INT) {
            log.error("newarray字节码指令: value1 不匹配的数据类型" + arrSize.getType());
            throw new Error("newarray字节码指令: value1 不匹配的数据类型" + arrSize.getType());
        }
        int _arrSize = (int) stack.pop().getData();

        ArrayOop arrayOop = new ArrayOop(BasicType.T_OBJECT, referenceName, _arrSize);

        stack.pushArray(arrayOop, frame);
    }

    /**
     * 执行newarray字节码指令
     * 该指令功能为: 创建一个新的一维数组（数组元素为基本类型），并将该数组的引用压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void newArray(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，一个无符号byte类型数据，标识要创建数组的元素类型
        int arrType = code.getU1Code();

        // 从栈顶取出要创建数组的大小
        StackValue arrSize = stack.peek();
        if (arrSize.getType() != BasicType.T_INT) {
            log.error("newarray字节码指令: value1 不匹配的数据类型" + arrSize.getType());
            throw new Error("newarray字节码指令: value1 不匹配的数据类型" + arrSize.getType());
        }
        int _arrSize = (int) stack.pop().getData();

        ArrayOop arrayOop = new ArrayOop(arrType, _arrSize);

        stack.pushArray(arrayOop, frame);
    }

    /**
     * 执行if_acmpne字节码指令
     * 该指令功能为: 比较栈顶两个引用类型数值，当前结果不相等时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifACmpNe(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_OBJECT) {
            log.error("if_acmpne字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("if_acmpne字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        Object _value2 = stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_OBJECT) {
            log.error("if_acmpne字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("if_acmpne字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        Object _value1 = stack.pop().getData();

        if (_value1 != _value2) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行if_acmpeq字节码指令
     * 该指令功能为: 比较栈顶两个引用类型数值，当前结果相等时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifACmpEq(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_OBJECT) {
            log.error("if_acmpeq字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("if_acmpeq字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        Object _value2 = stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_OBJECT) {
            log.error("if_acmpeq字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("if_acmpeq字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        Object _value1 = stack.pop().getData();

        if (_value1 == _value2) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行goto字节码指令
     * 该指令功能为: 无条件跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void jGoto(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
        // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
        code.inc(operand - 1 - 2);
    }

        /**
         * 执行if_icmpge字节码指令
         * 该指令功能为: 比较栈顶两个int类型数值大小，当前者大于后者时跳转
         * @param currentThread 当前线程
         * @param code 当前方法的指令段
         * */
    private static void ifICmpGe(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_INT) {
            log.error("if_icmpge字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("if_icmpge字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        int _value2 = (int) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT) {
            log.error("if_icmpge字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("if_icmpge字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        int _value1 = (int) stack.pop().getData();

        if (_value1 >= _value2) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行if_icmpgt字节码指令
     * 该指令功能为: 比较栈顶两个int类型数值大小，当前者大于后者时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifICmpGt(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_INT) {
            log.error("if_icmpgt字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("if_icmpgt字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        int _value2 = (int) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT) {
            log.error("if_icmpgt字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("if_icmpgt字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        int _value1 = (int) stack.pop().getData();

        if (_value1 > _value2) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行if_icmple字节码指令
     * 该指令功能为: 比较栈顶两个int类型数值大小，当前者小于等于后者时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifICmpLe(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_INT) {
            log.error("if_icmple字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("if_icmple字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        int _value2 = (int) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT) {
            log.error("if_icmple字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("if_icmple字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        int _value1 = (int) stack.pop().getData();

        if (_value1 <= _value2) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行if_icmplt字节码指令
     * 该指令功能为: 比较栈顶两个int类型数值大小，当前者小于后者时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifICmpLt(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_INT) {
            log.error("if_icmplt字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("if_icmplt字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        int _value2 = (int) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT) {
            log.error("if_icmplt字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("if_icmplt字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        int _value1 = (int) stack.pop().getData();

        if (_value1 < _value2) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行if_icmpne字节码指令
     * 该指令功能为: 比较栈顶两个int类型数值大小，当前者等于后者时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifICmpNe(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_INT) {
            log.error("if_icmpne字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("if_icmpne字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        int _value2 = (int) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT) {
            log.error("if_icmpne字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("if_icmpne字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        int _value1 = (int) stack.pop().getData();

        if (_value1 != _value2) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行if_icmpeq字节码指令
     * 该指令功能为: 比较栈顶两个int类型数值大小，当前者等于后者时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifICmpEq(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_INT) {
            log.error("if_icmpeq字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("if_icmpeq字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        int _value2 = (int) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_INT) {
            log.error("if_icmpeq字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("if_icmpeq字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        int _value1 = (int) stack.pop().getData();

        if (_value1 == _value2) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行ifgt字节码指令
     * 该指令功能为: 当栈顶int类型数值大于0时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifGt(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶元素
        StackValue value = stack.peek();
        if (value.getType() != BasicType.T_INT) {
            log.error("ifgt字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("ifgt字节码指令: value 不匹配的数据类型" + value.getType());
        }
        int _value = (int) stack.pop().getData();

        if (0 < _value) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行ifge字节码指令
     * 该指令功能为: 当栈顶int类型数值大于等于0时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifGe(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶元素
        StackValue value = stack.peek();
        if (value.getType() != BasicType.T_INT) {
            log.error("ifge字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("ifge字节码指令: value 不匹配的数据类型" + value.getType());
        }
        int _value = (int) stack.pop().getData();

        if (0 <= _value) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行ifle字节码指令
     * 该指令功能为: 当栈顶int类型数值小于等于0时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifLe(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶元素
        StackValue value = stack.peek();
        if (value.getType() != BasicType.T_INT) {
            log.error("ifle字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("ifle字节码指令: value 不匹配的数据类型" + value.getType());
        }
        int _value = (int) stack.pop().getData();

        if (0 >= _value) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行iflt字节码指令
     * 该指令功能为: 当栈顶int类型数值小于0时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifLt(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶元素
        StackValue value = stack.peek();
        if (value.getType() != BasicType.T_INT) {
            log.error("iflt字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("iflt字节码指令: value 不匹配的数据类型" + value.getType());
        }
        int _value = (int) stack.pop().getData();

        if (0 > _value) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行ifne字节码指令
     * 该指令功能为: 当栈顶int类型数值不等于0时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifNe(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶元素
        StackValue value = stack.peek();
        if (value.getType() != BasicType.T_INT) {
            log.error("ifne字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("ifne字节码指令: value 不匹配的数据类型" + value.getType());
        }
        int _value = (int) stack.pop().getData();

        if (0 != _value) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行ifeq字节码指令
     * 该指令功能为: 当栈顶int类型数值等于0时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifEq(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶元素
        StackValue value = stack.peek();
        if (value.getType() != BasicType.T_INT) {
            log.error("ifeq字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("ifeq字节码指令: value 不匹配的数据类型" + value.getType());
        }
        int _value = (int) stack.pop().getData();

        if (0 == _value) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行ifnonnull字节码指令
     * 该指令功能为: 当栈顶饮用类型数值不为null时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifNonNull(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶元素
        StackValue value = stack.peek();
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY) {
            log.error("ifnonnull字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("ifnonnull字节码指令: value 不匹配的数据类型" + value.getType());
        }
        Object _value = stack.pop().getData();

        if (null != _value) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行ifnull字节码指令
     * 该指令功能为: 当栈顶饮用类型数值为null时跳转
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void ifNull(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 取出操作数，两个无符号byte类型数据组合成一个16位有符号的分支偏移量
        int operand = code.getUnsignedShort();

        // 取出栈顶元素
        StackValue value = stack.peek();
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY) {
            log.error("ifnull字节码指令: value 不匹配的数据类型" + value.getType());
            throw new Error("ifnull字节码指令: value 不匹配的数据类型" + value.getType());
        }
        Object _value = stack.pop().getData();

        if (null == _value) {
            // 比较并跳转指令的操作数是跳转后的位置相对于当前指令开头的偏移量
            // 而当读取完当前指令的操作数之后，程序计数器已经在下一条指令的开头，所以需要将偏移量减去当前指令的长度（操作码长度1byte+操作数长度2byte）
            code.inc(operand - 1 - 2);
        }
    }

    /**
     * 执行dcmpg字节码指令
     * 该指令功能为: 比较栈顶两个double类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈），当其中一个值为NaN时，将1压入栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dCmpG(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_DOUBLE) {
            log.error("dcmpg字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("dcmpg字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        double _value2 = stack.popDouble();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_DOUBLE) {
            log.error("dcmpg字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("dcmpg字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        double _value1 = stack.popDouble();

        if (Double.isNaN(_value1) || Double.isNaN(_value2)) {
            stack.pushInt(1, frame);
        } else {
            if (_value1 > _value2) {
                stack.pushInt(1, frame);
            } else if (_value1 == _value2) {
                stack.pushInt(0, frame);
            } else {
                stack.pushInt(-1, frame);
            }
        }
    }

    /**
     * 执行dcmpl字节码指令
     * 该指令功能为: 比较栈顶两个double类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈），当其中一个值为NaN时，将-1压入栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void dCmpL(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_DOUBLE) {
            log.error("dcmpl字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("dcmpl字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        double _value2 = stack.popDouble();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_DOUBLE) {
            log.error("dcmpl字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("dcmpl字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        double _value1 = stack.popDouble();

        if (Double.isNaN(_value1) || Double.isNaN(_value2)) {
            stack.pushInt(-1, frame);
        } else {
            if (_value1 > _value2) {
                stack.pushInt(1, frame);
            } else if (_value1 == _value2) {
                stack.pushInt(0, frame);
            } else {
                stack.pushInt(-1, frame);
            }
        }
    }

    /**
     * 执行fcmpg字节码指令
     * 该指令功能为: 比较栈顶两个float类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈），当其中一个值为NaN时，将1压入栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fCmpG(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_FLOAT) {
            log.error("fcmpg字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("fcmpg字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        float _value2 = (float) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_FLOAT) {
            log.error("fcmpg字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("fcmpg字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        float _value1 = (float) stack.pop().getData();

        if (Float.isNaN(_value1) || Float.isNaN(_value2)) {
            stack.pushInt(1, frame);
        } else {
            if (_value1 > _value2) {
                stack.pushInt(1, frame);
            } else if (_value1 == _value2) {
                stack.pushInt(0, frame);
            } else {
                stack.pushInt(-1, frame);
            }
        }
    }

    /**
     * 执行fcmpl字节码指令
     * 该指令功能为: 比较栈顶两个float类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈），当其中一个值为NaN时，将-1压入栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void fCmpL(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_FLOAT) {
            log.error("fcmpl字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("fcmpl字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        float _value2 = (float) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_FLOAT) {
            log.error("fcmpl字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("fcmpl字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        float _value1 = (float) stack.pop().getData();

        if (Float.isNaN(_value1) || Float.isNaN(_value2)) {
            stack.pushInt(-1, frame);
        } else {
            if (_value1 > _value2) {
                stack.pushInt(1, frame);
            } else if (_value1 == _value2) {
                stack.pushInt(0, frame);
            } else {
                stack.pushInt(-1, frame);
            }
        }
    }

    /**
     * 执行lcmp字节码指令
     * 该指令功能为: 比较栈顶两个long类型数值大小，并将结果(1, 0, -1)压入栈顶（需要将long类型值出栈）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void lCmp(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        // 取出栈顶两个元素
        StackValue value2 = stack.peek();
        if (value2.getType() != BasicType.T_LONG) {
            log.error("lcmp字节码指令: value1 不匹配的数据类型" + value2.getType());
            throw new Error("lcmp字节码指令: value1 不匹配的数据类型" + value2.getType());
        }
        long _value2 = (long) stack.pop().getData();

        StackValue value1 = stack.peek();
        // 检查操作数类型
        if (value1.getType() != BasicType.T_LONG) {
            log.error("lcmp字节码指令: value2 不匹配的数据类型" + value1.getType());
            throw new Error("lcmp字节码指令: value2 不匹配的数据类型" + value1.getType());
        }
        long _value1 = (long) stack.pop().getData();

        if (_value1 > _value2) {
            stack.pushInt(1, frame);
        } else if (_value1 == _value2) {
            stack.pushInt(0, frame);
        } else {
            stack.pushInt(-1, frame);
        }
    }

    /**
     * 执行aconst_null字节码指令
     * 该指令功能为: 将一个null对象引用压入栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aConstNull(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();

        stack.pushNull(frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        stack.pushInt(ret, frame);
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2s字节码指令: 不匹配的数据类型");
            throw new Error("i2s字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

        // 强转
        short ret = (short) ((int) value.getData());
        // 将结果压入栈中
        // 在JVM中，小于4字节的数据类型都是以4字节进行存储的，即小于4字节的数据类型都是以int类型存储的
        stack.pushInt(ret, frame);
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2c字节码指令: 不匹配的数据类型");
            throw new Error("i2c字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

        // 强转
        char ret = (char) ((int) value.getData());
        // 将结果压入栈中
        // 在JVM中，小于4字节的数据类型都是以4字节进行存储的，即小于4字节的数据类型都是以int类型存储的
        stack.pushInt(ret, frame);
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2b字节码指令: 不匹配的数据类型");
            throw new Error("i2b字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

        // 强转
        byte ret = (byte) ((int) value.getData());
        // 将结果压入栈中
        // 在JVM中，小于4字节的数据类型都是以4字节进行存储的，即小于4字节的数据类型都是以int类型存储的
        stack.pushInt(ret, frame);
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
        float ret =  (float) stack.popDouble();
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_FLOAT, ret));
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
        long ret =  (long) stack.popDouble();
        // 将结果压入栈中
        stack.push(new StackValue(BasicType.T_LONG, ret));
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
        int ret =  (int) stack.popDouble();
        // 将结果压入栈中
        stack.pushInt(ret, frame);
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("f2d字节码指令: 不匹配的数据类型");
            throw new Error("f2d字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("f2l字节码指令: 不匹配的数据类型");
            throw new Error("f2l字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("f2i字节码指令: 不匹配的数据类型");
            throw new Error("f2i字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

        // 强转
        int ret = (int) ((float) value.getData());
        // 将结果压入栈中
        stack.pushInt(ret, frame);
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("l2d字节码指令: 不匹配的数据类型");
            throw new Error("l2d字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("l2f字节码指令: 不匹配的数据类型");
            throw new Error("l2f字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("l2i字节码指令: 不匹配的数据类型");
            throw new Error("l2i字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

        // 强转
        int ret = (int) ((long) value.getData());
        // 将结果压入栈中
        stack.pushInt(ret, frame);
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2d字节码指令: 不匹配的数据类型");
            throw new Error("i2d字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2f字节码指令: 不匹配的数据类型");
            throw new Error("i2f字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("i2l字节码指令: 不匹配的数据类型");
            throw new Error("i2l字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();

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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore_3字节码指令: 不匹配的数据类型");
            throw new Error("lstore_3字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore_2字节码指令: 不匹配的数据类型");
            throw new Error("lstore_2字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore_1字节码指令: 不匹配的数据类型");
            throw new Error("lstore_1字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore_0字节码指令: 不匹配的数据类型");
            throw new Error("lstore_0字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_LONG) {
            log.error("lstore字节码指令: 不匹配的数据类型");
            throw new Error("lstore字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore_0字节码指令: 不匹配的数据类型");
            throw new Error("fstore_0字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore_2字节码指令: 不匹配的数据类型");
            throw new Error("fstore_2字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore_3字节码指令: 不匹配的数据类型");
            throw new Error("fstore_3字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_FLOAT) {
            log.error("fstore字节码指令: 不匹配的数据类型");
            throw new Error("fstore字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        stack.pushInt(0, frame);
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
        stack.pushInt(1, frame);
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
        stack.pushInt(2, frame);
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
        stack.pushInt(3, frame);
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
        stack.pushInt(4, frame);
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
        stack.pushInt(5, frame);
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
        stack.pushInt(-1, frame);
    }

    /**
     * 执行aload_3字节码指令
     * 该指令功能为: 将局部变量表中索引为3的值（引用类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aLoad3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为3的值
        StackValue value = local.get(3);
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY) {
            log.error("aload_3字节码指令: 不匹配的数据类型");
            throw new Error("aload_3字节码指令: 不匹配的数据类型");
        }
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行aload_2字节码指令
     * 该指令功能为: 将局部变量表中索引为2的值（引用类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aLoad2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为2的值
        StackValue value = local.get(2);
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY) {
            log.error("aload_2字节码指令: 不匹配的数据类型");
            throw new Error("aload_2字节码指令: 不匹配的数据类型");
        }
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行aload_1字节码指令
     * 该指令功能为: 将局部变量表中索引为1的值（引用类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aLoad1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为1的值
        StackValue value = local.get(1);
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY) {
            log.error("aload_1字节码指令: 不匹配的数据类型");
            throw new Error("aload_1字节码指令: 不匹配的数据类型");
        }
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行aload_0字节码指令
     * 该指令功能为: 将局部变量表中索引为0的值（引用类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aLoad0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从局部变量表中索引为0的值
        StackValue value = local.get(0);
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY) {
            log.error("aload_0字节码指令: 不匹配的数据类型");
            throw new Error("aload_0字节码指令: 不匹配的数据类型");
        }
        // 压入操作数栈中
        stack.push(value);
    }

    /**
     * 执行aload字节码指令
     * 该指令功能为: 将局部变量表中对应索引（操作数中给出）位置的值（引用类型）压入操作数栈中
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aLoad(JavaThread currentThread, ByteCodeStream code) {
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
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY) {
            log.error("aload字节码指令: 不匹配的数据类型");
            throw new Error("aload字节码指令: 不匹配的数据类型");
        }
        // 压入操作数栈中
        stack.push(value);
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
     * 执行astore_3字节码指令
     * 该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中索引为3的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aStore3(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从操作数栈中弹出栈顶元素
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY && value.getType() != BasicType.T_ADDRESS) {
            log.error("astore_3字节码指令: 不匹配的数据类型: " + value.getType());
            throw new Error("astore_3字节码指令: 不匹配的数据类型: " + value.getType());
        }
        value = stack.pop();
        // 存入局部变量表对应索引的位置
        local.set(3, value);
    }

    /**
     * 执行astore_2字节码指令
     * 该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中索引为2的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aStore2(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从操作数栈中弹出栈顶元素
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY && value.getType() != BasicType.T_ADDRESS) {
            log.error("astore_2字节码指令: 不匹配的数据类型: " + value.getType());
            throw new Error("astore_2字节码指令: 不匹配的数据类型: " + value.getType());
        }
        value = stack.pop();
        // 存入局部变量表对应索引的位置
        local.set(2, value);
    }

    /**
     * 执行astore_1字节码指令
     * 该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中索引为1的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aStore1(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从操作数栈中弹出栈顶元素
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY && value.getType() != BasicType.T_ADDRESS) {
            log.error("astore_1字节码指令: 不匹配的数据类型: " + value.getType());
            throw new Error("astore_1字节码指令: 不匹配的数据类型: " + value.getType());
        }
        value = stack.pop();
        // 存入局部变量表对应索引的位置
        local.set(1, value);
    }

    /**
     * 执行astore_0字节码指令
     * 该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中索引为0的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aStore0(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();

        // 从操作数栈中弹出栈顶元素
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY && value.getType() != BasicType.T_ADDRESS) {
            log.error("astore_0字节码指令: 不匹配的数据类型: " + value.getType());
            throw new Error("astore_0字节码指令: 不匹配的数据类型: " + value.getType());
        }
        value = stack.pop();
        // 存入局部变量表对应索引的位置
        local.set(0, value);
    }

    /**
     * 执行astore字节码指令
     * 该指令功能为: 将操作数栈栈顶的引用类型的元素存入局部变量表中对应索引（索引通过操作数给出）的位置
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void aStore(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 局部变量表
        StackValueCollection local = frame.getLocalVariableTable();
        // 取出操作数，无符号byte类型整数，标识局部变量表中的索引号，占一个字节
        int index = code.getU1Code();

        // 从操作数栈中弹出栈顶元素
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_OBJECT && value.getType() != BasicType.T_ARRAY && value.getType() != BasicType.T_ADDRESS) {
            log.error("astore字节码指令: 不匹配的数据类型: " + value.getType());
            throw new Error("astore字节码指令: 不匹配的数据类型: " + value.getType());
        }
        value = stack.pop();
        // 存入局部变量表对应索引的位置
        local.set(index, value);
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore字节码指令: 不匹配的数据类型");
            throw new Error("istore字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore_0字节码指令: 不匹配的数据类型");
            throw new Error("istore_0字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore_1字节码指令: 不匹配的数据类型");
            throw new Error("istore_1字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore_2字节码指令: 不匹配的数据类型");
            throw new Error("istore_2字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        StackValue value = stack.peek();
        // 检查操作数类型
        if (value.getType() != BasicType.T_INT) {
            log.error("istore_3字节码指令: 不匹配的数据类型");
            throw new Error("istore_3字节码指令: 不匹配的数据类型");
        }
        value = stack.pop();
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
        stack.pushInt(value, frame);
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
        stack.pushInt(value, frame);
    }

    /**
     * 执行new字节码指令
     * 只分配内存
     *      Integer: 没有不带参数的构造函数
     *      String: 调用不带参数的构造函数，返回空
     * 该指令功能为: 创建一个对象，并将其引用压入栈顶
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void jNew(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 取出操作数，new指令的操作数是常量池的索引（Class），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassName(operand).replace('/', '.');

        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor();
            Object object = constructor.newInstance();

            if (object instanceof Throwable) {
                frame.getOperandStack().push(new StackValue(BasicType.T_Throwable, object));
            } else {
                frame.getOperandStack().push(new StackValue(BasicType.T_OBJECT, object));
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            /*
             * 如果没有无参构造函数，就传null，保证栈帧平衡
             * 后面调用到构造方法的时候进行判断处理
             */
            frame.getOperandStack().push(new StackValue(BasicType.T_OBJECT, null));
        }
    }

    /**
     * 执行invokeinterface字节码指令
     * 可以用两种方式实现:
     * 1.借助反射
     * 2.走自己的逻辑
     * 该指令功能为: 调用接口方法
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void invokeInterface(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 取出操作数，invokevirtual指令的操作数是常量池的索引（Methodref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand).replace('/', '.');
        String methodName = constantPool.getMethodName(operand);
        String descriptorName = constantPool.getFieldDescriptor(operand);

        log.info("执行方法: " + className + ":" + methodName + "#" + descriptorName);

        // 解析方法描述符
        DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
        descriptorStream.parseMethod();
        // 获取方法所有形参类型的Class对象按照形参顺序组成的数组
        Class<?>[] paramsClass = descriptorStream.getParamsType();

        // 从操作数栈中根据形类型 顺序 获取实参，即从操作数栈中弹出实参
        Object[] params = descriptorStream.getParamsVal(frame);

        // 从操作数栈中弹出 被调方法所属类的对象，即this指针
        Object obj = frame.getOperandStack().pop().getData();

        try {
            Method fun = obj.getClass().getMethod(methodName, paramsClass);

            /**
             * 处理：
             *  1.无返回值
             *  2.有返回值，需要将返回值压入操作数中（return字节码指令在从被调用方的操作数栈中取出返回值，压入调用方的操作数栈中）
             */
            if (BasicType.T_VOID == descriptorStream.getReturnElement().getType()) {
                fun.invoke(obj, params);
            } else {
                descriptorStream.pushReturnElement(fun.invoke(obj, params), frame);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如何找到一个类
     * 1.系统类（java开头）通过反射找到对应的类（反射原理就是从类加载器的缓存空间中找到对应类的klass模型然后通过klass模型再找到Class对象）
     * 2.自己加载的类通过在类加载器的缓存空间中找对应的klass（类对应的klass模型会存储在加载它的那个类加载器的缓存空间中，所以只需要在类加载器的缓存空间中通过类的全限定名找到对应的klass模型即可）
     *
     * 执行invokevirtual字节码指令
     * 调用虚方法
     * 1.public修饰
     * 2.protected修饰
     * 需要有被调用方法所属类的对象信息，即this指针
     * 该指令功能为: 调用实例方法，依据实例的类型进行分派，这个方法不能使实例初始化方法也不能是类或接口的初始化方法（静态初始化方法）
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void invokeVirtual(JavaThread currentThread, ByteCodeStream code) {
        log.info("执行指令: invokevirtual( java体系的借助反射实现，自己定义的类自己实现 )");

        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 取出操作数，invokevirtual指令的操作数是常量池的索引（Methodref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand).replace('/', '.');
        String methodName = constantPool.getMethodName(operand);
        String descriptorName = constantPool.getFieldDescriptor(operand);

        log.info("执行方法: " + className + ":" + methodName + "#" + descriptorName);

        // 系统加载的类走反射
        if (className.startsWith("java")) {
            // 解析方法描述符
            DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
            descriptorStream.parseMethod();
            // 获取方法所有形参类型的Class对象按照形参顺序组成的数组
            Class<?>[] paramsClass = descriptorStream.getParamsType();

            // 从操作数栈中根据形类型 顺序 获取实参，即从操作数栈中弹出实参
            Object[] params = descriptorStream.getParamsVal(frame);

            // 从操作数栈中弹出 被调方法所属类的对象，即this指针
            Object obj = frame.getOperandStack().pop().getData();

            try {
                Method fun = obj.getClass().getMethod(methodName, paramsClass);

                /**
                 * 处理：
                 *  1.无返回值
                 *  2.有返回值，需要将返回值压入操作数中（return字节码指令在从被调用方的操作数栈中取出返回值，压入调用方的操作数栈中）
                 */
                if (BasicType.T_VOID == descriptorStream.getReturnElement().getType()) {
                    fun.invoke(obj, params);
                } else {
                    descriptorStream.pushReturnElement(fun.invoke(obj, params), frame);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            // 在类加载器的缓存中查找是否有该类，没有就触发加载
            if (!BootClassLoader.isLoadedKlass(className)) {
                log.info("类[" + className + "]还未加载，开始加载");
                BootClassLoader.loadKlass(className);
            }

            // 在类加载器的缓存中找到对应的类
            InstanceKlass klass = BootClassLoader.findLoadedKlass(className);
            // 在对应的类中找到对应的方法
            MethodInfo method = JavaNativeInterface.getMethod(klass, methodName, descriptorName);
            if (null == method) {
                throw new Error("不存在的方法: " + methodName + "#" + descriptorName);
            }

            // 同一个方法重复调用问题: 该方法的程序计数器如果没有重置，会导致下一次调用是从上一次调用完之后的指令位置开始，导致出错
            // 调用某一个方法之前，需要重置该方法的程序计数器，避免上面所说的重复调用的问题
            CodeAttribute codeAttributeInfo = (CodeAttribute) method.getAttributes().get(CodeAttribute.JVM_ATTRIBUTE_Code);
            // 重置程序计数器
            codeAttributeInfo.getCode().reset();

            JavaNativeInterface.callMethod(method);
        }
    }

    /**
     * 执行invokestatic字节码指令
     * 该指令功能为: 调用静态方法，即static修饰的方法
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void invokeStatic(JavaThread currentThread, ByteCodeStream code) {
        log.info("执行指令: invokestatic( java体系的借助反射实现，自己定义的类自己实现 )");

        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 取出操作数，invokestatic指令的操作数是常量池的索引（Methodref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand).replace('/', '.');
        String methodName = constantPool.getMethodName(operand);
        String descriptorName = constantPool.getFieldDescriptor(operand);

        log.info("执行方法: " + className + ":" + methodName + "#" + descriptorName);

        // 系统加载的类走反射
        if (className.startsWith("java")) {
            // 解析方法描述符
            DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
            descriptorStream.parseMethod();
            // 获取方法所有形参类型的Class对象按照形参顺序组成的数组
            Class<?>[] paramsClass = descriptorStream.getParamsType();

            // 从操作数栈中根据形类型 顺序 获取实参，即从操作数栈中弹出实参
            Object[] params = descriptorStream.getParamsVal(frame);

            try {
                // 通过反射获取静态方法所属类的Class对象
                Class<?> clazz = Class.forName(className.replace('/', '.'));
                // 找到被调用的静态方法
                Method fun = clazz.getMethod(methodName, paramsClass);

                /**
                 * 处理：
                 *  1.无返回值
                 *  2.有返回值，需要将返回值压入操作数中（return字节码指令在从被调用方的操作数栈中取出返回值，压入调用方的操作数栈中）
                 */
                if (BasicType.T_VOID == descriptorStream.getReturnElement().getType()) {
                    fun.invoke(clazz, params);
                } else {
                    descriptorStream.pushReturnElement(fun.invoke(clazz, params), frame);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // 在类加载器的缓存中找到对应的类
            InstanceKlass klass = BootClassLoader.findLoadedKlass(className);
            // 在类加载器的缓存中查找是否有该类，没有就触发加载
            if (null == klass) {
                log.info("类[" + className + "]还未加载，开始加载");
                klass = BootClassLoader.loadKlass(className);
            }

            // 在对应的类中找到对应的方法
            MethodInfo method = JavaNativeInterface.getMethod(klass, methodName, descriptorName);
            if (null == method) {
                throw new Error("不存在的方法: " + methodName + "#" + descriptorName);
            }

            // 同一个方法重复调用问题: 该方法的程序计数器如果没有重置，会导致下一次调用是从上一次调用完之后的指令位置开始，导致出错
            // 调用某一个方法之前，需要重置该方法的程序计数器，避免上面所说的重复调用的问题
            CodeAttribute codeAttributeInfo = (CodeAttribute) method.getAttributes().get(CodeAttribute.JVM_ATTRIBUTE_Code);
            // 重置程序计数器
            codeAttributeInfo.getCode().reset();

            JavaNativeInterface.callStaticMethod(method);
        }
    }

    /**
     * 执行invokespecial字节码指令
     * 调用:
     * 1.构造方法
     * 2.私有方法
     * 3.父类方法（super）
     * 该指令功能为: 调用实例方法，专门用来调用父类方法、私有方法和实例初始化方法
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void invokeSpecial(JavaThread currentThread, ByteCodeStream code) {
        log.info("执行指令: invokespecial( java体系的借助反射实现，自己定义的类自己实现 )");

        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();
        // 取出操作数，invokestatic指令的操作数是常量池的索引（Methodref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand).replace('/', '.');
        String methodName = constantPool.getMethodName(operand);
        String descriptorName = constantPool.getFieldDescriptor(operand);

        log.info("执行方法: " + className + ":" + methodName + "#" + descriptorName);

        // 系统加载的类走反射
        if (className.startsWith("java")) {
            // 解析方法描述符
            DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
            descriptorStream.parseMethod();
            // 获取方法所有形参类型的Class对象按照形参顺序组成的数组
            Class<?>[] paramsClass = descriptorStream.getParamsType();

            // 从操作数栈中根据形类型 顺序 获取实参，即从操作数栈中弹出实参
            Object[] params = descriptorStream.getParamsVal(frame);

            /*
             * 1、为什么执行这步?
             *      因为非静态方法调用前都会压入对象指针，构建环境时给this赋值
             *      而java体系，我的设计中走的是反射机制，不需要手动给this赋值。所以需要手动完成出栈，保持堆栈平衡
             * 2、为什么要放在去参数后面？因为参数在对象引用（this）上面
             * --------
             * | 参数2 |
             * --------
             * | 参数1 |
             * --------
             * | this |
             * --------
             */
            StackValue stackValue = frame.getOperandStack().pop();
            Object object = stackValue.getObject();

            // 判断调用的是构造方法还是普通方法
            if (methodName.equals("<init>")) {
                if (null == object || object.equals("")) {
                    // 这里判空的原因
                    // 1.在jvm层面，new只是在堆中分配了内存，此时是没有java对象实体的。因为是用java模拟的，所以jvm返回给我们java的是没有映射实体的，为null
                    // 2.执行new字节码指令时，对于没有无参数构造函数的类创建对象的处理逻辑是 直接在栈中压入了null
                    log.info("\t new字节码指令未创建对象的，在这里创建");
                    try {
                        Class<?> clazz = Class.forName(className);
                        Constructor<?> constructor = clazz.getConstructor(paramsClass);
                        object = constructor.newInstance(params);
                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                /*
                * 1.所有类都会继承自Object类
                * 2.所有类的构造方法中都会先执行父类的构造方法
                * 3.示例代码中的类没有显示继承，所以继承自Object类
                * 4.执行Object类的构造方法时，在invokespecial字节码指令之前没有dup（和执行其他类的构造方法的字节码指令不一样），只是把this指针压栈，
                *   而上面弹出了这个this指针，所以这里peek会获取到空报异常
                * */
                if (!className.equals("java.lang.Object")) {
                    /*
                     * 执行new字节码指令时，会将对象引用(this)压入操作数栈中
                     * 然后再执行invokespecial字节码指令调用构造方法前，还会执行dup指令，将上面new压入栈的this指针复制一份再压入栈中（执行Object类的构造方法除外）
                     * 之后再压入执行构造方法的参数，最后才会执行invokespecial字节码指令
                     *
                     * 所以指向invokespecial字节码指令之前的操作数栈如下
                     * --------
                     * | 参数2 |
                     * --------
                     * | 参数1 |
                     * --------
                     * | this |
                     * --------
                     * | this |
                     * --------
                     *
                     * 执行invokespecial字节码指令前，pop出了实参值，然后pop出了一个this指针，操作数栈中还剩一个this指针
                     * 所以当执行完invokespecial字节码指令之后，真正创建了对象，就要将这个对象的引用赋值给栈顶那个this指针
                     * */
                    // 注意：这里应该是给栈帧顶部的StackValue赋值，而不是创建新的压栈
                    frame.getOperandStack().peek().setObject(object);
                }
            } else {
                // java体系，非构造方法
                throw new Error("java体系，非构造方法，未做处理");
            }
        } else {    // 非JVM系统加载的类，自己处理
            // 在类加载器的缓存中找到对应的类
            InstanceKlass klass = BootClassLoader.findLoadedKlass(className);
            // 在类加载器的缓存中查找是否有该类，没有就触发加载
            if (null == klass) {
                log.info("类[" + className + "]还未加载，开始加载");
                klass = BootClassLoader.loadKlass(className);
            }

            // 在对应的类中找到对应的方法
            MethodInfo method = JavaNativeInterface.getMethod(klass, methodName, descriptorName);
            if (null == method) {
                throw new Error("不存在的方法: " + methodName + "#" + descriptorName);
            }

            // 同一个方法重复调用问题: 该方法的程序计数器如果没有重置，会导致下一次调用是从上一次调用完之后的指令位置开始，导致出错
            // 调用某一个方法之前，需要重置该方法的程序计数器，避免上面所说的重复调用的问题
            CodeAttribute codeAttributeInfo = (CodeAttribute) method.getAttributes().get(CodeAttribute.JVM_ATTRIBUTE_Code);
            // 重置程序计数器
            codeAttributeInfo.getCode().reset();

            JavaNativeInterface.callMethod(method);
        }
    }

    /**
     * 执行getfield字节码指令
     * 该指令功能为: 获取对象的属性值并压入操作数栈
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void getField(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();

        // 取出操作数，getfield指令的操作数是常量池的索引（Fieldref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand).replace('/', '.');
        String fieldName = constantPool.getFieldName(operand);
        String descriptorName = constantPool.getFieldDescriptor(operand);

        // 解析字段描述符
        DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
        descriptorStream.parseFiled();

        // 从操作数栈中弹出 属性所属类的对象，即this指针
        Object obj = frame.getOperandStack().pop().getData();

        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getField(fieldName);

            // 如果字段不是静态字段的话，Field.get(Object)要传入反射类的对象（实例属性和对象绑定）。如果传null会报: java.lang.NullPointerException
            // 如果字段是静态字段的话，Field.get(Object)传入任何对象都是可以的（类属性和类绑定），包括null
            descriptorStream.pushField(field.get(obj), frame);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行putfield字节码指令
     * 该指令功能为: 设置对象的属性值
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void putField(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();

        // 取出操作数，putfield指令的操作数是常量池的索引（Fieldref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand).replace('/', '.');
        String fieldName = constantPool.getFieldName(operand);
        String descriptorName = constantPool.getFieldDescriptor(operand);

        // 解析字段描述符
        DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
        descriptorStream.parseFiled();

        // 从栈中取出对应类型的字段值
        Object value = descriptorStream.getFieldVal(frame);

        // 从操作数栈中弹出 属性所属类的对象，即this指针
        Object obj = frame.getOperandStack().pop().getData();

        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getField(fieldName);

            field.set(obj, value);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
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

        // 取出操作数，getstatic指令的操作数是常量池的索引（Fieldref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand).replace('/', '.');
        String fieldName = constantPool.getFieldName(operand);
        String descriptorName = constantPool.getFieldDescriptor(operand);

        // 解析字段描述符
        DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
        descriptorStream.parseFiled();

        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getField(fieldName);

            // 如果字段不是静态字段的话，Field.get(Object)要传入反射类的对象（实例属性和对象绑定）。如果传null会报: java.lang.NullPointerException
            // 如果字段是静态字段的话，Field.get(Object)传入任何对象都是可以的（类属性和类绑定），包括null
            descriptorStream.pushField(field.get(null), frame);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行putstatic字节码指令
     * 该指令功能为: 为指定类的静态字段赋值
     * @param currentThread 当前线程
     * @param code 当前方法的指令段
     * */
    private static void putStatic(JavaThread currentThread, ByteCodeStream code) {
        // 获取栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 运行时常量池（运行时常量池就是 klass）
        ConstantPool constantPool = code.getBelongMethod().getBelongKlass().getConstantPool();

        // 取出操作数，putstatic指令的操作数是常量池的索引（Fieldref），占两个字节
        int operand = code.getUnsignedShort();

        String className = constantPool.getClassNameByFieldInfo(operand).replace('/', '.');
        String fieldName = constantPool.getFieldName(operand);
        String descriptorName = constantPool.getFieldDescriptor(operand);

        // 解析字段描述符
        DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
        descriptorStream.parseFiled();
        // 从栈中取出对应类型的字段值
        Object value = descriptorStream.getFieldVal(frame);

        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getField(fieldName);

            field.set(null, value);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行areturn字节码指令
     * 该指令功能为: 从方法中返回引用类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者
     * @param currentThread 当前线程
     * */
    private static void aReturn(JavaThread currentThread) {
        // 获取当前栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 当前栈帧的操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 从当前栈帧的操作数栈中弹出返回值
        StackValue ret = stack.peek();
        if (ret.getType() != BasicType.T_OBJECT && ret.getType() != BasicType.T_ARRAY) {
            log.error("areturn字节码指令: 不匹配的数据类型: " + ret.getType());
            throw new Error("areturnn字节码指令: 不匹配的数据类型" + ret.getType());
        }
        ret = stack.pop();

        // pop出当前栈帧
        currentThread.getStack().pop();
        log.info("\t 剩余栈帧数量: " + currentThread.getStack().size());

        // 将返回值压入调用者栈帧
        ((JavaVFrame) currentThread.getStack().peek()).getOperandStack().push(ret);
    }

    /**
     * 执行dreturn字节码指令
     * 该指令功能为: 从方法中返回double类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者
     * @param currentThread 当前线程
     * */
    private static void dReturn(JavaThread currentThread) {
        // 获取当前栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 当前栈帧的操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 从当前栈帧的操作数栈中弹出返回值
        double ret = stack.popDouble();

        // pop出当前栈帧
        currentThread.getStack().pop();
        log.info("\t 剩余栈帧数量: " + currentThread.getStack().size());

        // 将返回值压入调用者栈帧
        ((JavaVFrame) currentThread.getStack().peek()).getOperandStack().pushDouble(ret);
    }

    /**
     * 执行freturn字节码指令
     * 该指令功能为: 从方法中返回float类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者
     * @param currentThread 当前线程
     * */
    private static void fReturn(JavaThread currentThread) {
        // 获取当前栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 当前栈帧的操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 从当前栈帧的操作数栈中弹出返回值
        StackValue ret = stack.peek();
        if (ret.getType() != BasicType.T_FLOAT) {
            log.error("freturn字节码指令: 不匹配的数据类型" + ret.getType());
            throw new Error("freturnn字节码指令: 不匹配的数据类型" + ret.getType());
        }
        ret = stack.pop();

        // pop出当前栈帧
        currentThread.getStack().pop();
        log.info("\t 剩余栈帧数量: " + currentThread.getStack().size());

        // 将返回值压入调用者栈帧
        ((JavaVFrame) currentThread.getStack().peek()).getOperandStack().push(ret);
    }

    /**
     * 执行lreturn字节码指令
     * 该指令功能为: 从方法中返回long类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者
     * @param currentThread 当前线程
     * */
    private static void lReturn(JavaThread currentThread) {
        // 获取当前栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 当前栈帧的操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 从当前栈帧的操作数栈中弹出返回值
        StackValue ret = stack.peek();
        if (ret.getType() != BasicType.T_LONG) {
            log.error("lreturn字节码指令: 不匹配的数据类型" + ret.getType());
            throw new Error("lreturnn字节码指令: 不匹配的数据类型" + ret.getType());
        }
        ret = stack.pop();

        // pop出当前栈帧
        currentThread.getStack().pop();
        log.info("\t 剩余栈帧数量: " + currentThread.getStack().size());

        // 将返回值压入调用者栈帧
        ((JavaVFrame) currentThread.getStack().peek()).getOperandStack().push(ret);
    }

    /**
     * 执行ireturn字节码指令
     * 该指令功能为: 从方法中返回int类型数据，恢复调用者的栈帧，并且把程序的控制权交回调用者
     * @param currentThread 当前线程
     * */
    private static void iReturn(JavaThread currentThread) {
        // 获取当前栈帧
        JavaVFrame frame = (JavaVFrame) currentThread.getStack().peek();
        // 当前栈帧的操作数栈
        StackValueCollection stack = frame.getOperandStack();
        // 从当前栈帧的操作数栈中弹出返回值
        StackValue ret = stack.peek();
        if (ret.getType() != BasicType.T_INT) {
            log.error("ireturn字节码指令: 不匹配的数据类型" + ret.getType());
            throw new Error("ireturnn字节码指令: 不匹配的数据类型" + ret.getType());
        }
        ret = stack.pop();

        // pop出当前栈帧
        currentThread.getStack().pop();
        log.info("\t 剩余栈帧数量: " + currentThread.getStack().size());

        // 将返回值压入调用者栈帧
        ((JavaVFrame) currentThread.getStack().peek()).getOperandStack().push(ret);
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
                stack.pushInt(content, frame);
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
