package org.little.util;


public class arrbyte {
       private byte[] buf;

       public arrbyte(){buf=null;}
       public arrbyte(byte[] _buf){set(_buf);}

       public void   set(byte[] _buf){buf=_buf;}
       public byte[] get(){return buf;}

}