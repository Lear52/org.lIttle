package org.little.lmsg.store.fsstore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.little.lmsg.lMessage;
import org.little.lmsg.store.lFolder;
import org.little.lmsg.store.lUID;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string._stringQSort;


public class lFSFolder  extends lFSElement implements lFolder {
       private static final Logger  logger = LoggerFactory.getLogger(lFSFolder.class);
       private static final Random GENERATOR = new Random();
      
      
       public final static String PREFIX="f_";
      
       //private lStore                 store;
       private ArrayList<lFSMessage> list_fs_msg;
       
          
       private int unread_msg      =0;            
       private int recent          =0;            
       private int all_msg         =0;
       private int max_uid         =0;
       private int first_unread_msg=0;
       private int valid_uid       =0;
       private int next_uid       =0;

       
       public lFSFolder(lFSStore store,lFSElement _parent,String _name) {
                         super(_parent,_name);
                         clear();
                         //this.store = store; 
                         setFullName();
       }

       @Override
       public int getUnreadMsg() {       return unread_msg;}
       @Override
          public int getRecentMsg() {return recent;}
       @Override
          public int getAllMsg(){return all_msg;}
       @Override
          public int getMaxUID() {return max_uid;}
       @Override
          public int getFirstUnreadMsg() {return first_unread_msg;}
       @Override
          public int getNextUID() {       return next_uid;}

       
       @Override
       public    int     getUID     () {
                    String name=getName().toUpperCase();
                    if(name.startsWith("INBOX"))valid_uid=901;
                    else
                    if(name.startsWith("SENT " ))valid_uid=902;
                    else
                    if(name.startsWith("SENT" ))valid_uid=903;
                    else
                    if(name.startsWith("TRASH"))valid_uid=904;
                    else                        valid_uid=lUID.get();
                    return valid_uid;
       }       
       
       @Override
       public ArrayList<lMessage> getMsg(){
           ArrayList<lMessage> list=new ArrayList<lMessage>(list_fs_msg.size());
           for(int i=0;i<list_fs_msg.size();i++)list.add(list_fs_msg.get(i).get());
           return list;
       }
       @Override
       public boolean open(int _mode){
              //mode=_mode;
              File f_folder=new File(getFullName());
              if(f_folder.exists()==false) return false;
              if(f_folder.isDirectory()==false)return false;
              return load();
       }
       @Override
       public boolean close(){
              clear();
              return true;
       }
       public ArrayList<String> getList() {
              ArrayList<String> list=new ArrayList<String>(list_fs_msg.size());
              for(int i=0;i<list_fs_msg.size();i++)list.add(list_fs_msg.get(i).getName());
              return list;
       }
       
       protected void    clear(){
                         list_fs_msg      =new ArrayList<lFSMessage>(100);
                         //mode     =NO_OPEN;
       }
       protected String  getPrefix  (){return PREFIX;};
      
       protected boolean load(){
                 File     folder;

                 folder = new File(getFullName());

                 String[] ls     = folder.list();
                 if(ls!=null){
                    logger.trace("folder:"+getFullName()+" count file:"+ls.length);
                   
                    _stringQSort sorter = new _stringQSort();
                    sorter.sort(ls);
                    int countnum=1;
                   
                    for(int i = 0;i < ls.length; i++){
                        String n  = ls[i];
                        if(n.startsWith(lFSMessage.PREFIX)==false)continue;
                   
                        String f_n=getFullName()+File.separator+n;
                        File   f  =new File(f_n);
                        if(f.isFile()==false)continue;
                        if(f.length()>0){
                           String msg_name=n.substring(lFSMessage.PREFIX.length());
                           lFSMessage fs_msg=new lFSMessage(this,msg_name,countnum);
                           if(fs_msg.load()==false)continue;
                           if(fs_msg.get()==null)continue;
                           list_fs_msg.add(fs_msg);
                           fs_msg.get().setNum(countnum);
                        }
                        countnum++;
                    }
                 }
                 logger.trace("folder:"+getFullName()+" count msg:"+list_fs_msg.size());

                 return true;
       }
      
       protected ArrayList<lFSMessage> getMsgElement(){return list_fs_msg;}
    

       protected boolean create(){
              //mode=READ_WRITE;
              File f_folder=new File(getFullName());
              if(f_folder.exists()==true) return false;
              f_folder.mkdirs();
              return load();
       }
       //public boolean del(){return false;}
       //public boolean renameTo(String _new_name){return false;}
      
       protected String getNewName(){
              SimpleDateFormat sfd =new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
              return sfd.format(new Date())+GENERATOR.nextLong();
       }
       protected lFSMessage createMessageXML(){
              lFSMessage msg;
      
              msg=new lFSMessage(this,getNewName(),0);
              if(msg.create())return msg;
      
              GENERATOR.nextLong();
              msg=new lFSMessage(this,getNewName(),0);
              if(msg.create())return msg;
      
              GENERATOR.nextLong();
              msg=new lFSMessage(this,getNewName(),0);
              if(msg.create())return msg;
      
              return null;
       }
       @Override
       public boolean save(lMessage msg){
                 lFSMessage new_msg=createMessageXML();
                 if(new_msg==null)return false;
                 new_msg.set(msg);
                 new_msg.save();
                 return true;
       }

       
  
}
