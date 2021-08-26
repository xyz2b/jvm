package org.xyz.jvm.example.lambda;

public class TestLambda {
    public static void main(String[] args) {
        CustomLambda obj = (x, y) -> {
            System.out.println(x + y);
        };

        obj.run(1, 2);
    }
}
