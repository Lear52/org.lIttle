package org.little.imap.command.cmd.fetch;

import java.util.ArrayList;

public class FetchRequest {

       public final static int FLAGS        =1;
       public final static int UID          =2;
       public final static int INTERNALDATE =3;
       public final static int SIZE         =4;
       public final static int ENVELOPE     =5;
       public final static int BODY         =6;
       public final static int BODY_PEEK    =7;
       public final static int BODYSTRUCTURE=8;
       public final static int RFC822       =9;
       public final static int RFC822_TEXT   =10;
       public final static int RFC822_HEADER=11;
	
       public boolean flags        ;
       public boolean uid          ;
       public boolean internaldate ;
       public boolean size         ;
       public boolean envelope     ;
       public boolean body         ;
       public boolean body_peek    ;
       public String  body_section ;
       public String  size_section ;
       public boolean bodystructure;
       public boolean rfc822       ;
       public boolean rfc822_text  ;
       public boolean rfc822_header;
       
       private ArrayList<FetchElement> list_element;
       
       public FetchRequest(){
    	       list_element=new ArrayList<FetchElement>();
    	       
              flags         = false;        
              uid           = false;          
              internaldate  = false; 
              size          = false;        
              envelope      = false;
              body          = false;
              body_peek     = false;
              body_section  = null;
              size_section  = null;
              bodystructure = false;
              rfc822        = false;
              rfc822_text   = false;
              rfc822_header = false;   
              
       }

       public void add(int _type) {list_element.add(new FetchElement(_type));}
       public void add(int _type,String _arg) {list_element.add(new FetchElement(_type, _arg));}
       
       public ArrayList<FetchElement> get(){return list_element;}
       

}


