package org.little.proxy.util;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.stringWildCard;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class guest4URL {

    private static final Logger LOG = LoggerFactory.getLogger(guest4URL.class);

    private String   user_name;
    private String   host_mask;
    private String   url_mask;

    public guest4URL(){
                  setUser(null);
                  setHost(null);
    }
    public guest4URL(String _user, String _host, String _url){
                  setUser(_user);
                  setHost(_host);
                  setURL(_host);
    }
    public guest4URL(Node node_cfg, String def_user){
                   init(node_cfg,def_user);
    }

    public String  getHost(){return host_mask;}
    public String  getUser(){return user_name;}
    public String  getURL(){return url_mask;}


    public boolean isHost(String  ip){
                   if(host_mask==null || ip==null)return false; 
                   else return stringWildCard.wildcardMatch(ip, host_mask,null);
    }
    public boolean isURL(String  url){
                   boolean ret=false;

                   if(url_mask==null || url==null)ret=false; 
                   else ret=stringWildCard.wildcardMatch(url, url_mask,null);

                   //LOG.info("access u_mask:"+url_mask+" url:"+url+" h_mask:"+host_mask+" ret:"+ret);/**/
                   return ret;
    }

    public void    setUser(String s){ user_name=s;}
    public void    setHost(String h_m){host_mask=h_m;}
    public void    setURL(String u_m){url_mask=u_m;}

    public void    init(Node node_cfg,String def_user){
                   if("p".equals(node_cfg.getNodeName())){
                      NamedNodeMap at=node_cfg.getAttributes();
                      String s_at;
                      String _user;
                      s_at="user";
                      if(at.getNamedItem(s_at)!=null)_user=at.getNamedItem(s_at).getNodeValue();
                      else _user=def_user;

                      String _host=null;
                      s_at="host";
                      if(at.getNamedItem(s_at)!=null)_host=at.getNamedItem(s_at).getNodeValue();

                      String _url=null;
                      s_at="url";
                      if(at.getNamedItem(s_at)!=null)_url=at.getNamedItem(s_at).getNodeValue();

                      setUser(_user);
                      setHost(_host);
                      setURL(_url);

                      LOG.info("node:"+node_cfg.getNodeName()+" guest access host:"+_host+" url:"+_url+" map to user:"+_user);

                      return;
                   }

    }
}

