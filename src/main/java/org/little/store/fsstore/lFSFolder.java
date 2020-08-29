package org.little.store.fsstore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.little.store.lFolder;
import org.little.store.lMessage;
//import org.little.store.lStore;
import org.little.util._qSortString;


public class lFSFolder  extends lFSElement implements lFolder {
    private static final Random GENERATOR = new Random();


    public final static String PREFIX="f_";

    //private lStore                 store;
    private ArrayList<lFSMessage> msg;
    
    public lFSFolder(lFSStore store,lFSElement _parent,String _name) {
                      super(_parent,_name);
                      clear();
                      //this.store = store; 
                      setFullName();
    }
    protected void    clear(){
                      msg      =new ArrayList<lFSMessage>(100);
                      //mode     =NO_OPEN;
    }
    protected String  getPrefix  (){return PREFIX;};

    protected boolean load(){
              File     folder = new File(getFullName());
              String[] ls     = folder.list();
              if(ls==null)return true;

              _qSortString sorter = new _qSortString();
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
                     lFSMessage m=new lFSMessage(this,msg_name,countnum);
                     if(m.load()==false)continue;
                     if(m.get()==null)continue;
                     msg.add(m);
                  }
                  countnum++;
              }
              return true;
    }

    protected ArrayList<lFSMessage> getMsgElement(){return msg;}
    
	@Override
    public ArrayList<lMessage>    getMsg(){
           ArrayList<lMessage> list=new ArrayList<lMessage>(msg.size());
           for(int i=0;i<msg.size();i++)list.add(msg.get(i).get());
           return list;
    }


    protected boolean create(){
           //mode=READ_WRITE;
           File f_folder=new File(getFullName());
           if(f_folder.exists()==true) return false;
           f_folder.mkdirs();
           return load();
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

	@Override
	public ArrayList<String> getList() {
	       ArrayList<String> list=new ArrayList<String>(msg.size());
           for(int i=0;i<msg.size();i++)list.add(msg.get(i).getName());
           return list;
	}

	


  
}
