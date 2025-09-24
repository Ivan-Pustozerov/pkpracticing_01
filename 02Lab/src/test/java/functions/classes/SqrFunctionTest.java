package functions.classes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;

class SqrFunctionTest {

    private double delta=1e-6;
    private SqrFunction func=new SqrFunction();

    private static StringBuffer log=new StringBuffer("Default");
    private static String type=new String("Default");
    private static String type_out=new String("Default");
    private static final String[] TYPES ={
            "DOUBLE",//Double
            "FLOAT",//Float
            "LONG",//Long
            "INT",//Int
            "CHAR",//Char
            "BYTE",//Byte
            "BOOL",//Bool
            "STR"//String
    };

    private boolean isLog(String msg) {

        return(msg.charAt(0)=='L' && msg.charAt(1)=='O' && msg.charAt(2)=='G');
    }
    private boolean isType(String msg) {
        boolean res =false;
        int i=0;
        for(;!res && i<TYPES.length;++i) {res=msg.equals(TYPES[i]);}
        if(res) {type=(TYPES[i-1]);}
        return res;
    }
    private boolean getLog(String[] msg,String type) {
        boolean res=false;
        boolean type_res=isType(type);
        int i=0;
        int size=msg.length;
        while(!res && type_res && i<size){
            res=isLog(msg[i++]);
        }
        if(res){
            log=new StringBuffer(msg[i-1]);
        }
        return res && type_res;
    }
    private boolean getLog(String msg) {
        boolean res=isLog(msg);
        if(res)
        {
            log=new StringBuffer(msg);
        }
        return res;
    }

    private Object convert(String input,String type){
        Object in=new Object();
        if(type==TYPES[0])
        {
            in= Double.parseDouble(input);
        }
        else if(type==TYPES[1])
        {
            in=Float.parseFloat(input);
        }
        else if(type==TYPES[2])
        {
            in=Long.parseLong(input);
        }
        else if(type==TYPES[3])
        {
            in=Integer.parseInt(input);
        }
        else if(type==TYPES[4])
        {
            in=input.charAt(0);
        }
        else if(type==TYPES[5])
        {
            in=Byte.parseByte(input);
        }
        else if(type==TYPES[6])
        {
            in=Boolean.parseBoolean(input);
        }
        else if(type==TYPES[7])
        {
            in=input;
        }
        return in;
    }

    private void LazyTest(ArgumentsAccessor args,String type_out){
        String[] input=new String[args.size()-1];
        String ans=new String(args.getString(args.size()-1));
        for(int i=0;i<input.length;++i) {
            input[i]=args.getString(i);
        }
        if(!getLog(input,ans)){

            Object[] in=new Object[input.length];
            Object out=new Object();

            try{
                for(int i=0;i<input.length;++i)
                {
                    in[i]= convert(input[i],type);
                }
                out=convert(ans,type_out);
            }catch (NumberFormatException e) {System.out.println("ERRR!!!!");}


            if(type_out.equals("DOUBLE") ||type_out.equals("FLOAT"))
            {
                Assertions.assertEquals((double)out,(double)func.apply(in[0]),delta,String.valueOf(log));
            }
            else{
                Assertions.assertEquals(out,func.apply(in[0]), String.valueOf(log));
            }
        }
    }


    //в параметризированном тесте мы создаем НОВЫЙ объект sqrFuncTest НА КАЖДОЙ ИТЕРАЦИИ!!!
    @ParameterizedTest
    @CsvFileSource(resources = "/data1-1.csv",numLinesToSkip = 0)
    public void testApply(ArgumentsAccessor args) {
        String type_out="DOUBLE";
        LazyTest(args,type_out);
    }
}