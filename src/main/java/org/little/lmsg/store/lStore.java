package org.little.lmsg.store;

import java.util.ArrayList;

public interface lStore{
    
       public ArrayList<lFolder >    getListFolder          ();
       
       public lFolder                getInboxFolder         ();
       public lFolder                getOutboxFolder        ();
       public lFolder                getDelboxFolder        ();
       public lFolder                getCommonFolder        ();
      
       public lFolder                createInboxFolder      ();
       public lFolder                createOutboxFolder     ();
       public lFolder                createDelboxFolder     ();
                                    
       public lFolder                getFolder              (String name_folder);
       public lFolder                getFolder              (lFolder parent,String name_folder);
      
       public lFolder                createFolder           (String name_folder);
       public lFolder                createFolder           (lFolder parent,String name_folder);
      
       public String                 getName();
      
       public void                   close();

       public void                   deleteFolder(String org_folder_name);
}

