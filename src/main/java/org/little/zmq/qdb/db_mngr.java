/*
 * Created on 24.12.2020
 * Modification 24/12/2020
 *
 */
package org.little.zmq.qdb;

import java.util.ArrayList;

//------------------------------------------------
public class db_mngr{
       final private static String CLASS_NAME="org.little.qdb.db_mngr";
       final private static int    CLASS_ID  =204;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       private String  name;
       private ArrayList<db_queue> list_queue;

       public db_mngr(){
              setName(null);
              setList_queue(new ArrayList<db_queue>(10));
       }
	
       
       public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<db_queue> getList_queue() {
		return list_queue;
	}
	public void setList_queue(ArrayList<db_queue> list_queue) {
		this.list_queue = list_queue;
	}




}

