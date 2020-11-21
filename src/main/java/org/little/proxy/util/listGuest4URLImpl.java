package org.little.proxy.util;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class listGuest4URLImpl implements listGuest4URL {

    private static final Logger LOG = LoggerFactory.getLogger(listGuest4URLImpl.class);

    private String               default_user;
    private ArrayList<guest4URL> list_host_user;
    //private listPointHost             global_list_host;

    public listGuest4URLImpl(){
           default_user="noname";
           
    }

    public void init(Node node_cfg){

           list_host_user=new ArrayList<guest4URL>(10);
           if(node_cfg==null)return;
           LOG.info("The configuration node:"+node_cfg.getNodeName());
           {  String s_at="default_user";
              NamedNodeMap at=node_cfg.getAttributes();
              if(at.getNamedItem(s_at)!=null){
                 String s;
                 s=at.getNamedItem(s_at).getNodeValue();
                 setDefaultUser(s);
              }
              else{
                  LOG.info("The configuration does not contain a volue:"+s_at);
              }
            }

            NodeList plist=node_cfg.getChildNodes();
            for(int i=0;i<plist.getLength();i++){
                  Node n=plist.item(i);
                  if("p".equals(n.getNodeName())){

                     list_host_user.add(new guest4URL(n,getDefaultUser()));
                  }
            }
            LOG.info("The configuration contain guest access:"+list_host_user.size());

    }

    public void  setDefaultUser(String s){
           default_user=s;
           LOG.info("set default util:"+default_user);
    }

    public String  getDefaultUser(){
           return default_user;
    }

    public String getUser4IP(String ip){
              for(int i=0;i<list_host_user.size();i++){
                  guest4URL user_guest=list_host_user.get(i);
                  if(user_guest.isHost(ip)){
                     String u=user_guest.getUser();
                     return u;
                  }
              }
      return null;
    }

    public String getUser4URL(String url){
              for(int i=0;i<list_host_user.size();i++){
                  guest4URL user_guest=list_host_user.get(i);
                  if(user_guest.isURL(url)){
                     String u=user_guest.getUser();
                     return u;
                  }
              }
      return null;
    }

   

  
}

