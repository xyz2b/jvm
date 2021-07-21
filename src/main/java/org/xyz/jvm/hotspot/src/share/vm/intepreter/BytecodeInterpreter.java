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
                    log.info("执行指令: LDC，该指令功能为: 从运行时常量池中提取数据并压入操作数栈");
                    ldc(currentThread, code);
                    break;
                }
                case ByteCodes.RETURN:  {
                    log.info("执行指令: RETURN，该指令功能为: 从方法中返回void，恢复调用者的栈帧，并且把程序的控制权交回调用者");
                    jReturn(currentThread);
                    break;
                }
                case ByteCodes.GETSTATIC: {
                    log.info("执行指令: GETSTATIC，该指令功能为: 获取类的静态字段值并压入操作数栈");
                    getStatic(currentThread, code);
                    break;
                }
                case ByteCodes.INVOKEVIRTUAL: {
                    log.info("执行指令: INVOKEVIRTUAL，该指令功能为: 调用实例方法，依据实例的类型进行分派，这个方法不能使实例初始化方法也不能是类或接口的初始化方法（静态初始化方法）");
                    invokeVirtual(currentThread, code);
                    break;
                }
            }
        }
    }

    /**
     * 如何找到一个类
     * 1.系统类（java开头）通过反射找到对应的类（反射原理就是从类加载器的缓存空间中找到对应类的klass模型然后通过klass模型再找到Class对象）
     * 2.自己加载的类通过在类加载器的缓存空间中找对应的klass（类对应的klass模型会存储在加载它的那个类加载器的缓存空间中，所以只需要在类加载器的缓存空间中通过类的全限定名找到对应的klass模型即可）
     *
     * 执行INVOKEVIRTUAL字节码指令
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
        } else {    // 自己加载的类自己处理

        }
    }

    /**
     * 执行GETSTATIC字节码指令
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
        } else {    // 自己加载的类自己处理

        }
    }

    /**
     * 执行RETURN字节码指令
     * @param currentThread 当前线程
     * */
    private static void jReturn(JavaThread currentThread) {
        // pop出栈帧
        currentThread.getStack().pop();
        log.info("\t 剩余栈帧数量: " + currentThread.getStack().size());
    }

    /**
     * 执行LDC字节码指令
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
                // 从类加载器的缓存中获取Klass信息
                break;
            }
        }

    }
}
