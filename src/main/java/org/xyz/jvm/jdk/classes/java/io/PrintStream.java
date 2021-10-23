package org.xyz.jvm.jdk.classes.java.io;

import java.io.IOException;
import java.io.InterruptedIOException;

public class PrintStream extends FilterOutputStream {
    public PrintStream(OutputStream var1) {
        super(var1);
    }

    public void write(int var1) {
        this.out.write(var1);
    }

    public void write(byte[] var1) {
        this.out.write(var1);
    }

    public void print(int var1) {
        this.write(var1);
    }

}
