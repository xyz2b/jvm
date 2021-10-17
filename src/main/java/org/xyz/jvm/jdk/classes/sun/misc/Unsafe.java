package org.xyz.jvm.jdk.classes.sun.misc;

import org.xyz.jvm.jdk.classes.Handle;

public class Unsafe {
    public static native long allocateMemory(long bytes);
    public static native Handle allocateObject();
}
