package functions.classes;

import org.junit.jupiter.params.aggregator.ArgumentsAccessor;


public class LazyTester {
    protected class LazyHolder {//nested class//make static?????!
        private Object[] mem_in;//(решение - отключение параллельного тестирования)
        private Object mem_out;//делать статическими - ошибка,
        private int counter=0;//т.к несколько тестов будут вызывать данный холдер?
        public LazyHolder(Object[] in, Object out){
            mem_in=new Object[in.length];
            for(int i=0;i<in.length;++i)
            {
                mem_in[i]=in[i];
            }
            mem_out=new Object();
            mem_out=out;
        }
        public Object out(){return mem_out;}
        public Object nextIn(){return mem_in[counter++];}
        public void reset(){counter=0;}
        public void clear(){mem_in=null;}
    }

    protected double delta=1e-6;
    protected static StringBuffer log=new StringBuffer("Default");//temp nonstatic - in case of parallel tests
    protected static String type=new String("Default");//temp nonstatic - in case of parallel tests
    protected static final String[] TYPES ={
            "DOUBLE",//Double
            "FLOAT",//Float
            "LONG",//Long
            "INT",//Int
            "CHAR",//Char
            "BYTE",//Byte
            "BOOL",//Bool
            "STR"//String
    };

    protected static boolean isLog(String msg) {

        return(msg.charAt(0)=='L' && msg.charAt(1)=='O' && msg.charAt(2)=='G');
    }
    protected static boolean isType(String msg) {
        boolean res =false;
        int i=0;
        for(;!res && i<TYPES.length;++i) {res=msg.equals(TYPES[i]);}
        if(res) {type=(TYPES[i-1]);}
        return res;
    }
    protected static boolean getLog(String[] msg,String type) {
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
    protected boolean getLog(String msg) {
        boolean res=isLog(msg);
        if(res)
        {
            log=new StringBuffer(msg);
        }
        return res;
    }

    protected static Object convert(String input,String type){
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

    protected LazyHolder LazyTest(ArgumentsAccessor args, String type_out){
        LazyHolder holder=null;
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


            holder=new LazyHolder(in,out);


            /*if(type_out.equals("DOUBLE") ||type_out.equals("FLOAT"))
            {
                Assertions.assertEquals((double)out,(double)func.apply(in[0]),delta,String.valueOf(log));
            }
            else{
                Assertions.assertEquals(out,func.apply(in[0]), String.valueOf(log));
            }*/
        }
        return holder;
    }


}
