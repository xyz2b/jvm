package org.xyz.jvm.example;


public class HelloWorld extends Father {
    public static int i = 1;
    public int a;
    public static long l = 100;
    public int t = 1;

    public int test(int i) {
        t = i;
        return t;
    }

    public static void main(String[] args) {
        HelloWorld helloWorld = new HelloWorld();
        System.out.println(i);
        i = 2;
        int b = i;
        int c = helloWorld.t;

        System.out.println(helloWorld.t);

        helloWorld.test(10);
        System.out.println(b);
        System.out.println(helloWorld.t);

    }
}

