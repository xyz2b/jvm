package org.xyz.jvm.example.lambda;

public class TestLambda {
    public static void main(String[] args) {
        CustomLambda obj = () -> {
            System.out.println("hello");
        };

        obj.run();
    }
}
