package org.xyz.jvm.example.jni;

import java.util.HashMap;

public class Hashmap {
    public static native HashMap createHashmap();
    public static native Object get(HashMap hashMap, String key);
    public static native Object get(HashMap hashMap, Object key);
    public static native void put(HashMap hashMap, String key, Object value);
    public static native void put(HashMap hashMap, Object key, Object value);

    public static void main(String[] args) {
        System.loadLibrary("jni");

        HashMap hashMap = Hashmap.createHashmap();
        Hashmap.put(hashMap, 1, "test");
        System.out.println(Hashmap.get(hashMap, 1));

        Hashmap.put(hashMap, 2, "test2");
        System.out.println(Hashmap.get(hashMap, 2));

        Hashmap.put(hashMap, "1", "test1");
        System.out.println(Hashmap.get(hashMap, "1"));

        Hashmap.put(hashMap, "2", "test22");
        System.out.println(Hashmap.get(hashMap, "2"));
    }
}
