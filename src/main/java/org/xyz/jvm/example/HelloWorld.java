package org.xyz.jvm.example;


public class HelloWorld extends Father implements Test {
    public static int i = 1;
    public int a;
    public static long l = 100;
    public int t = 1;

    public void test1() {
        System.out.println("interface");
    }

    public void test2() {
        System.out.println("interface2");
    }

    public void test(int i) {
        t = 10;
    }

    public void hello() {
        System.out.println(1);
    }

    public static void main(String[] args) {
//        String s = new String("xyzjiao");
//        System.out.println(s);
//
//        Test test = new HelloWorld();
//        test.test1();
//
//        Test2 test2 = new HelloWorld();
//        test2.test1();
//        test2.test2();
//
//        Test granTest = new Gran();
//        granTest.test1();
//
//        Test2 granTest2 = new Gran();
//        granTest2.test1();
//        granTest2.test2();
//
//        Test faTest = new Father();
//        faTest.test1();
//
//        Test2 faTest2 = new Father();
//        faTest2.test1();
//        faTest2.test2();

//        Father hello = new HelloWorld();
//        hello.hello();

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
//
//        helloWorld.t();
//        System.out.println(helloWorld.t);
//
//        helloWorld.test(10);
//        System.out.println(b);
//        System.out.println(helloWorld.t);
//        helloWorld.t();


//        System.out.println(helloWorld.s);
//        System.out.println(helloWorld.test());
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

