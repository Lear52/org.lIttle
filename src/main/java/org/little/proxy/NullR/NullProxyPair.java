package org.little.proxy.NullR;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.Channel;

public class NullProxyPair{
    private static final Logger logger = LoggerFactory.getLogger(NullProxyFServer.class);
    private ArrayList<Channel> front;
    private ArrayList<Channel> back;

    public  NullProxyPair(){clear();}

    public  void clear(){
            front=new ArrayList<Channel>();
            back =new ArrayList<Channel>(); 
    }

    public synchronized void addFront(Channel front_channel){
           front.add(front_channel);
    }
    public synchronized int sizeFront(){
           return front.size();
    }
    public synchronized void delFront(Channel front_channel){
           String id=front_channel.id().asShortText();
           for(int i=0;i<front.size();i++)if(front.get(i).id().asShortText().equals(id)){front.remove(i);return;}
    }
    public synchronized void addBack(Channel back_channel){
           back.add(back_channel);
    }
    public synchronized int sizeBack(){
           return back.size();
    }
    public synchronized void delBack(Channel back_channel){
           String id=back_channel.id().asShortText();
           for(int i=0;i<back.size();i++)if(back.get(i).id().asShortText().equals(id)){back.remove(i);return;}
    }

    public synchronized Channel getPair4Front(Channel front_channel){
           logger.trace("search  pair for front channel front(wait):"+front.size()+ " back(pair):"+back.size()+" front channel:"+front_channel.id().asShortText());
           Channel p_channel=getPair(front,back,front_channel);
           logger.trace("search  pair for front channel front(wait):"+front.size()+ " back(pair):"+back.size()+" front channel:"+front_channel.id().asShortText());
    	   return p_channel;
    }
    public synchronized Channel getPair4Back(Channel back_channel){
           logger.trace("search  pair for back channel front(pair):"+front.size()+ " back(wait):"+back.size()+" back channel:"+back_channel.id().asShortText());
           Channel p_channel=getPair(back,front,back_channel);
           logger.trace("search  pair for back channel front(pair):"+front.size()+ " back(wait):"+back.size()+" back channel:"+back_channel.id().asShortText());
 	   return p_channel;
    }
    private synchronized static Channel getPair(ArrayList<Channel> list_wait,ArrayList<Channel> list_pair,Channel current_channel){
        while(list_pair.size()!=0){
           Channel pair_channel;
           pair_channel=list_pair.remove(0);
           logger.trace("pair channel ch:"+pair_channel.id().asShortText());
           //if(pair_channel.isOpen()) {
           if(pair_channel.isActive()) {
              logger.trace("pair channel is ok ch:"+pair_channel.id().asShortText());
              return pair_channel;
           }
        }
        list_wait.add(current_channel);
        logger.trace("no pair channel list_wait:"+list_wait.size()+ " list_pair:"+list_pair.size()+" channel:"+current_channel.id().asShortText());
        return null;
    }


}
