package org.little.proxy.NullR;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.Channel;

public class NullProxyPair{
    private static final Logger logger = LoggerFactory.getLogger(NullProxyServer.class);
    private ArrayList<Channel> front;
    private ArrayList<Channel> back;

    public  NullProxyPair(){clear();}

    public  void clear(){
            front=new ArrayList<Channel>();
            back =new ArrayList<Channel>(); 
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
    private static Channel getPair(ArrayList<Channel> list_wait,ArrayList<Channel> list_pair,Channel current_channel){
        while(list_pair.size()!=0){
           Channel pair_channel;
           pair_channel=list_pair.remove(0);
           logger.trace("pair channel ch:"+pair_channel.id().asShortText());
           if(pair_channel.isOpen()) {
              logger.trace("pair channel is ok ch:"+pair_channel.id().asShortText());
         	  return pair_channel;
           }
        }
        list_wait.add(current_channel);
        logger.trace("no pair channel list_wait:"+list_wait.size()+ " list_pair:"+list_pair.size()+" channel:"+current_channel.id().asShortText());
        return null;
    }


}
