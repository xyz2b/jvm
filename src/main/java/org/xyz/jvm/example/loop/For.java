package org.xyz.jvm.example.loop;

public class For {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }

    public static void test() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }

    /**
     * 跟数组一起
     */
    public static void forHard() {
        byte[] arr = {1, 2};

        for (byte b: arr) {
            System.out.println(b);
        }
    }

    /**
     * 这个代码会循环10次吗
     */
    public static void test3() {
        for (int i = 0; i < 10; i++) {
            break;
        }
    }
}
