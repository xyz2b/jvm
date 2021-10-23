package org.xyz.jvm.jdk.classes.java.lang;

import org.xyz.jvm.jdk.classes.java.io.FileOutputStream;
import org.xyz.jvm.jdk.classes.java.io.PrintStream;
import org.xyz.jvm.jdk.classes.java.io.FileDescriptor;

public final class System {
    public static final PrintStream out;

    static {
        out = newPrintStream(new FileOutputStream(FileDescriptor.out));
    }

    private static void initializeSystemClass() {

    }

    private static PrintStream newPrintStream(FileOutputStream var0) {
        return new PrintStream(var0);
    }
}
