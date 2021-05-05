/*
 * Created on 24.12.2020
 * Modification 24/12/2020
 *
 */
package org.little.zmq.qdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

//------------------------------------------------
public class db_msg{
       final private static String CLASS_NAME="org.little.qdb.db_msg";
       final private static int    CLASS_ID  =204;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       private String  id;
       private int     user_id;
       private Date    create_time;
       private byte [] msg;

       public db_msg(){
              id=null;        
              user_id=0;   
              create_time=null;
              msg=null;       
       }


       public void set(ResultSet r) throws SQLException{
              id         =r.getString(1);  
              user_id    =r.getInt   (2);  
              create_time=new Date(r.getDate  (3).getTime());  
              msg        =r.getBlob(4).getBytes(1,0);  
       }

       public String getId() {return id;}
	   public void setId(String id) {this.id = id;}
	   public int getUser_id() {return user_id;}
	   public void setUser_id(int user_id) {this.user_id = user_id;}
	   public Date getCreate_time() {return create_time;}
	   public void setCreate_time(Date create_time) {this.create_time = create_time;}
	   public byte[] getMsg() {return msg;}
	   public void setMsg(byte[] msg) {this.msg = msg;}


}

