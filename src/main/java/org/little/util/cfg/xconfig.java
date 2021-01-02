package org.little.util.cfg; 

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
//import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.little.util.Except;
import org.little.util.Logger;


/** 
  Класс Config обеспечивает работу с файло конфигураци
  
  @author <b>Andrey Shadrin</b>, Copyright &#169; 2009
  @version 1.0
 */
public class xconfig implements iconfig {
       final private static String CLASS_NAME="org.little.util.xconfig";
       final private static int    CLASS_ID  =120;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);
        //----------------------------------------------------------------------------
        // 
        //----------------------------------------------------------------------------
        protected String topic;
        protected iconfig sub_cfg;
        protected Document doc;

        public String getTopic(){if(topic==null)return "";else return topic;};
        /**
         * Открытие темы в файле конфигурации
         * @param sub_cfg - ссылка на общий файл конфигурации
         * @param topic префикс темы ([sis.]   sis.basa.)
         */
        public void open(iconfig sub_cfg, String topic) {
               this.sub_cfg = sub_cfg;
               this.topic = topic;
        }
        /**
         * Открытие файла конфигурации
         * @param file_name - файл конфигурации
         */
        public void open(String file_name) throws Except {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                try {
                        DocumentBuilder builder;
                        builder = factory.newDocumentBuilder();
                        doc = builder.parse(file_name);
                        doc.getFirstChild().getNodeValue();
                        Node nn=doc.getFirstChild();                
                        nn.getNodeName();
                } 
                catch (Exception e) {
                       log.trace("error open config file "+file_name + "ex:"+e);
                       Except ex =         new Except("Can`t load resourse " + file_name,e);
                       throw ex;
                }
        log.trace("open config file "+file_name);
        };
        /**
         * закрытие файла конфигурации
         */
        public void close() {
                doc=null;
                sub_cfg = null;
                topic = null;
        }

        /**
         * Получение строки по заголовку
         *
         * @param item - заголовок строки
         */
        public String get(String item) throws Except {
               String r=null;
               return r;
        }
        public long getLong(String item){
                long r;
                try {String s=get(item);r=Long.parseLong(s,10);} catch (Exception e) {r = -1;}
                return r;
        }
        public int getInt(String item){
                int r;
                try {String s=get(item);r=Integer.parseInt(s,10);} catch (Exception e) {r = -1;}
                return r;
        }
        //----------------------------------------------------------------------------
        //
        //----------------------------------------------------------------------------
        public static void main(String[] args){
               xconfig cfg=new xconfig();
               iconfig cfg2=new xconfig();
               try {
                    cfg.open(def.def_cfg_name);
                    cfg2.open(cfg,"db");
                    System.out.println("ok");
               } catch (Exception e) {
                              
                    e.printStackTrace();
               }
        }
        
        
}
