package org.little.store.fsstore;

import java.io.File;
import java.util.ArrayList;

import org.little.store.lRoot;
import org.little.store.lStore;
import org.little.util._qSortString;


public class lFSRoot  extends lFSElement implements lRoot{

       private final static String      cfg_base_name="var"+File.separator+"mbox";//+File.separator;
    
       public  final static lFSRoot     root=new lFSRoot();
    
       public lFSRoot() {
                 super(null,cfg_base_name);
                 setFullName();
       }

       public    ArrayList<lStore>  getStore  (){return getStore  (new ArrayList<lStore>());}

       private   ArrayList<lStore >    getStore  (ArrayList<lStore > list){
           File     folder = new File(getFullName());
           String[] ls     = folder.list();
           if(ls==null)return null;
           _qSortString sorter = new _qSortString();
           sorter.sort(ls);
           for(int i = 0;i < ls.length; i++){
               String short_name  = ls[i];

               if(short_name.startsWith(lFSStore.PREFIX)==false)continue;
               String f_n=getFullName()+File.separator+short_name;
               File   f  =new File(f_n);
               if(f.isDirectory()==false)continue;

               short_name=short_name.substring(lFSStore.PREFIX.length());

               list.add(new lFSStore(short_name));
           }
   	    return list;
       }

       
       
}
