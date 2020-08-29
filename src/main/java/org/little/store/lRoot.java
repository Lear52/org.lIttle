package org.little.store;

import java.util.ArrayList;

import org.little.store.fsstore.lFSRoot;
import org.little.store.fsstore.lFSStore;


public class lRoot  {

       public static lStore getStore(String _name){
              return new lFSStore(_name); 
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

                  ArrayList<lFolder> f_list =list.get(i).getListFolder();  

                  System.out.println("count folder:"+f_list.size()+" for store:"+list.get(i).getName());

                  for(int j=0;j<f_list.size();j++) {
                     System.out.println("folder("+j+"):"+f_list.get(j).getName());
                     f_list.get(j).open(lFolder.READ_WRITE);
                     ArrayList<String> m_list = f_list.get(j).getList();
                     System.out.println("folder("+f_list.get(j).getName()+") count msg:"+m_list.size());
                     for(int k=0;k<m_list.size();k++) {
                    	 System.out.println("folder("+f_list.get(j).getName()+") msg:"+m_list.get(k));
                    	 
                     }
                  }
              }
              
       }

}
