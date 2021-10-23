package org.xyz.jvm.jdk.classes.java.io;

public class FilterOutputStream extends OutputStream {
    protected OutputStream out;

    public FilterOutputStream(OutputStream var1) {
        this.out = var1;
    }

    public void write(int var1) {
        this.out.write(var1);
    }

    public void write(byte[] var1) {
        this.write(var1, 0, var1.length);
    }

    public void write(byte[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            this.write(var1[var2 + var4]);
        }
    }
}
