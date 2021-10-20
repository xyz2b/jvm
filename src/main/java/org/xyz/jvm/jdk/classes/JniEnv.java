package org.xyz.jvm.jdk.classes;

public class JniEnv {
    public static native Handle loadClassFile(String className);
    public static native Handle getMethodId(Handle klassHandle, String methodName, String descriptorName);
    public static native void callStaticVoidMethod(Handle klassHandle, Handle methodHandle);
}
