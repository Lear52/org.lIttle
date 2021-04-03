package org.little.lmsg.store.fsstore;

import java.io.File;
import java.util.ArrayList;

import org.little.lmsg.store.lFolder;
import org.little.lmsg.store.lRoot;
import org.little.lmsg.store.lStore;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string._stringQSort;



public class lFSStore  extends lFSElement implements lStore  {
       private static final Logger logger = LoggerFactory.getLogger(lStore.class);
      
       public  final static String      PREFIX="s_";
      
       public lFSStore(String _name) {
               super(lFSRoot.root,_name);
               setFullName();
       }
       protected  String   getPrefix  (){return PREFIX;};
      
       public boolean open(int _mode){
              boolean ret;
              File f_store;
      
              f_store=new File(getFullName());
      
              if(f_store.exists()) {
                 if(f_store.isDirectory())ret=true;
                 else ret=false;
              }
              else ret=false;
              logger.trace("store:"+getFullName()+" open:"+ret);
              return ret;
       }
      
       @Override
       public void close(){return ;}
       //public boolean del(){return false;}
      
       @Override
       public    ArrayList<lFolder >    getListFolder  (){
                 return getFolder  (new ArrayList<lFolder >(),null);
       }
       @Override
       public lFolder getInboxFolder   (){return getFolder("INBOX");}
       @Override
       public lFolder createInboxFolder(){return createFolder("INBOX");}
      
       @Override
       public lFolder getOutboxFolder   (){return getFolder("OUTBOX");}
       @Override
       public lFolder createOutboxFolder(){return createFolder("OUTBOX");}

       @Override
       public lFolder getCommonFolder   (){return lRoot.getCommonStore().getInboxFolder();}

       @Override
       public lFolder getDelboxFolder   (){return getFolder("TRASHBOX");}
       @Override
       public lFolder createDelboxFolder(){return createFolder("TRASHBOX");}
      
       @Override
       public lFolder getFolder(String name_folder){
              lFSFolder f= new lFSFolder(this,this,name_folder);
              if(f.open(lFolder.READ_WRITE))return f;
              return null;
       }
       @Override
       public lFolder createFolder(String name_folder){
              lFSFolder f= new lFSFolder(this,this,name_folder);
              if(f.create())return f;
              return null;
       }
       @Override
       public lFolder getFolder(lFolder parent,String name_folder){
              lFSFolder f=new lFSFolder(this,(lFSFolder)parent,name_folder);
              if(f.open(lFolder.READ_WRITE))return f;
              return null;
       };
      
       @Override
       public lFolder createFolder(lFolder parent,String name_folder){
              lFSFolder f=new lFSFolder(this,(lFSFolder)parent,name_folder);
              if(f.create())return f;
              return null;
       };
       @Override
             public void deleteFolder(String org_folder_name) {
                 // TODO Auto-generated method stub
                 
       }
       
       //-------------------------------------------------------------------------------------------------------------

       private   ArrayList<lFolder >    getFolder  (ArrayList<lFolder > list,lFSFolder _parent_folder){
           String   folder_name;
           if(_parent_folder==null)folder_name=getFullName();
           else                    folder_name=_parent_folder.getFullName();
      
           File     folder = new File(folder_name);
           String[] ls     = folder.list();
      
           if(ls!=null){
              logger.trace("folder:"+folder_name+" count file:"+ls.length);
              _stringQSort sorter = new _stringQSort();
              sorter.sort(ls);
              for(int i = 0;i < ls.length; i++){
                  String short_name  = ls[i];
             
                  if(short_name.startsWith(lFSFolder.PREFIX)==false)continue;
                  String f_n=getFullName()+File.separator+short_name;
                  File   f  =new File(f_n);
                  if(f.isDirectory()==false)continue;
             
                  short_name=short_name.substring(lFSFolder.PREFIX.length());
             
                  lFSFolder new_folder;
                  if(_parent_folder==null)new_folder=new lFSFolder(this,this          ,short_name);
                  else                    new_folder=new lFSFolder(this,_parent_folder,short_name);
                  list.add(new_folder);
             
                  getFolder(list,new_folder);
              }
           }
           
           if(_parent_folder==null)list.add(getCommonFolder());
           
           logger.trace("folder:"+folder_name+" count msg:"+list.size());
           return list;
       }
      
       
     
        /*
      
       public static void main(String[] args) {
              lStore store1 =lRoot.getStore("av");
              lStore store2 =lRoot.getStore("iap");
              lFolder f1=store1.getFolder("inbox");
              lFolder f2=store1.getFolder("outbox");
              lFolder f3=store2.getFolder("inbox");
              lFolder f4=store2.getFolder("outbox");
              //lFolder f2=store.createFolder("new");
              ArrayList<lMessage> msg=f2.getMsg();
              for(int i=0;i<msg.size();i++){
                  lMessage l=msg.get(i);
                  f1.save(l);
                  f3.save(l);
                  f4.save(l);
              }
       }
      */

}

