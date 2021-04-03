package org.little.lmsg.store.dbstore;

import java.util.ArrayList;

import org.little.lmsg.store.lFolder;
import org.little.lmsg.store.lRoot;
import org.little.lmsg.store.lStore;


public class lDBStore   implements lStore, lDBElement  {
    //private static final Logger logger = LoggerFactory.getLogger(lStore.class);
       private String name_store;

       public lDBStore(String _name) {
              name_store=_name;
       }

       @Override
       public  boolean init (){
               //String sql="CREATE TABLE L_STORE("+"ID   NUMBER,"+"NAME VARCHAR(128)"+")";


               return false;
       }// TODO Auto-generated method stub
    
       @Override
       public  ArrayList<lFolder >    getListFolder  (){return null;}                    // TODO Auto-generated method stub
       @Override
       public lFolder getInboxFolder() {return null; }                                   // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder createInboxFolder() { return null; }                               // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder getOutboxFolder() { return null; }                                 // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder createOutboxFolder() { return null; }                              // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder getFolder(String name_folder) { return null; }                     // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder createFolder(String name_folder){return null;}                     // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder getFolder(lFolder parent, String name_folder) {return null; }      // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder createFolder(lFolder parent, String name_folder) {return null;}    // TODO Auto-generated method stub
       @Override                                                                        
       public String getName() { return name_store;}                                     // TODO Auto-generated method stub
       @Override                                                                        
       public void close(){return ;}                                                     // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder getDelboxFolder() { return null;}                                  // TODO Auto-generated method stub
       @Override                                                                        
       public lFolder createDelboxFolder() { return null;}                               // TODO Auto-generated method stub
       @Override                                                                        
       public void deleteFolder(String org_folder_name) {}                               // TODO Auto-generated method stub

       @Override
       public lFolder getCommonFolder(){return lRoot.getCommonStore().getInboxFolder();} // TODO Auto-generated method stub
   

}

