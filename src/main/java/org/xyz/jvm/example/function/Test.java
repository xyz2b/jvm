package org.xyz.jvm.example.function;

public class Test {
    public static int add(int a, int b) {
        int c = a + b;
        int d = c + 9;
        return d;
    }

    public static void main(String[] args) {
        System.out.println(add(5, 8));
    }
}
