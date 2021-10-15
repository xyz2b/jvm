package org.xyz.jvm.example.jni;

public class JavaThread {
    public static void main(String[] args) {
        System.loadLibrary("jni");
        new JavaThread().start();

        // start-->start0-->通过JNI调用java的run方法
    }

    public void start() {
        start0();
    }

    public native void start0();

    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }
}
