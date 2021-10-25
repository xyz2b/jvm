package org.xyz.jvm.example;


public class HelloWorld {
    public static int i = 1;
    public int a;
    public static long l = 100;
    public int t = 1;

    public void test(int i) {
        t = i;
    }

    public static void main(String[] args) {
        HelloWorld helloWorld = new HelloWorld();
        System.out.println(helloWorld.t);
//        System.out.println("HelloWorld");
    }
}

