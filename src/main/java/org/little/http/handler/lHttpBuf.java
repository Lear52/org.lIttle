package org.little.http.handler;



public class lHttpBuf{

       protected String                 name;
       protected String                 mime;
       protected byte []                bin_buf;
       

       public lHttpBuf(String f_name,String f_mime,byte[] buf){
               name=f_name;   
               mime=f_mime;   
               bin_buf=buf;
       }

       public lHttpBuf(){
               clear();
       }
       private void clear(){
               name=null;   
               mime=null;   
               bin_buf=null;
       }

       public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getMime() {
                return mime;
        }

        public void setMime(String mime) {
                this.mime = mime;
        }

        public byte[] getBuf() {
                return bin_buf;
        }

        public void setBuf(byte[] bin_buf) {
                this.bin_buf = bin_buf;
        }


}

