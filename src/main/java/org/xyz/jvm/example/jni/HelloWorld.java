package org.xyz.jvm.example.jni;

public class HelloWorld {
    // 测试读写静态属性
    public static int val = 10;
    public static native void showVal();
    public static native int getVal();
    public static native int setVal(int newValue);

    // 测试读写非静态属性
    public int val2 = 11;
    public native void showVal2();
    public native int getVal2();
    public native int setVal2(int newValue);

    // 使用JNI创建thread对象
    public static native JavaThread createThread();

    // 使用JNI调用thread的run方法
    public static native void threadRunFast(JavaThread thread);

    // 使用JNI抛出异常
    public native void throwException();

    public static void main(String[] args) {
//        System.out.println(System.getProperty("java.library.path"));

        System.loadLibrary("jni");
//        threadRunFast(new JavaThread());
//        System.out.printf("%d\n", getVal());
//        showVal();
//        setVal(2);
//        System.out.printf("%d\n", getVal());
//        showVal();
//
//        HelloWorld helloWorld = new HelloWorld();
//        helloWorld.showVal2();
//        helloWorld.setVal2(3);
//        System.out.printf("%d\n", helloWorld.getVal2());

//        createThread().start();

        new HelloWorld().throwException();
    }
}