package org.little.db.kir;
                    
import java.io.*;
import java.util.*;
import org.little.util.*;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.*;


public class listKIR{

       private static final Logger logger = LoggerFactory.getLogger(listKIR.class);
       private String         work_path;
       private String         current_filename;
       private String         flash_filename;
       private FileReader     fin;   
       private BufferedReader in;   
       private FileWriter     fout;   
       private BufferedWriter out;   

       public listKIR(String _work_path) {
              work_path=_work_path;
              clear();
              logger.trace("create listKIR:"+work_path);
       }

       protected void clear() {
              current_filename=work_path+"out.dat";
              flash_filename=".dat";
              fin=null;      
              in=null;      
              fout=null;      
              out=null;      
       }


       public void open() {
                   openOUT();
       }

       public boolean write(objKIR obj) {if(out==null)return false;try{out.write(obj.printXML());out.flush();}catch (IOException e) { closeOUT();return false;}return true;}


       public void close(){closeOUT();closeIN();}
       
       //------------------------------------------------------------------------------------------------------------------------
       public void createFlush() {
                   flash_filename=work_path+"flash.dat";
                   closeOUT();
                   File f1 = new File(current_filename);
                   File f2 = new File(flash_filename);
                   logger.info(f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   openOUT();
                   openIN();
       }


       public String readLine()      {if(in==null)return null;try{return in.readLine();}catch (IOException e) { return null;}}
       public int    read()          {if(in==null)return -1;  try{return in.read();    }catch (IOException e) { return -1;}}
       public int    read(char[] buf){if(in==null)return -1;  try{return in.read(buf); }catch (IOException e) { return -1;}}

       public void revertFlush() {
                   closeIN();
                   closeOUT();
                   String tmp_filename=work_path+"tmp.dat";
                   {
                   File f1 = new File(current_filename);
                   File f2 = new File(tmp_filename);
                   logger.info(f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   }
                   {
                   File f1 = new File(flash_filename);
                   File f2 = new File(current_filename);
                   logger.info(f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   }
                   openOUT();

                   tmp_filename=work_path+"flash.dat";
                   {
                   File f1 = new File(tmp_filename);
                   File f2 = new File(flash_filename);
                   logger.info(f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   }
                   openIN();
                   int howmany;
                   try{
                      while ((howmany = in.read()) >= 0) {out.write(howmany);}
                      out.flush();
                   }
                   catch (IOException e) { ;}
                   closeIN();

                   {
                   File f1 = new File(flash_filename);
                   logger.info((f1.delete() ? "Deleted " :"Could not delete ") + f1.getPath());
                   }

       }
       public void commitFlush() {
                   closeIN();
                   String new_filename=work_path+stringDate.date2str(new Date())+".dat";
                   {
                   File f1 = new File(flash_filename);
                   File f2 = new File(new_filename);
                   logger.info(f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   }
       }
       //------------------------------------------------------------------------------------------------------------------------
       private void openOUT() {
              try{
                  fout=new FileWriter(current_filename);
                  out =new BufferedWriter(fout);
              }
              catch (IOException e){
                   logger.error("ex:"+new Except("open out filename:"+current_filename,e));
                   closeOUT();
              }
       }
       private void openIN() {
              try{
                  fin=new FileReader(flash_filename);
                  in =new BufferedReader(fin);
              }
              catch (IOException e){
                   logger.error("ex:"+new Except("open int filename:"+flash_filename,e));
                   closeIN();
              }
       }
       private void closeOUT() {
              if(out !=null)try{ out.flush(); out.close();}catch (IOException e){}out=null;
              if(fout!=null)try{fout.close();}catch (IOException e){}fout=null;
       }
       private void closeIN() {
              if(in !=null)try{ in.close();}catch (IOException e){}in=null;
              if(fin!=null)try{fin.close();}catch (IOException e){}fin=null;
       }



       public static void main(String args[]){
              int metod=0;
              listKIR list=new listKIR("./");
              list.createFlush();
              try {
                    Writer out = new BufferedWriter(new FileWriter(args[0]));
                    
                    if(metod==0){
                       String str;
                       while ((str = list.readLine()) != null) {
                             out.write(str);
                       }
                    }
                    else
                    if(metod==1){
                         int howmany;
                         char[] buf = new char[512];
                         while ((howmany = list.read(buf)) >= 0) {
                                out.write(buf, 0, howmany);
                         }
                    }
                    else
                    if(metod==2){
                         int howmany;
                         while ((howmany = list.read()) >= 0) {
                                out.write(howmany);
                         }
                    }

                    out.flush();
                    out.close();
                    list.commitFlush();
              } catch (IOException e) {
                  list.revertFlush();
                  System.err.println(e);
              }

       }
}
