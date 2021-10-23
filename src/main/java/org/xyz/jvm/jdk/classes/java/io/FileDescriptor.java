package org.xyz.jvm.jdk.classes.java.io;

public class FileDescriptor {
    private int fd;

    public FileDescriptor(int fd) { this.fd = fd; }

    public static final FileDescriptor out = new FileDescriptor(1);
}
