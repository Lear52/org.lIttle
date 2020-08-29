package org.little.proxy.util;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * class @pointHost collection
 * */

public class listPointHost {

    private static final Logger LOG = LoggerFactory.getLogger(listPointHost.class);

    /**
     * контейнер локальной коллекция
     * */
    private ArrayList<pointHost> list_host;
    private static  int          count_id=0;
    /**
     * ссылка на глобальную коллекцию
     * */
    private ArrayList<pointHost> global_list_host;


    public listPointHost(){
                  list_host=new ArrayList<pointHost>(10);
                  global_list_host=null;
    }
    public listPointHost(ArrayList<pointHost> _global_list_host){
                  list_host=new ArrayList<pointHost>(10);
                  global_list_host=_global_list_host;
    }
    protected synchronized int getID() {
           count_id++;
           return count_id;
    }
    public pointHost getPoint() {
        for(int i=0;i<list_host.size();i++){
            pointHost h=list_host.get(i);
            if(h.isActive())return h;
        }
        return null;
    }
    public String  getHostPort(){
           pointHost h=getPoint();
           if(h!=null)return h.getHostPort();
           return null;
    }
    public String  getHost(){
        pointHost h=getPoint();
        if(h!=null)return h.getHost();
        return null;
    }
    public int  getPort(){
        pointHost h=getPoint();
        if(h!=null)return h.getPort();
        return 0;
    }
    public ArrayList<pointHost>  get(){
           return list_host;
    }

    public ArrayList<String>  getArrayString(){

           ArrayList<String>  list=new ArrayList<String>(list_host.size());
               for (int i = 0; i < list_host.size(); i++) {
                    pointHost hp = list_host.get(i);
                    String s=hp.getString();
                    list.add(s);
                    LOG.trace("String:"+s);
               }
               LOG.trace("String size:"+list.size());
           return list;
    }

    public void addHost(String h,boolean _is_active){

           if(global_list_host!=null) {
               for (int i = 0; i < global_list_host.size(); i++) {
                   pointHost hp = global_list_host.get(i);
                   if (hp.isEquals(h)) {
                       list_host.add(hp);
                       return;
                   }
               }
           }
           pointHost hp=new pointHost(h);
           hp.setID(getID());
           hp.isActive(_is_active);
           list_host.add(hp);
           if(global_list_host!=null)global_list_host.add(hp);

    }
    public void init(Node node_cfg,ArrayList<pointHost> _global_list_host){
        global_list_host=_global_list_host;
        init(node_cfg);
    }
    public void init(Node node_cfg){

           if(node_cfg==null)return;

           NodeList hlist=node_cfg.getChildNodes();
                   
           for(int i=0;i<hlist.getLength();i++){
               Node nn=hlist.item(i);
               //<host>host_name</host>
               if("host".equals(nn.getNodeName())){
                  NamedNodeMap at=nn.getAttributes();
                  boolean _is_active=true;
                  String  s_at;
                  String  _active;
                  s_at="active";
                  if(at.getNamedItem(s_at)!=null){
                     _active=at.getNamedItem(s_at).getNodeValue();
                     try{_is_active=Boolean.parseBoolean(_active);}catch(Exception e){ _is_active=false; LOG.error("_is_active:"+_active);}
                  }
                  String t_host=nn.getTextContent();
                  LOG.info("Load configuration node:"+node_cfg.getNodeName()+" host:"+t_host);
                  addHost(t_host,_is_active);
               }
          }
    }

}

