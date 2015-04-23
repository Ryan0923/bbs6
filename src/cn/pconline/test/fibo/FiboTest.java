package cn.pconline.test.fibo;

public class FiboTest {

    public static int fibStaticTernary(int n) {
        return (n >= 2) ? fibStaticTernary(n - 1) + fibStaticTernary(n - 2) : 1;
    }


    public static int fibStaticIf(int n) {
        if (n >= 2)
            return fibStaticIf(n - 1) + fibStaticIf(n - 2);
        else
            return 1;
    }


    int fibTernary(int n) {
        return (n >= 2) ? fibStaticTernary(n - 1) + fibStaticTernary(n - 2) : 1;
    }


    int fibIf(int n) {
        if (n >= 2)
            return fibIf(n - 1) + fibIf(n - 2);
        else
            return 1;
    }


    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        System.out.println(FiboTest.fibStaticTernary(45));
        System.out.println("Java(static ternary): " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        System.out.println(FiboTest.fibStaticIf(45));
        System.out.println("Java(static if): " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        System.out.println(new FiboTest().fibTernary(45));
        System.out.println("Java(instance ternary): " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        System.out.println(new FiboTest().fibIf(45));
        System.out.println("Java(instance if): " + (System.currentTimeMillis() - start) + "ms");
    }
}
