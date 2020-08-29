package org.little.proxy.util;

import java.util.Hashtable;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**


*/

public class listHost4UserImpl implements listHost4User {

    private static final Logger LOG = LoggerFactory.getLogger(listHost4UserImpl.class);

    private Hashtable<String, host4URL> list;
    private host4URL                    default_host;
    private listPointHost               global_list_host;

    public listHost4UserImpl(listPointHost _global_list_host){

           global_list_host=_global_list_host;

           list            =new Hashtable<String, host4URL>(100);
          
           //setDefaultHostPort("localhost:8080");
           boolean _is_active_path=false;
           default_host=new host4URL(null,null,"localhost:8080",_is_active_path,null);// empty default_host
    }

    @Override
    public String    getHostPort(String user)           {
                     if(list!=null && user!=null){
                        host4URL lh=list.get(user);
                        if(lh!=null){
                           return lh.get().getHostPort();
                        }
                     }
                     return getDefaultHostPort();
    }
    @Override
    public String    getHostPort(String user,String url){
                     if(list!=null){
                        host4URL lh=list.get(user);
                        if(lh!=null){
                           return lh.get().getHostPort();
                        }
                     }
                     return getDefaultHostPort();
    }
    @Override
    public host4URL getDefault()                      {return default_host;}
    @Override
    public String   getDefaultHostPort()               {return default_host.get().getHostPort();}
    @Override
    public void     setDefaultHostPort(String h,boolean _is_active_path)       {
                    default_host=new host4URL(null,null,h,_is_active_path,global_list_host);
                    LOG.info("SetDefaultHostPort:"+h);
    }
    @Override
    public String   getDefaultHost()                   {return default_host.get().getHost();}
    @Override
    public int      getDefaultPort()                   {return default_host.get().getPort();}


    public void     init(Node node_cfg){

           if(node_cfg==null)return;
           LOG.info("The configuration node:"+node_cfg.getNodeName());
           //-----------------------------------------------------------
           {
              NodeList plist=node_cfg.getChildNodes();     

              for(int i=0;i<plist.getLength();i++){
                  //----------------------------------------------------
                  Node   n     =plist.item(i);
                  host4URL t_uhost=new host4URL(n,global_list_host);
                  String t_user;
                  t_user=t_uhost.getUser();
                  //-------------------------
                  if(t_user!=null){
                     list.put(t_user,t_uhost);
                  }
                  else 
                  if(t_uhost.isDefault()){
                     default_host=t_uhost;
                  }
                  //-------------------------
              }
           //-----------------------------------------------------------
           }

   }





}

