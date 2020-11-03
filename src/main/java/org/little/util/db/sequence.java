package org.little.util.db;

import java.sql.SQLException;

//import prj0.util.Logger;
/** 
 * È ﬁﬂﬂ Sequence ⁄¡ ⁄≈–ﬂ⁄ Õ¿Õ Õ¬…Õ» ƒ ⁄ ŒÕﬂ ≈ƒÕ¡ﬁ–≈ ›ÃÕﬂ–’ ¡ ‡ﬁ√≈
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2002-2017
 * @version 1.3
 */
public class sequence {
       private final static String CLASS_NAME="prj0.util.db.sequence";
       private final static int    CLASS_ID  =207;
             public        static String getClassName(){return CLASS_NAME;}
             public        static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       private              String id;	
       /**
        * @param _id string
        */
       public sequence(String _id) {
               this.id = _id;
       }


       public String String_Next() {
               return id + ".NEXTVAL";
       }
       public String get(query q) throws dbExcept {
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

