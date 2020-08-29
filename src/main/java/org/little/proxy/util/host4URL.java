package org.little.proxy.util;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class host4URL {

    private static final Logger LOG = LoggerFactory.getLogger(host4URL.class);

    private String        user_name;
    private String        url_mask;
    private listPointHost host;
    private boolean       is_default;
    /*
    public host4URL(){
                  is_default=false;
                  setUser(null);
                  setURL(null);
                  host=new listPointHost(null);
                 
    }
    public host4URL(String user, String url_mask, listPointHost _global_list_host){
                  is_default=false;
                  setUser(user);
                  setURL(url_mask);
                  host=new listPointHost(_global_list_host.get());
    }
    */
    public host4URL(String user, String url_mask, String _host,boolean _is_active_path, listPointHost _global_list_host){
                  if(user!=null)is_default=false;
                  else          is_default=true;
                  setUser(user);
                  setURL(url_mask);
                  if(_global_list_host==null)host=new listPointHost();
                  else                       host=new listPointHost(_global_list_host.get());
                  host.addHost(_host,_is_active_path);
    }
    public host4URL(Node node_cfg, listPointHost _global_list_host){
                  is_default=false;
                  if(_global_list_host==null)host=new listPointHost();
                  else host=new listPointHost(_global_list_host.get());
                  init(node_cfg);
    }
    public listPointHost  get(){return host;}
    //public String         getHost(){return host.getHost();}
    //public int            getPort(){return host.getPort();}
    //public String         getHostPort(){return host.getHostPort();}
                          
    public String         getHost4User(String u){
                          if(isUser(u))return host.getHostPort();
                          else return null;
    }                     
    public String         getUser(){return user_name;}
    public String         getURL(){return url_mask;}
                          
    public boolean        isUser(String u){if(user_name==null)return false;else return user_name.equals(u);}
    public boolean        isURL(String  u){if(url_mask==null )return true; else return url_mask.equals(u);}
    public boolean        isDefault(){return is_default;}
                          
    //public void           addHost(String h){ host.addHost(h);}

    public void           setUser(String s){ user_name=s;}
    public void           setURL(String u_m){url_mask=u_m;}
    public void           init(Node node_cfg){
                          if("p".equals(node_cfg.getNodeName())){
                             LOG.trace("node:"+node_cfg.getNodeName());
                             NamedNodeMap at=node_cfg.getAttributes();
                             String s_at;
                             String _user;
                             s_at="user";
                             if(at.getNamedItem(s_at)!=null)_user=at.getNamedItem(s_at).getNodeValue();
                             else return;
                          
                             String _url;
                             s_at="url";
                             if(at.getNamedItem(s_at)!=null)_url=at.getNamedItem(s_at).getNodeValue();
                             else _url=null;
                          
                             setUser(_user);
                             setURL(_url);
                             host.init(node_cfg);
                             return;
                          }
                          if("default".equals(node_cfg.getNodeName())){
                             LOG.trace("node:"+node_cfg.getNodeName());
                             is_default=true;
                             setUser(null);
                             setURL(null);
                             host.init(node_cfg);
                             return;
                          }

    }


}

