package org.little.mq.mqapi;


public class cfg_conn {
       final private static String CLASS_NAME="org.little.stream.mq.conn";
       final private static int    CLASS_ID  =1702;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       private String          qmname;
       private String          hostname;
       private int             port;
       private String          channel;
       private boolean         local;
       private String          user;
       private String          password;

       public String     getQMName   (){return qmname  ;}
       public String     getHost   (){return hostname;}
       public int        getPort   (){return port    ;}
       public String     getChannel(){return channel ;}
       public boolean    isLocal   (){return local   ;}
       public String     getUser()            {return user;}
       protected String  getPassword(){return password; }

       public void       setUser(String u)    {user=u;     }
       public void       setPassword(String p){password=p; }
       public void       setQMName   (String _qmname ){ this.qmname   =_qmname ;}
       public void       setHost   (String _host   ){ this.hostname =_host   ;}
       public void       setPort   (int    _port   ){ this.port     =_port   ;}
       public void       setChannel(String _channel){ this.channel  =_channel;}
       public void       isLocal   (boolean l){local=l;}

       public synchronized void clear() {
              qmname    =null;
              hostname  =null;
              port      =1414;
              channel   =null;
              local     =true;
       }
       
      


       public cfg_conn(){clear();}
      
      


}


