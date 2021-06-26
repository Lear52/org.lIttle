package org.little.mq.file.util;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class fileElement{
       private static final Logger log = LoggerFactory.getLogger(fileElement.class);
       public  static String getClassName(){return fileElement.class.getName();}

       private File in_file;
       private File out_file;
       private long start;


       public fileElement() {
              clear();
       }
       public void clear() {
              in_file=null;
              out_file=null;
              start   =0;
       }
       public long getStart() {
       return start;
       }
       public boolean remove() {
              if(out_file!=null)return in_file.renameTo(out_file);               
              return false;
       }
       public String getName() {
              return in_file.getPath();
       }
       public String getOut() {
              return out_file.getPath();
       }
       public File getIn() {
       return in_file;
       }

       public static ArrayList<fileElement> getDir(String _in_dirname,String _out_dirname,long time_range){
              ArrayList<fileElement> list=new ArrayList<fileElement>(100);
              File dir = new File(_in_dirname);
              File[] ls = dir.listFiles();
              int i;
              if(ls==null){
                 log.info("get dir:"+_in_dirname+" is empty");
                 return null;
              }
              for (i = 0;i < ls.length;i++){
                   fileElement f=new fileElement();
                   if(ls[i].isFile()){
                      f.in_file=ls[i];
                      if(_out_dirname!=null)f.out_file=new File(_out_dirname,ls[i].getName());
                      list.add(f);
                   }

              }
              if(list.size()==0)return null;

              Collections.sort(list,new Comparator<fileElement>() 
                                   {
                                     @Override
                                     public int compare(fileElement n1,fileElement n2){
                                           return n1.getName().compareToIgnoreCase(n2.getName());
                                     }
                                   }
              );

              long st=System.currentTimeMillis();
              //time_range*=1000;
              time_range/=list.size();
              for(i=0;i<list.size();i++){
                  list.get(i).start=st+time_range*i;
              }

              log.info("get dir:"+_in_dirname+" size:"+list.size());

              return list;
       }
       public static void work(String in_dirname,String out_dirname,long time_range ){
              ArrayList<fileElement> list=fileElement.getDir(in_dirname,out_dirname,time_range);

              if(list==null){
                 log.trace("dirname:"+in_dirname+" is_empty");
                 return;
              }

              //------------------------------------------------
              while(list.size()>0)
              for(int i=0;i<list.size();i++){
                  fileElement o=list.get(i);
                  long t=System.currentTimeMillis()-o.start;
                  if(t>=0){
                     o.getIn();
                     o.remove();
                     o.clear();
                     list.remove(i);
                     break;
                  }
                  else{
                       try{Thread.sleep(t);}catch(Exception e){}
                  }
                  
              }
       }

       public static void help(String[] args) {
              System.out.println("run java "+getClassName()+" in_dirname time_range_sek [remove_dir_name]");
       }
       public static void main(String[] args) {

              if(args.length<2){
                 help(args);
                 return;
              }
              if(args.length==2){
                 ArrayList<fileElement> list=fileElement.getDir(args[0],null,0);
                 for(int i=0;i<list.size();i++)System.out.println("> "+list.get(i).getName() );
                 return;
              }
              if(args.length==3){
                 fileElement.getDir(args[0],args[2],0);
                 return;
              }


       }

}
