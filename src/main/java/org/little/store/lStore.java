package org.little.store;

import java.util.ArrayList;

public interface lStore{
    
    public ArrayList<lFolder >    getListFolder              ();
    
    public lFolder                getInboxFolder         ();
    public lFolder                getOutboxFolder        ();

    public lFolder                createInboxFolder      ();
    public lFolder                createOutboxFolder     ();
                                 
    public lFolder                getFolder              (String name_folder);
    public lFolder                getFolder              (lFolder parent,String name_folder);

    public lFolder                createFolder           (String name_folder);
    public lFolder                createFolder           (lFolder parent,String name_folder);

    public String                 getName();

}

