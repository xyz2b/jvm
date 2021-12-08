package org.xyz.jvm.example.test;

public class Test {
    public static void main(String[] args) {
        System.out.println(test_b.str);

        test_b.str = "C";

        System.out.println(test_b.str);

        System.out.println(test_b.str_b);

        // 输出
        // A Block
        // A
    }
}

class test_a {
    public static String str = "A";

    static {
        System.out.println("A Block");
    }
}

class test_b extends test_a {
    public static String str_b = "B";
    static {
        System.out.println("B Block");
    }
}