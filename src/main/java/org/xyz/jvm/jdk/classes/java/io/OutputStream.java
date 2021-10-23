package org.xyz.jvm.jdk.classes.java.io;

public abstract class OutputStream {
    public abstract void write(int b);

    public void write(byte b[]) {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int offset, int length) {
        for(int i = 0; i < length; i++) {
            write(b[offset +i ]);
        }
    }

    public void flush() {}

    public void close() {}
}
