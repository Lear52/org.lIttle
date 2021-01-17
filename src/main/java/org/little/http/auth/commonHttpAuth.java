package org.little.http.auth;

import org.little.http.commonHTTP;
import org.little.proxy.util.statChannel;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonHttpAuth  {
       private static final Logger  logger = LoggerFactory.getLogger(commonHttpAuth.class);


       private int           type_authenticateClients;

       public commonHttpAuth(){clear();}

       public void clear(){
              type_authenticateClients       =0;
       }

       public static HttpAuth getInstatce(statChannel _stat) {
              int _type_http_auth=commonHTTP.get().getCfgHttpAuth().getTypeAuthenticateHTTPClients();

              HttpAuth ret;
              switch(_type_http_auth) {
              case HttpAuth.BASIC:  ret=new HttpAuthBasic    (HttpAuth.BASIC ,_stat);break;
              case HttpAuth.DIGEST: ret=new HttpAuthDigest   (HttpAuth.DIGEST,_stat);break;
              case HttpAuth.SPNEGO: ret=new HttpAuthNegotiate(HttpAuth.SPNEGO,_stat);break;
              default:              ret=new HttpAuthNo       (HttpAuth.NOAUTH,_stat);break;
              }
              ret.setRealm(commonHTTP.get().getCfgAuth().getRealm());

              logger.trace("HttpAuth getInstatce for authenticateClients:"+_type_http_auth+" ("+ret.getTypeAuthentication()+")");
              
              return ret;
       }
       
       public void init(NodeList glist){
              if(glist==null) return;
              logger.info("The configuration auth");
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("authenticateClients".equals(n.getNodeName())){String s=n.getTextContent(); try{type_authenticateClients=Integer.parseInt(s, 10);}catch(Exception e){ type_authenticateClients=0; logger.error("authenticateClients:"+s);} logger.info("authenticateClients:"+type_authenticateClients);}
                  //else
              }
       }

       public int      getTypeAuthenticateHTTPClients(){return type_authenticateClients;}
       


}
