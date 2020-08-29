package org.little.imap;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.store.lStore;

import java.util.ArrayList;

import org.little.store.lFolder;
import org.little.store.lMessage;

/**
 * models an ongoing mail transaction
 */
public class IMAPTransaction {
       private static  final Logger logger = LoggerFactory.getLogger(IMAPTransaction.class);
       private String  username;
       private String  foldername;
       private lStore  store;
       private lFolder folder;
       private ArrayList<lMessage> list_msg;

       
       public IMAPTransaction() {
              logger.info("Begin new IMAPTransaction");
              username   =null;  
              foldername =null;
              list_msg   =null;
       }

       public boolean finished() {
              logger.info("End IMAPTransaction");
              username   =null;  
              foldername =null;
              return true;
       }

       public String getUserName() { return username;}
       public void   setUserName(String username) {this.username = username;}
       public String getFolderName() {return foldername;}
       public void   setFolderName(String foldername) { this.foldername = foldername;}
	   public lStore getStore() {return store;}
	   public void setStore(lStore store) {this.store = store;}
       public lFolder getFolder(){return folder;}
       public void setFolder(lFolder folder){this.folder = folder;}

       public ArrayList<lMessage> getMsg() { return list_msg;}
       public void   setMsg(ArrayList<lMessage> l) {this.list_msg = l;}

}
