package org.little.rcmd.rsh;


public class sequence{
       private char [] s_buffer;
       private byte [] b_buffer;
       private int     point;
       private boolean equal;
       private String  type;
       private String  id;

       public sequence(String _type,String _id,String str_sequence) {
              clear();

              if(str_sequence!=null){
                 s_buffer=str_sequence.toCharArray();
                 b_buffer=str_sequence.getBytes();
              }
              else{
                   s_buffer=null;
                   b_buffer=null;
              }
              type=_type;
              id=_id;
       }
       private void clear() {
               point=0;
               equal=false;
               type="";
               b_buffer=null;
               s_buffer=null;
               
       }
       public boolean put(char ch) {
              if(s_buffer==null){clear();return false;}
              equal=false; 

              if(s_buffer[point]==ch){
                 point++;
                 if(point==s_buffer.length){
                      equal=true; 
                      point=0;
                      return true;
                 }
              }
              else point=0;
              return equal;
       }
       public boolean put(byte ch) {
              if(b_buffer==null){clear();return false;}
              equal=false; 

              if(b_buffer[point]==ch){
                 point++;
                 if(point==b_buffer.length){
                      equal=true; 
                      point=0;
                      return true;
                 }
              }
              else point=0;
              return equal;
       }
       public void    reset() {point=0;equal=false;}
       public boolean check() {return equal;}
       public String  getType() {return type;}
       public String  getID() {return id;}
         
       public static void main(String[] arg){
           //boolean ret;
           char [] b="234567812345612345667".toCharArray();
           sequence s=new sequence("test","01","123");
           for(int i=0;i<b.length;i++) {
              if(s.put(b[i])){
                   System.out.println(s.getType());
              }
           }
           
       }

}
