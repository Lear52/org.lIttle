package org.little.lmsg.store;

import java.util.ArrayList;

import org.little.lmsg.store.fsstore.lFSRoot;
import org.little.lmsg.store.fsstore.lFSStore;


public interface lRoot  {

       public static lStore getStore(String _name){
              return new lFSStore(_name); 
       }
       public static lStore getCommonStore(){
              return new lFSStore("COMMON"); 
       }


       public static  ArrayList<lStore>  getStore  (){
              return new lFSRoot().getStore();
              
       }
       //--------------------------------------------------------------------------------------------
       public static void main(String[] args) {
              ArrayList<lStore> list =lRoot.getStore();
              System.out.println("count store:"+list.size());
              for(int i=0;i<list.size();i++) {
                  System.out.println("store("+i+"):"+list.get(i).getName());
                  lStore f=list.get(i);

                  ArrayList<lFolder> f_list =f.getListFolder();  

                  System.out.println("count folder:"+f_list.size()+" for store:"+list.get(i).getName());

                  for(int j=0;j<f_list.size();j++) {
                     lFolder ff=f_list.get(j);
                     System.out.println("folder("+j+"):"+ff.getName());
                     ff.open(lFolder.READ_WRITE);

                     ArrayList<String> m_list = ff.getList();

                     System.out.println("folder("+f_list.get(j).getName()+") count msg:"+m_list.size());
                     for(int k=0;k<m_list.size();k++) {
                         System.out.println("folder("+f_list.get(j).getName()+") msg:"+m_list.get(k));
                            
                     }
                  }
              }
              
       }

}
