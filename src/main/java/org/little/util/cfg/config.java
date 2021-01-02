package org.little.util.cfg;

import java.util.ResourceBundle;

import org.little.util.Except;
import org.little.util.Logger;

/** 
  Класс Config обеспечивает работу с файло конфигураци
  (оболочка над ResourceBundle)
  
  @author <b>Andrey Shadrin</b>, Copyright &#169; 2009-2017
  @version 1.0
 */
public class config implements iconfig {
       final private static String CLASS_NAME="org.little.util.config";
       final private static int    CLASS_ID  =102;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);
	//----------------------------------------------------------------------------
        //
        //----------------------------------------------------------------------------
        String         topic;
        iconfig        sub_cfg;
        ResourceBundle rb;


	public String getTopic(){if(topic==null)return "";else return topic;};
        /**
         * Открытие темы в файле конфигурации
         * @param sub_cfg - ссылка на общий файл конфигурации
         * @param topic префикс темы ([sis.]   sis.basa.)
         */
        public void open(iconfig sub_cfg, String topic) {
                this.sub_cfg = sub_cfg;
                this.topic = topic;
                rb = null;
        }
        /**
         * Открытие файла конфигурации
         * @param resourse_file - файл конфигурации
         */
        public void open(String resourse_file) throws Except {
                try {
                    rb = ResourceBundle.getBundle(resourse_file);
                } catch (Exception e) {
                    log.trace("error open config file "+resourse_file + "ex:"+e);
                    Except ex = new Except("Can`t load resourse " + resourse_file,e);
                    throw ex;
                }
                log.trace("open config file "+resourse_file);
        };
        /**
         * закрытие файла конфигурации
         */
        public void close() {
                rb = null;
                sub_cfg = null;
                topic = null;
        }

        /**
         * Получение строки по заголовку
         *
         * @param item - заголовок строки
         */
        public String get(String item) throws Except {
                String r;
                if(rb == null) {
                   if(sub_cfg == null || topic == null) {
                      throw new Except("resourse is't open config");
                   }
                   return sub_cfg.get(topic +"."+ item);
                }
                try{
                   r = rb.getString(item);
                } catch (Exception e) {
                        r = null;
                }
                return r;
        }
        public long getLong(String item){
                long r;
    	        try {
                     String s=get(item);r=Long.parseLong(s,10);
                } catch (Exception e) {r = -1;}
                return r;
        }
        public int getInt(String item){
                int r;
    	        try {
                     String s=get(item);r=Integer.parseInt(s,10);
                } catch (Exception e) {r = -1;}
                return r;
        }
        //----------------------------------------------------------------------------
        //
        //----------------------------------------------------------------------------
        public static void main(String[] args){
               config cfg=new config();
               iconfig cfg2=new config();
               try {
                    cfg.open(org.little.util.cfg.def.def_cfg_name);
                    cfg2.open(cfg,"db");
                    System.out.println("ok");
               } catch (Exception e) {
                        e.printStackTrace();
               }
        }
}
