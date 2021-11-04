package org.xyz.jvm.example;

public class Gran implements Test, Test2 {
    public static int it = 2;
    public int t = 2;
    public int s = 1;

    public void test1() {
        System.out.println("gran interface");
    }

    public void test2() {
        System.out.println("gran interface2");
    }


    public int test() {
        return s;
    }

    public void hello() {
        System.out.println(3);
    }

    public void t() {
        System.out.println(t);
    }
}
