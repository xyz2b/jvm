package org.xyz.jvm.example.array;

public class Traverse {

    public static void main(String[] args) {
        byte[] arr = {1, 2, 3, 4, 5, 10};

        for (int i =0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    public static void forBasic() {
        byte[] arr = {1, 2};

        for (int i =0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    public static void forByLong() {
        byte[] arr = {1, 2};

        for (long i =0; i < arr.length; i++) {
            System.out.println(arr[0]);
        }
    }

    public static void forHard() {
        byte[] arr = {1, 2, 3, 4, 5};

        for (byte b: arr) {
            System.out.println(b);
        }
    }

    /**
     * 测试数组作为函数参数
     *  1、基本类型数组
     *  2、引用类型数组
     * @param arr
     */
    public static void traverse(int[] arr) {
        for (int i: arr) {
            System.out.println(i);
        }
    }

    public static void traverse_test1() {
        int[] arr = {1, 2, 3, 4, 5};

        traverse(arr);
    }

    /**
     * 改变数组元素值
     *  1、基本类型数组
     *  2、引用类型数组
     */
    public static void change(int[] arr) {
        arr[1] = 20;
    }

    public static void change_test1() {
        int[] arr = {1, 2, 3, 4, 5};

        change(arr);
        traverse(arr);
    }
}
