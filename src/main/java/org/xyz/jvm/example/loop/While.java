package org.xyz.jvm.example.loop;

public class While {

    public static void main(String[] args) {
        int i = 10;

        while (0 < i) {
            System.out.println("hello");
            System.out.println(i);
            i--;
        }
    }

    public static void test_1() {
        int i = 10;

        while (10 == i) {
            System.out.println("hello");
        }
    }

}
