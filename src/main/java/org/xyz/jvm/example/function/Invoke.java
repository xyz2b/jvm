package org.xyz.jvm.example.function;

public class Invoke {

    public static void main(String[] args) {
        int i = 10;
        System.out.println(Other.toHexString(i));
//        invokeStatic();
    }

    public static void invokeStatic() {
        test();
    }

    public static void invokeVirtual() {
        new Invoke().show();
    }

    public static void invokeSpecial() {
        new Invoke().hello();
    }

    public static void test() {
        System.out.println("test");
    }

    public void show() {
        System.out.println("show");
    }

    private void hello() {
        System.out.println("hello");
    }
}
