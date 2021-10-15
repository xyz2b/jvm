package org.xyz.jvm.jdk.classes.sun.misc;

import org.xyz.jvm.jdk.classes.Handle;

public class Unsafe {
    public static native Handle allocateMemory(long bytes);
}
