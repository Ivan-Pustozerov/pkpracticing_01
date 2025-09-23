package Task4.myfirstpackage;
public class MySecondClass{
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
