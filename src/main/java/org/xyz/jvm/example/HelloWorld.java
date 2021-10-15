package org.xyz.jvm.example;

public class HelloWorld implements Test {
    public final static int i = 1;
    public static void main(String[] args) throws IndexOutOfBoundsException {
        try {
            System.out.println("Hello World");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(System.getProperty("java.library.path"));
    }
}