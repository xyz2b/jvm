package org.xyz.jvm.example.basictype;

public class PrintNull {
    public static void main(String[] args) {
        Object a = null;
        Object arr[] = {null, null};
        if (a == null) {
            System.out.println(a);
        }
    }
}
