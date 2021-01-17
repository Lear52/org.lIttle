package org.little.proxy.util;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class listChannel {

    private static final Logger LOG = LoggerFactory.getLogger(listChannel.class);
    private ArrayList<statChannel>  list;

    public listChannel(){
           clear();
    }

    public void clear(){
           list = new ArrayList<statChannel> (100); 
    }

    public statChannel get(ChannelHandlerContext cnx){
           return get(cnx.channel());
    }
    public statChannel get(Channel _channel){
           return get(_channel.id().asShortText());
    }

    public statChannel get(String id){
           for(int i=0;i<list.size();i++){
               statChannel _ch=list.get(i);
               if(_ch.isId(id)){
                  return _ch;
               }
           }
           return null;
    }
    private synchronized statChannel create(){
           for(int i=0;i<list.size();i++){
               statChannel _ch=list.get(i);
               if(_ch.isEmpty()){
                  _ch.isEmpty(false);
                  return _ch;
               }
           }
           statChannel ch=new statChannel();
           ch.isEmpty(false);
           list.add(ch);
           LOG.trace("create statChannel empty");
           return ch;
    } 
    public synchronized statChannel create(Channel _channel){
           LOG.trace("create statChannel empty");
           statChannel ch=create();
           ch.setChannel(_channel);
           return ch;
    } 
    public synchronized void remove(statChannel ch){
           if(ch==null)return;
           LOG.trace("remove statChannel id:"+ch.getId());
           for(int i=0;i<list.size();i++){
               statChannel _ch=list.get(i);
               if(_ch==ch&&_ch.isEmpty()==false){
                  LOG.trace("1remove  id:"+ch.getId()+" OK");
                  _ch.clear();
                  list.set(i,new statChannel());
                  break;
               }
           }
        
    } 
    public synchronized void remove(String _id){
           if(_id==null)return;
           LOG.trace("remove statChannel id:"+_id);
           for(int i=0;i<list.size();i++){
               statChannel _ch=list.get(i);
               if(_ch.getId().equals(_id)&&_ch.isEmpty()==false){
                  LOG.trace("remove  id:"+_id+" OK");
                  list.set(i,new statChannel());
                  _ch.clear();
                  break;
               }
           }
        
    } 
    public int getAllCountChannel(){
           int count=0;
           for (int i = 0; i < list.size(); i++) {
                statChannel _ch = list.get(i);
                if(_ch.isEmpty())continue;
                count++;
           }
           return count;
    }
    public int getFrontCountChannel(){
           int count=0;
           for (int i = 0; i < list.size(); i++) {
                statChannel _ch = list.get(i);
                if(_ch.isEmpty())continue;
                if(!_ch.isFront())continue;
                count++;
           }
           return count;
    }
    public int getBackCountChannel(){
           int count=0;
           for (int i = 0; i < list.size(); i++) {
                statChannel _ch = list.get(i);
                if(_ch.isEmpty())continue;
                if(_ch.isFront())continue;
                count++;
           }
           return count;
    }
    public ArrayList<String>  getAllArrayString(){
           ArrayList<String>  string_list=new ArrayList<String>(list.size());
               for (int i = 0; i < list.size(); i++) {
                    statChannel _ch = list.get(i);
                    if(_ch.isEmpty())continue;

                    String s=_ch.getString();
                    string_list.add(s);
               }
           return string_list;
    }
    public ArrayList<String>  getFrontArrayString(){
           ArrayList<String>  string_list=new ArrayList<String>(list.size());
               for (int i = 0; i < list.size(); i++) {
                    statChannel _ch = list.get(i);
                    if(_ch.isEmpty())continue;
                    if(!_ch.isFront())continue;
                    String s=_ch.getString();
                    string_list.add(s);
               }
           return string_list;
    }
    public ArrayList<String>  getBackArrayString(){
           ArrayList<String>  string_list=new ArrayList<String>(list.size());
               for (int i = 0; i < list.size(); i++) {
                    statChannel _ch = list.get(i);
                    if(_ch.isEmpty())continue;
                    if(_ch.isFront())continue;
                    String s=_ch.getString();
                    string_list.add(s);
               }
           return string_list;
    }

}
