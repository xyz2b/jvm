package org.xyz.jvm.hotspot.src.share.vm.classfile;

import org.xyz.jvm.jdk.classes.Handle;

public class AppClassLoader {
    public static native Handle loadKlass(String className);
}
