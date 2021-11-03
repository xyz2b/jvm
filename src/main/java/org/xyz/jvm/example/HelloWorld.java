package org.xyz.jvm.example;


public class HelloWorld extends Father {
    public static int i = 1;
    public int a;
    public static long l = 100;
    public int t = 1;

    public void test(int i) {
        t = 10;
    }

    public void hello() {
        System.out.println(1);
    }

    public static void main(String[] args) {
//        String s = new String("xyzjiao");
//        System.out.println(s);


        HelloWorld helloWorld = new HelloWorld();
        Father father = new Father();
        Gran gran = new Gran();
        helloWorld.hello();
        father.hello();
        gran.hello();
//        int f = HelloWorld.it;
//        System.out.println(f);
//
//        HelloWorld.it = 1;
//        System.out.println(HelloWorld.it);
//        System.out.println(helloWorld.tt);
//
//        helloWorld.tt = 1;
//
//        System.out.println(helloWorld.tt);


//        System.out.println(i);
//        i = 2;
//        int b = i;
//        int c = helloWorld.t;
////
        helloWorld.t();
        System.out.println(helloWorld.t);
////
        helloWorld.test(10);
//        System.out.println(b);
        System.out.println(helloWorld.t);
        helloWorld.t();


        System.out.println(helloWorld.s);
        System.out.println(helloWorld.test());
//        helloWorld.s = 10;
//        System.out.println(helloWorld.s);

//        Father f = new Father();
//        System.out.println(f.s);
//        System.out.println(f.tt);
//        f.tt = 10;
//        System.out.println(f.tt);
//        f.s = 10;
//        System.out.println(f.s);

//        System.out.println(Father.it);
//        Father.it = 1;
//        System.out.println(Father.it);
//
//
//        Gran gran = new Gran();
//        System.out.println(gran.s);
//        System.out.println(Gran.it);
//        Gran.it = 1;
//        System.out.println(Gran.it);
    }

}

