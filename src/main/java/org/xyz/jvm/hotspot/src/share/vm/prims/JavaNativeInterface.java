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

    public static void callStaticMethod(MethodInfo method) {
        // 获取当前线程
        JavaThread currentThread = Threads.currentThread();

        if (!method.getAccessFlags().isStatic()) {
            throw new Error("只能调用静态方法");
        }

        // 获取当前方法的Code属性
        CodeAttribute codeAttributeInfo = (CodeAttribute) method.getAttributes().get(CodeAttribute.JVM_ATTRIBUTE_Code);

        // 创建栈帧
        JavaVFrame frame = new JavaVFrame(codeAttributeInfo.getMaxLocals(), method);

        // 将栈帧压入当前线程的操作数栈
        currentThread.getStack().push(frame);

        log.info("第 " + currentThread.getStack().size() + " 个栈帧");

        // 执行方法的任务交给字节码解释器
        BytecodeInterpreter.run(currentThread, method);

    }
}
