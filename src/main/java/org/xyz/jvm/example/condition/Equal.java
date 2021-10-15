package org.xyz.jvm.example.condition;

public class Equal {

    public static void main(String[] args) {
        Object obj = null;
        System.out.println(obj == null);

//        byte v1 = 10;
//        byte v2 = 20;
//        System.out.println(v1 != v2);

//        long v1 = 10;
//        long v2 = 20;
//        System.out.println(v1 == v2);
    }

    public static void test1() {
        byte v1 = 10;
        byte v2 = 20;

        System.out.println(v1 == v2);
    }

    public static void testLong() {
        long v1 = 10;
        long v2 = 20;

        System.out.println(v1 == v2);
    }

    public static void test() {
        Object obj = null;

        System.out.println(obj == null);
    }
}
