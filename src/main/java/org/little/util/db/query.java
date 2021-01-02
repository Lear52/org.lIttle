package org.little.util.db;
/**
 * Класс Query выполняет запросы к базе данных
 * 
 * 
 * @author <b>Andrey Shadrin </b>, Copyright &#169; 2002 - 2021
 * @version 1.4
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import javax.sql.rowset.serial.SerialBlob;


public class query {
       private final static String CLASS_NAME="org.little.util.db.query";
       private final static int    CLASS_ID  =205;
             public        static String getClassName(){return CLASS_NAME;}
             public        static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       protected            int               id;
       protected            Connection        con;
       protected            Statement         stmt;
       protected            PreparedStatement p_stmt;
       protected            ResultSet         rset;
       protected            query_info        info;

       //-----------------------------------------------------------------------------
       //
       //-----------------------------------------------------------------------------
       public query() {
               con    = null;
               id     = -1;
               stmt   = null;
               p_stmt = null;
               rset   = null;
               info   = new query_info();
       }
       public void close() {
                   info.stop();
       }
       public void clear() {
               id  = -1;
               if(rset!=null){   try {rset.close();  } catch (Exception e1) {} rset=null;}
               if(stmt!=null){   try {stmt.close();  } catch (Exception e1) {} stmt = null;}
               if(p_stmt!=null){ try {p_stmt.close();} catch (Exception e1) {} p_stmt = null;}
               info = new query_info();
               if(con!=null){    try {con.close();   } catch (Exception e1) {}        con = null;}
       }

       public void setId(int _id){id=_id;}

       public int getId(){return id;}

       void creatSt() throws dbExcept {
               if(con == null) throw new dbExcept("Connection null");
               try {
                       stmt = con.createStatement();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Create statement", e);
                       throw ex;
               }
       }

       public void creatPreSt(String str) throws dbExcept {
               if(con == null) throw new dbExcept("Connection null");
               try {
                       p_stmt = con.prepareStatement(str);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Create prepare statement for ("+str+")", e);
                       throw ex;
               }
       }

       public void closePreSt() throws dbExcept {
               if(p_stmt != null)
               try {
                       p_stmt.close();
                       p_stmt = null;
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Close prepare statement", e);
                       throw ex;
               }
       }

       PreparedStatement PreSt() {
               return p_stmt;
       }

       public void commit(boolean is_auto) throws dbExcept {

               if(con == null) throw new dbExcept("Connection null");
               try {
                       con.setAutoCommit(is_auto);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set commit", e);
                       throw ex;
               }
       }

       public void commit() throws dbExcept {
               if(con == null) throw new dbExcept("Connection null");
               try {
                       con.commit();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t commit", e);
                       throw ex;
               }
       }

       public void rollback() throws dbExcept {
               if(con == null) throw new dbExcept("Connection null");
               try {
                       con.rollback();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set rollback", e);
                       throw ex;
               }
       }

       //-----------------------------------------------------------------------------
       //
       //-----------------------------------------------------------------------------
       public void readonly(boolean is_readonly) throws dbExcept {
               if(con == null) throw new dbExcept("Connection null");
               try {
                       con.setReadOnly(is_readonly);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set read only", e);
                       throw ex;
               }
       }

       //-----------------------------------------------------------------------------
       //
       //-----------------------------------------------------------------------------
       public ResultSet Result() {
               return rset;
       }

       /**
        * @param select
        * @return ResultSet
        */
       public ResultSet getResult(String select) throws dbExcept {
               if(stmt == null) throw new dbExcept("Statement null");
               try {
                       rset = stmt.executeQuery(select);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t get result query ", e);
                       throw ex;
               }
               return rset;
       }

       //-----------------------------------------------------------------------------
       //
       //-----------------------------------------------------------------------------
       public void execute(String q) throws dbExcept {

               if(stmt == null) throw new dbExcept("Statement null");
               try {
                       //int ret = 
                    		   stmt.executeUpdate(q);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t exec query", e);
                       throw ex;
               }
       }

       public boolean execute() throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                       return p_stmt.execute();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t exec prepared statement", e);
                       throw ex;
               }
       }

       public ResultSet executeQuery() throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                       rset = p_stmt.executeQuery();
                       return rset;
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t exec prepared statement", e);
                       throw ex;
               }
       }

       public int executeUpdate() throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                       return p_stmt.executeUpdate();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t exec prepared statement", e);
                       throw ex;
               }
       }

       public void  addBatch() throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                       p_stmt.addBatch();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t exec prepared statement", e);
                       throw ex;
               }
       }
       public int [] executeBatch() throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                       return p_stmt.executeBatch();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t exec prepared statement", e);
                       throw ex;
               }
       }

       //-----------------------------------------------------------------------------
       //
       //-----------------------------------------------------------------------------
       public boolean isNextResult() throws dbExcept {
               boolean ret;
               if(rset==null)return false;
               try {
                       ret = rset.next();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t get next result", e);
                       throw ex;
               }

               return ret;
       }

       //-----------------------------------------------------------------------------
       //
       //-----------------------------------------------------------------------------
       public static StringBuffer SQL_Mask_I(String s) {
               return SQL_Mask_I(new StringBuffer(s));
       }

       public static StringBuffer SQL_Mask_I(StringBuffer buf) {

               for (int i = 0; i < buf.length(); i++) {
                       char c;
                       c = buf.charAt(i);
                       if (c == '\'') {
                               buf.insert(i, '\'');
                               i++;
                       }
               }
               return buf;
       }

       public static StringBuffer SQL_Mask_S(String s) {
               return SQL_Mask_S(new StringBuffer(s));
       }

       public static StringBuffer SQL_Mask_S(StringBuffer buf) {
               char[] t = { '%', '\'' };

               for (int i = 0; i < buf.length(); i++) {
                       char c;
                       c = buf.charAt(i);
                       for (int j = 0; j < t.length; j++) {
                               if (c == t[j]) {
                                       buf.insert(i, '%');
                                       i++;
                                       break;
                               }
                       }
               }

               return buf;
       }

       //-------------------------------------------------------------------------
       public void setNull(int i, int j) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setNull(i, j);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setArray(int i, Array ar) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setArray(i, ar);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }
       public void setBoolean(int i, boolean flag) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setBoolean(i, flag);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setByte(int i, byte byte0) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setByte(i, byte0);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setShort(int i, short word0) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setShort(i, word0);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setInt(int i, int j) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setInt(i, j);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setLong(int i, long l) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setLong(i, l);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setFloat(int i, float f) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setFloat(i, f);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setDouble(int i, double d) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setDouble(i, d);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setBigDecimal(int i, BigDecimal bigdecimal) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setBigDecimal(i, bigdecimal);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setString(int i, String s) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setString(i, s);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setBytes(int i, byte abyte0[]) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setBytes(i, abyte0);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }
 
       public void setDate(int i, java.sql.Date date) throws dbExcept {
               //if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    if(p_stmt == null)p_stmt.setNull(i,java.sql.Types.DATE);
                    else              p_stmt.setDate(i, date);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setTime(int i, Time time) throws dbExcept {
               //if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                       if(p_stmt == null)p_stmt.setNull(i,java.sql.Types.TIME);
                       else              p_stmt.setTime(i, time);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setTimestamp(int i, Timestamp timestamp) throws dbExcept {

               //if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                       if(p_stmt == null)p_stmt.setNull(i,java.sql.Types.TIMESTAMP);
                       else              p_stmt.setTimestamp(i, timestamp);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setAsciiStream(int i, InputStream inputstream, int j) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                       p_stmt.setAsciiStream(i, inputstream, j);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setBinaryStream(int i, InputStream inputstream, int j) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setBinaryStream(i, inputstream, j);
               } catch (SQLException e) {
                    e.printStackTrace();

                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void clearParameters() throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.clearParameters();
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setObject(int i, Object obj, int j, int k) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setObject(i, obj, j, k);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setObject(int i, Object obj, int j) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setObject(i, obj, j);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setObject(int i, Object obj) throws dbExcept {
               if(p_stmt == null) throw new dbExcept("Statement null");
               try {
                    p_stmt.setObject(i, obj);
               } catch (SQLException e) {
                       dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                       throw ex;
               }
       }

       public void setBLOB1(int i,  byte byte0[]) throws dbExcept {
       	      ByteArrayInputStream is=new ByteArrayInputStream(byte0);
              int len=byte0.length;
              setBinaryStream(i,is,len);
       }
       public void setBLOB(int i,  byte byte0[]) throws dbExcept {
           if(p_stmt == null) throw new dbExcept("Statement null");
           try {
                Blob b=new SerialBlob(byte0);
				p_stmt.setBlob(i, b);
           } catch (SQLException e) {
                   dbExcept ex = new dbExcept("Can`t set parametr statement ", e);
                   throw ex;
           }
    }

}



