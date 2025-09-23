package Task4;

import myfirstpackage.*;
class MyFirstClass {
    public static void main(String[] s){
        MySecondClass o = new MySecondClass(0,1);
        System.out.println(o.Sum());
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                o.setA(i);
                o.setB(j);
                System.out.print(o.Sum());
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}

