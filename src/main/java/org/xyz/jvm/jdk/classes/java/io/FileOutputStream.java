package org.xyz.jvm.jdk.classes.java.io;

public class FileOutputStream extends OutputStream {
    private final FileDescriptor fd;
    private final boolean append;

    public FileOutputStream(FileDescriptor fd) {
        this.fd = fd;
        this.append = false;
    }

    private native void write(int b, boolean append);
    @Override
    public void write(int b) {
        write(b, append);
    }

    private native void writeBytes(byte b[], int off, int len, boolean append);
    public void write(byte[] b) {
        writeBytes(b, 0, b.length, append);
    }
    public void write(byte[] b, int off, int len) {
        writeBytes(b, off, len, append);
    }
}
