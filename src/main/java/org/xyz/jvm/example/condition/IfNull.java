package org.xyz.jvm.example.condition;

public class IfNull {

    public static void main(String[] args) {
        Object obj = null;

        if (null == obj) {
            System.out.println("null");
        } else {
            System.out.println("not null");
        }
    }

    public static void ifNull() {
        Object obj = null;

        if (null == obj) {
            System.out.println("null");
        } else {
            System.out.println("not null");
        }
    }

    public static void ifNonNull() {
        Object obj = null;

        if (null != obj) {
            System.out.println("null");
        } else {
            System.out.println("not null");
        }
    }
}
