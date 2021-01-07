package org.little.util;

import org.little.util.string.stringTransform;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonServer{

       private static final Logger logger = LoggerFactory.getLogger(commonServer.class);
      
       private int           bind_port                      ;
       private String        local_bind_client              ;
       private String        local_bind_server              ;
       private boolean       is_dump_log                    ;

      
       public commonServer(){clear();}

       public void clear(){
              bind_port                      =2525;
              local_bind_client              ="*";
              local_bind_server              ="*";
              is_dump_log                    =false;

       }
       public void init(NodeList glist){
               if(glist==null)return;
               logger.info("The configuration server node");

               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);
                     if("port"                           .equals(n.getNodeName())){String s=n.getTextContent(); try{bind_port=Integer.parseInt(s, 10);               }catch(Exception e){ bind_port=8080;            logger.error("bind default port:"+bind_port);} logger.info("Bind cfg port:"+bind_port);                          }
                     else
                     if("local_bind_client"              .equals(n.getNodeName())){local_bind_client=n.getTextContent();              logger.info("local_bind_client:"+local_bind_client);             }
                     else
                     if("local_bind_server"              .equals(n.getNodeName())){local_bind_server=n.getTextContent();              logger.info("local_bind_server:"+local_bind_server);             }
                     else
                     if("dump_log"                       .equals(n.getNodeName())){String s=n.getTextContent(); try{is_dump_log=Boolean.parseBoolean(s);                }catch(Exception e){ is_dump_log=false;logger.error("dump_log:"+s);} logger.info("dump_log:"+is_dump_log);}
               }
               if(stringTransform.isEmpty(local_bind_server))local_bind_server="*";
               if(stringTransform.isEmpty(local_bind_client))local_bind_server="*";

       }

       public int           getPort                   (){return bind_port;                      }
       public String        getLocalServerBind        (){return local_bind_server;              }
       public String        getLocalClientBind        (){return local_bind_client;              }
       public boolean       isDumpLog                 (){return is_dump_log;          }

}

