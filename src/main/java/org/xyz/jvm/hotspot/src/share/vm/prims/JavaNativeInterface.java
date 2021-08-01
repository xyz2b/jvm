package org.xyz.jvm.hotspot.src.share.vm.prims;

import lombok.extern.slf4j.Slf4j;
import org.xyz.jvm.hotspot.src.share.vm.intepreter.BytecodeInterpreter;
import org.xyz.jvm.hotspot.src.share.vm.oops.InstanceKlass;
import org.xyz.jvm.hotspot.src.share.vm.oops.MethodInfo;
import org.xyz.jvm.hotspot.src.share.vm.oops.attribute.CodeAttribute;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaThread;
import org.xyz.jvm.hotspot.src.share.vm.runtime.Threads;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaVFrame;

import java.util.List;

@Slf4j
public class JavaNativeInterface {
    /**
     * 根据方法名和方法描述符在Klass模型中找到对应的方法
     * @param klass Klass模型
     * @param methodName 方法名
     * @param descriptorName 方法描述符
     * @return 找到的方法信息，没找到返回null
     * */
    public static MethodInfo getMethod(InstanceKlass klass, String methodName, String descriptorName) {
        List<MethodInfo> methodInfos = klass.getMethods();

        for (MethodInfo methodInfo: methodInfos) {
            String tmpMethodName = klass.getConstantPool().getUtf8(methodInfo.getNameIndex());
            String tmpDescriptorName = klass.getConstantPool().getUtf8(methodInfo.getDescriptorIndex());

            if (tmpMethodName.equals(methodName) && tmpDescriptorName.equals(descriptorName)) {
                log.info("找到了方法: " + methodName + "#" + descriptorName);
                return methodInfo;
            }
        }

        log.error("没有找到方法: " + methodName + "#" + descriptorName);
        return null;
    }

    /**
     * 调用静态方法
     * @param method 被调用的方法信息
     * */
    public static void callStaticMethod(MethodInfo method) {
        // 调用方栈帧
        JavaVFrame callerFrame = null;
        // 获取当前线程
        JavaThread currentThread = Threads.currentThread();

        if (!method.getAccessFlags().isStatic()) {
            throw new Error("只能调用静态方法");
        }

        // 判断是否有参数
        if (0 != method.getDescriptor().getMethodParamsSize()) {
            // 这个判断是为了过滤调用main方法的情况，因为调用main方法时线程虚拟机栈中还是空的，没有任何栈帧，main方法的参数由JVM自动传入
            if (0 != currentThread.getStack().size()) {
                // 实参在调用方的操作数栈中，需要将实参存到被调用方的局部变量表中，而不是压到被调用方的操作数栈中
                // 此时调用方的方法栈帧在当前线程虚拟机栈的栈顶，因为被调用方的方法栈帧还未压入虚拟机栈
                log.info("\t 获取调用方的方法栈帧");
                // 获取调用方的方法栈帧
                callerFrame = (JavaVFrame) currentThread.getStack().peek();
            }
        } else {    // 无参数
            log.info("\t 方法 [ " + method.getMethodName() + " ] 没有参数");
        }

        // 获取当前方法的Code属性
        CodeAttribute codeAttributeInfo = (CodeAttribute) method.getAttributes().get(CodeAttribute.JVM_ATTRIBUTE_Code);

        // 创建被调用方方法栈帧
        JavaVFrame calleeFrame = new JavaVFrame(codeAttributeInfo.getMaxLocals(), method);

        // 使用调用方操作数栈中的实参给被调用方局部变量中的形参赋值
        // 调用方操作数栈栈顶的实参 对应 被调用方局部变量索引最大的形参
        // 同时非静态方法第一个形参是this指针，注意赋值
        if (null != callerFrame) {
            // 由于是静态方法，所以参数在被调用方局部变量表中的位置是从0开始
            for (int i = method.getDescriptor().getMethodParamsSize() - 1; i >= 0; i--) {
                calleeFrame.getLocalVariableTable().set(i, callerFrame.getOperandStack().pop());
            }
        }

        // 将栈帧压入当前线程的操作数栈
        currentThread.getStack().push(calleeFrame);

        log.info("第 " + currentThread.getStack().size() + " 个栈帧");

        // 执行方法的任务交给字节码解释器
        BytecodeInterpreter.run(currentThread, method);

    }

    /**
     * 调用方法（静态、非静态）
     * @param method 被调用的方法信息
     * */
    public static void callMethod(MethodInfo method) {
        // 调用方栈帧
        JavaVFrame callerFrame = null;
        // 获取当前线程
        JavaThread currentThread = Threads.currentThread();

        /*
         * 需要获取上一个方法栈帧的情况：
         * 1.非静态方法。因为需要给this赋值
         * 2.需要传参
         */
        if (!method.getAccessFlags().isStatic() || 0 != method.getDescriptor().getMethodParamsSize()) {
            // 这个判断是为了过滤调用main方法的情况，因为调用main方法时线程虚拟机栈中还是空的，没有任何栈帧，main方法的参数由JVM自动传入
            if (0 != currentThread.getStack().size()) {
                // 实参在调用方的操作数栈中，需要将实参存到被调用方的局部变量表中，而不是压到被调用方的操作数栈中
                // 此时调用方的方法栈帧在当前线程虚拟机栈的栈顶，因为被调用方的方法栈帧还未压入虚拟机栈
                log.info("\t 获取调用方的方法栈帧");
                // 获取调用方的方法栈帧
                callerFrame = (JavaVFrame) currentThread.getStack().peek();
            }
        } else {    // 无参数
            log.info("\t 方法 [ " + method.getMethodName() + " ] 没有参数");
        }

        // 获取当前方法的Code属性
        CodeAttribute codeAttributeInfo = (CodeAttribute) method.getAttributes().get(CodeAttribute.JVM_ATTRIBUTE_Code);

        // 创建被调用方方法栈帧
        JavaVFrame calleeFrame = new JavaVFrame(codeAttributeInfo.getMaxLocals(), method);

        // 使用调用方操作数栈中的实参给被调用方局部变量中的形参赋值
        // 调用方操作数栈栈顶的实参 对应 被调用方局部变量索引最大的形参
        // 同时非静态方法第一个形参是this指针，注意赋值
        if (null != callerFrame) {
            if (method.getAccessFlags().isStatic()) {
                // 由于是静态方法，所以参数在被调用方局部变量表中的位置是从0开始
                for (int i = method.getDescriptor().getMethodParamsSize() - 1; i >= 0; i--) {
                    calleeFrame.getLocalVariableTable().set(i, callerFrame.getOperandStack().pop());
                }
            } else {
                // 由于是非静态方法，被调用方局部变量表索引为0的位置是this，所以参数的位置是从1开始
                for (int i = method.getDescriptor().getMethodParamsSize(); i > 0; i--) {
                    calleeFrame.getLocalVariableTable().set(i, callerFrame.getOperandStack().pop());
                }

                // 给this赋值
                calleeFrame.getLocalVariableTable().set(0, callerFrame.getOperandStack().pop());
            }
        }

        // 将栈帧压入当前线程的操作数栈
        currentThread.getStack().push(calleeFrame);

        log.info("第 " + currentThread.getStack().size() + " 个栈帧");

        // 执行方法的任务交给字节码解释器
        BytecodeInterpreter.run(currentThread, method);

    }
}
