package org.little.store;

import java.util.ArrayList;

import org.little.lmsg.lMessage;


public interface  lFolder{

       public static final int    READ_ONLY        = 1;
       public static final int    READ_WRITE       = 3;
       public static final int    WRITE            = 2;
       public static final int    NO_OPEN          = -1;
      
       public ArrayList<lMessage>    getMsg     ();
       public int                    getUID     ();
       
       
       public boolean                open(int _mode);
       public boolean                close();
       public boolean                save(lMessage msg);
       public String                 getName();
       public ArrayList<String>      getList();
      
       public static int getAllMsg   (ArrayList<lMessage> list) {
                 return list.size();
       }
       public static int  getUnreadMsg(ArrayList<lMessage> list){
              int count=0;           
              for(int i=0;i<list.size();i++)if(list.get(i).getReceiveDate()==null)count++;
              return count;
       }
       public static int  getFirstUnreadMsg(ArrayList<lMessage> list){
              for(int i=0;i<list.size();i++)if(list.get(i).getReceiveDate()==null)return list.get(i).getNum();
              return 0;
       }
       public static lMessage getMsg4Id(ArrayList<lMessage> list,int num){
              for(int i=0;i<list.size();i++)if(list.get(i).getNum()==num)return list.get(i);
              return null;
       }
       public int getUnreadMsg();
       public int getRecentMsg();
       public int getAllMsg();
       public int getNextUID();
       public int getMaxUID();
       public int getFirstUnreadMsg();
      
  
}
