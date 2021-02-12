package org.little.rcmd.rsh;


import java.util.ArrayList;

public class sequences{
       private ArrayList<sequence> list;
       private sequence            point;

       public sequences() {
              clear();
       }
       private void clear() {
               point=null;
               list =new ArrayList<sequence> (10);
       }
       public void add(String type,String id,String mask){
              list.add(new sequence(type,id,mask));
       }
       public synchronized sequence   put(byte a) {
              point=null;
              for(int i=0;i<list.size();i++){
                  if(list.get(i).put(a)){
                     point=list.get(i);
                     return point;
                  }
              }
              return null;
       }
       public sequence check() {return point;}

       public String getName() {if(point==null) return "";else return point.getType();}
         
       public static void main(String[] arg){
              //boolean ret;
              byte [] b="234567812345612345667".getBytes();
              sequences s=new sequences();
              s.add("test1","01","123");
              s.add("test2","02","561");
              for(int i=0;i<b.length;i++) {
              	  if(s.put(b[i])!=null){
                     System.out.println(s.getName());
                  }
              }
           
       }

}
