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

class MySecondClass{
    private int a;
    private int b;

    public MySecondClass(int a, int b)
    {
        setA(a);
        setB(b);
    }

    public int Sum(){return a+b;}

    public void setA(int a){this.a=a;}
    public void setB(int b){this.b=b;}
    public int getA(){return this.a;}
    public int getB(){return this.b;}


}
