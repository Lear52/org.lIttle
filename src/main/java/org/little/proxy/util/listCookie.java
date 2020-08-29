package org.little.proxy.util;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class listCookie {

    private static final Logger LOG = LoggerFactory.getLogger(listCookie.class);
    private ArrayList<String>  list;

    public listCookie(){
           clear();
    }

    public void clear(){
           list = new ArrayList<String> (100); 
    }

    public int    size(){return list.size();}
    public String get(int i){return list.get(i);}

    public void add(String c){
           list.add(c);
    } 
    public void init(Node node_cfg){

           if(node_cfg==null)return;

           NodeList hlist=node_cfg.getChildNodes();
                   
           for(int i=0;i<hlist.getLength();i++){
               Node nn=hlist.item(i);
               if("clear".equals(nn.getNodeName())){
                  String t_coocie=nn.getTextContent();
                  LOG.info("Load configuration node:"+node_cfg.getNodeName()+" coocie:"+t_coocie);
                  add(t_coocie);
               }
          }
    }

}
