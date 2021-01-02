package org.little.util.db;

import java.sql.SQLException;

/** 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2002-2021
 * @version 1.4
 */
public class sequence {
       private final static String CLASS_NAME="org.little.util.db.sequence";
       private final static int    CLASS_ID  =207;
             public        static String getClassName(){return CLASS_NAME;}
             public        static int    getClassId(){return CLASS_ID;}

       private              String id;	
       private              query  q;
       /**
        * @param _id string
        */
       public sequence(String _id) {
               this.id = _id;
               this.q = null;
       }
       public sequence(String _id,query  _q) {
               this.id = _id;
               this.q = _q;
       }


       public String String_Next() {
               return id + ".NEXTVAL";
       }
       public String get(query _q) throws dbExcept {
              if(_q==null){
                 dbExcept ex = new dbExcept("query is null");
                 throw ex;
              }
               try {
                       _q.getResult("SELECT " + id + ".NEXTVAL FROM DUAL");
                       //----------------------------------------------
                       while (_q.isNextResult()) {
                               // ŒÕ —¬≈Ã’≈ œ≈√— ›–ﬁ–ﬁ
                               return _q.Result().getString(1);
                       }
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Get result sequence(string) " + e);
                       throw ex;
               }
               //----------------------------------------------
               return null;
       }
       public String get() throws dbExcept {
              if(q==null){
                 dbExcept ex = new dbExcept("query is null");
                 throw ex;
              }
               try {
                       q.getResult("SELECT " + id + ".NEXTVAL FROM DUAL");
                       //----------------------------------------------
                       while (q.isNextResult()) {
                               // ŒÕ —¬≈Ã’≈ œ≈√— ›–ﬁ–ﬁ
                               return q.Result().getString(1);
                       }
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Get result sequence(string) " + e);
                       throw ex;
               }
               //----------------------------------------------
               return null;
       }
       public int getInt(query q) throws dbExcept {
               return (int) getLong(q);
       }

       public long getLong(query q) throws dbExcept {
               String s = get(q);
               try {
                       return Long.parseLong(s, 10);
               } catch (Exception e) {
                       dbExcept ex = new dbExcept("Get result sequence (long) " + e);
                       throw ex;
               }
       }
       //-----------------------------------------------------------------------------
}

