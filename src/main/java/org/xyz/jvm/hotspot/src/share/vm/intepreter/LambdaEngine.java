package org.xyz.jvm.hotspot.src.share.vm.intepreter;

import org.xyz.jvm.hotspot.src.share.vm.classfile.DescriptorStream;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;
import org.xyz.jvm.hotspot.src.share.vm.oops.ConstantPool;
import org.xyz.jvm.hotspot.src.share.vm.oops.MethodInfo;
import org.xyz.jvm.hotspot.src.share.vm.oops.attribute.BootstrapMethods;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LambdaEngine {
    private MethodInfo method;
    private int index;

    public LambdaEngine(MethodInfo method, int index) {
        this.method = method;
        this.index = index;
    }

    public Object createObject() {
        ConstantPool constantPool = method.getBelongKlass().getConstantPool();

        // 获取常量池项JVM_CONSTANT_InvokeDynamic中的信息
        // run
        String sourceMethodName = constantPool.getMethodNameByInvokeDynamicInfo(index);
        // ()Lorg/xyz/jvm/example/lambda/CustomLambda;
        // 主要用到返回值类型
        String descriptorName = constantPool.getMethodDescriptorByInvokeDynamicInfo(index);
        DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
        descriptorStream.parseMethod();

        // BootstrapMethods属性中的index，即当前lambda表达式对应的是第几个BootstrapMethod
        int bootstrapMethodIndex = constantPool.getBootstrapMethodIndexByInvokeDynamicInfo(index);
        BootstrapMethods bootstrapMethods = (BootstrapMethods) method.getBelongKlass().getAttributes().get(Attribute.JVM_ATTRIBUTE_BootstrapMethods);
        BootstrapMethods.BootstrapMethod bootstrapMethod = bootstrapMethods.getBootstrapMethods().get(bootstrapMethodIndex);

        // 常量池中JVM_CONSTANT_MethodHandle常量池项的索引
        int methodHandleIndex = bootstrapMethod.getBootstrapArguments().get(1);
        // <org/xyz/jvm/example/lambda/TestLambda.lambda$main$0> lambda生成的方法所属的类: org/xyz/jvm/example/lambda/TestLambda
        // 因为 lambda 表达式是在 org/xyz/jvm/example/lambda/TestLambda 类中编写的，所以生成的 lambda 方法应该在该类中
        String className = constantPool.getMethodClassNameByMethodHandleInfo(methodHandleIndex);
        // <org/xyz/jvm/example/lambda/TestLambda.lambda$main$0> lambda生成的方法名称: lambda$main$0
        String lambdaMethodName = constantPool.getMethodNameByMethodHandleInfo(methodHandleIndex);
        // <org/xyz/jvm/example/lambda/TestLambda.lambda$main$0> lambda生成的方法的描述符: ()V
        String lambdaDescriptor = constantPool.getMethodDescriptorByMethodHandleInfo(methodHandleIndex);
        DescriptorStream lambdaDescriptorStream = new DescriptorStream(lambdaDescriptor);
        lambdaDescriptorStream.parseMethod();
        Class<?>[] paramsClass = lambdaDescriptorStream.getParamsType();

        try {
            // invokedymaic指令的返回值类型 org/xyz/jvm/example/lambda/CustomLambda
            Class returnClazz = Class.forName(descriptorStream.getReturnElement().getTypeDesc().replace("/", "."));
            // 调用方类型，在 org/xyz/jvm/example/lambda/TestLambda 中调用的lambda表达式
            Class callerClazz = Class.forName(className.replace("/", "."));

            // 获取调用者org/xyz/jvm/example/lambda/TestLambda的MethodHandles.Lookup
            MethodHandles.Lookup lookup = getLookup(callerClazz);

            // 从调用方的类中 org/xyz/jvm/example/lambda/TestLambda 找到 lambda生成的方法 lambda$main$0
            Method method = callerClazz.getDeclaredMethod(lambdaMethodName, paramsClass);

            // 获取被调用方法的MethodHandle
            MethodHandle unreflect = lookup.unreflect(method);

            // 获取被调用方法的MethodType
            MethodType type = unreflect.type();

            // 获取被调用方法的返回值的MethodType
            MethodType factorType = MethodType.methodType(returnClazz);

            // 这个调用的方法，就是在 BootstrapMethods 属性中的 Bootstrap方法，传入的参数也包含 BootstrapMethods属性中的参数
            // lookup: 调用者org/xyz/jvm/example/lambda/TestLambda的lookup
            // sourceMethodName: run
            // factorType: invokedymaic指令的返回值类型 org/xyz/jvm/example/lambda/CustomLambda 的 MethodType
            // type: lambda生成的方法 org/xyz/jvm/example/lambda/TestLambda.lambda$main$0 的 MethodType
            // unreflect: lambda生成的方法 org/xyz/jvm/example/lambda/TestLambda.lambda$main$0 的 MethodHandle
            CallSite callSite = LambdaMetafactory.metafactory(lookup, sourceMethodName, factorType, type, unreflect, type);

            // 这里的MethodHandle就是下面生成的类中构造方法
            MethodHandle target = callSite.getTarget();

            return target.invoke();
            /*
            * 最后生成的类
            * package org.xyz.jvm.example.lambda;
            *
            * final class TestLambda$$Lambda$1 implements CustomLambda {
            *   private TestLambda$$Lambda$1() {}
            *
            *   // 这里将run方法和lambda生成的方法对应起来了
            *   public void run() { TestLambda.lambda$main$0(); }
            * }
            * */

            /*
            * 调用方法的方式
            * 1.逗号表达式
            * 2.反射
            * 3.MethodHandle
            *   获取MethodHandles.lookup()获取调用类的MethodHandles.Lookup
            *   获取被调用方法的MethodHandle以及MethodType
            *   获取被调用方法的返回值的MethodType
            *   LambdaMetafactory.metafactory()获取CallSite
            *   callSite.getTarget()
            *
            * */


        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    // MethodHandles的lookup方法是不能够接收参数的，即不能指定获取哪个类的Lookup，所以通过反射实现一个能够接收参数的lookup方法
    private MethodHandles.Lookup getLookup(Class callerClass) throws Exception {
        // 获取 MethodHandles.Lookup Class对象
        Class<?> c = Class.forName(MethodHandles.Lookup.class.getName());

        // 从 MethodHandles.Lookup Class 对象中获取 MethodHandles.Lookup 的 IMPL_LOOKUP 属性
        Field implLookup = c.getDeclaredField("IMPL_LOOKUP");
        implLookup.setAccessible(true);

        Object o = implLookup.get(MethodHandles.Lookup.class);

        // 获取 MethodHandles.Lookup 的 in 方法，因为该方法才可以接收参数，来获取指定类的Lookup
        Method method = c.getDeclaredMethod("in", Class.class);

        return (MethodHandles.Lookup) method.invoke(o, callerClass);
    }
}
