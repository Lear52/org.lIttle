package org.little.rcmd.rsh;


public class sequence{
       private byte [] buffer;
       private int     point;
       private boolean equal;
       private String  type;
       private String  id;

       public sequence(String _type,String _id,String s) {
              clear();

              if(s!=null)buffer=s.getBytes();
              else       buffer=null;
              type=_type;
              id=_id;
       }
       private void clear() {
               point=0;
               equal=false;
               type="";
               buffer=null;
               
       }
       public boolean put(byte a) {
              if(buffer==null){clear();return false;}
              equal=false; 

              if(buffer[point]==a){
                 point++;
                 if(point==buffer.length){
                      equal=true; 
                      point=0;
                      return true;
                 }
              }
              else point=0;
              return equal;
       }
       public boolean check() {return equal;}
       public String  getType() {return type;}
       public String  getID() {return id;}
         
       public static void main(String[] arg){
           //boolean ret;
           byte [] b="234567812345612345667".getBytes();
           sequence s=new sequence("test","01","123");
           for(int i=0;i<b.length;i++) {
              if(s.put(b[i])){
                   System.out.println(s.getType());
              }
           }
           
       }

}
