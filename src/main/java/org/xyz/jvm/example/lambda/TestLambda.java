package org.xyz.jvm.example.lambda;

public class TestLambda {
    public static void main(String[] args) {
        CustomLambda obj = (x) -> {
            System.out.println(x);
        };

        obj.run(1);
    }
}
