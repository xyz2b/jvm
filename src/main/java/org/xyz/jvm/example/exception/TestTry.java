package org.xyz.jvm.example.exception;

public class TestTry {
    public static void main(String[] args) {
        try {
            int i = 1 / 0;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
