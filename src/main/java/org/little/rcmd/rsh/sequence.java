package org.little.rcmd.rsh;


public class sequence{
       private byte [] buffer;
       private int     point;
       private boolean equal;
       private String  name;

       public sequence(String _name,String s) {
              clear();

              if(s!=null)buffer=s.getBytes();
              else       buffer=null;
              name=_name;
       }
       private void clear() {
               point=0;
               equal=false;
               name="";
               buffer=null;
               
       }
       public boolean    put  (byte a) {
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
       public String  getName() {return name;}
         
       public static void main(String[] arg){
           //boolean ret;
           byte [] b="234567812345612345667".getBytes();
           sequence s=new sequence("test","123");
           for(int i=0;i<b.length;i++) {
           	if(s.put(b[i]))System.out.println(s.getName());
           	
           }
           
       }

}
