package org.xyz.jvm.example.loop;

public class DoWhile {

    public static void main(String[] args) {
        int i = 10;

        do {
            System.out.println("ziya");
            i--;
        } while (0 < i);
    }

    public static void test_1() {
        int i = 10;

        do {
            System.out.println("ziya");
        } while (10 == i);
    }
}
