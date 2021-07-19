package org.xyz.jvm.hotspot.src.share.tools;

public class Stream {
    /**
     * 从源字节数组读取size个字节数据到目的字节数组
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @param size      读取的字节数
     * @param ret       读取的目的字节数组，也同时是传出的参数
     */
    public static void readSimple(byte[] content, int from , int size, byte[] ret) {
        System.arraycopy(content, from, ret, 0, size);
    }

    /**
     * 从源字节数组中读取1个字节数据到目的字节数组，并返回读取后的位置
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @param ret       读取的目的字节数组，也同时是传出的参数
     * @return          读取完成之后的指针位置， 即开始读的位置 + 读了多少
     */
    private static int readU1(byte[] content, int from, byte[] ret) {
        System.arraycopy(content, from, ret, 0 , 1);
        return from + 1;
    }

    /**
     * 从源字节数组中读取1个字节数据
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @return          读取的字节
     */
    public static byte readU1Simple(byte[] content, int from) {
        byte[] arr = new byte[1];
        System.arraycopy(content, from, arr, 0 , 1);
        return arr[0];
    }

    /**
     * 从源字节数组中读取2个字节数据到目的字节数组，并返回读取后的位置
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @param ret       读取的目的字节数组，也同时是传出的参数
     * @return          读取完成之后的指针位置， 即开始读的位置 + 读了多少
    */
    public static int readU2(byte[] content, int from, byte[] ret) {
        System.arraycopy(content, from, ret, 0 , 2);
        return from + 2;
    }

    /**
     * 从源字节数组中读取2个字节数据到目的字节数组
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @param ret       读取的目的字节数组，也同时是传出的参数
     */
    public static void readU2Simple(byte[] content, int from, byte[] ret) {
        System.arraycopy(content, from, ret, 0 , 2);
    }

    /**
     * 从源字节数组中读取4个字节数据到目的字节数组，并返回读取后的位置
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @param ret       读取的目的字节数组，也同时是传出的参数
     * @return          读取完成之后的指针位置， 即开始读的位置 + 读了多少
     */
    public static int readU4(byte[] content, int from, byte[] ret) {
        System.arraycopy(content, from, ret, 0 , 4);
        return from + 4;
    }

    /**
     * 从源字节数组中读取4个字节数据到目的字节数组
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @param ret       读取的目的字节数组，也同时是传出的参数
     */
    public static void readU4Simple(byte[] content, int from, byte[] ret) {
        System.arraycopy(content, from, ret, 0 , 4);
    }

    /**
     * 从源字节数组中读取8个字节数据到目的字节数组，并返回读取后的位置
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @param ret       读取的目的字节数组，也同时是传出的参数
     * @return          读取完成之后的指针位置， 即开始读的位置 + 读了多少
     */
    public static int readU8(byte[] content, int from, byte[] ret) {
        System.arraycopy(content, from, ret, 0 , 8);
        return from + 8;
    }

    /**
     * 从源字节数组中读取8个字节数据到目的字节数组
     * @param content   读取的源字节数组
     * @param from      从哪个位置开始读取
     * @param ret       读取的目的字节数组，也同时是传出的参数
     */
    public static void readU8Simple(byte[] content, int from, byte[] ret) {
        System.arraycopy(content, from, ret, 0 , 8);
    }
}
