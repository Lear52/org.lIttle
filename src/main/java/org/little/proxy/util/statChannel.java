package org.little.proxy.util;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

//import org.little.util.Logger;
//import org.little.util.LoggerFactory;

import io.netty.channel.Channel;


public class statChannel {

    //private static final Logger LOG = LoggerFactory.getLogger(statChannel.class);
    private String        id;
    private String        user;
    private pointHostPort src;
    private pointHostPort dst;
    private AtomicLong    _in;
    private AtomicLong    _out;
    private AtomicLong    lastTime;
    //private AtomicLong    queuesSize;
    private statChannel   neighbour;
    private boolean       is_front;
    private boolean       is_empty;

    public statChannel(){
           clear();
    }

    public void clear(){
           id = ""; 
           user= "";
           src= new pointHostPort();
           dst= new pointHostPort();
           neighbour= null;
           is_front = true;
           is_empty = true;
           _in        = new AtomicLong();
           _out       = new AtomicLong();
           lastTime   = new AtomicLong();
           //queuesSize = new AtomicLong();
    }

    public String       getId        (){return  id;               }
    public String       getUser      (){return  user;             }
    public String       getSrc       (){return  src.getHostPort();}
    public String       getDst       (){return  dst.getHostPort();}
    public String       getSrcHost   (){return  src.getHost();    }
    public String       getDstHost   (){return  dst.getHost();    }
    public long         getOut       (){return  _out.get();       }
    public long         getIn        (){return  _in.get();        }
    public long         getChangeTime(){return  lastTime.get();   }
    public statChannel  getNeighbour (){return  neighbour;        }
    public boolean      isFront      (){return  is_front;         }
    public boolean      isEmpty      (){return  is_empty;         }

    public boolean      isId         (String     t_id ){if(isEmpty())return false; return id.equals(t_id);   }

    public void  setChangeTime()                {lastTime.set(System.currentTimeMillis());}
    public void  setId        (String     t_id ){id       =t_id;                          }
    public void  setUser      (String     t_u  ){user     =t_u;                           }
    public void  setSrc       (String     t_src){src.setHostPort(t_src);                  }
    public void  setDst       (String     t_dst){dst.setHostPort(t_dst);                  }
    public void  setOut       (long       t_out){_out.set(t_out);                         }
    public void  setIn        (long       t_in ){_in.set (t_in);                          }
    public void  setNeighbour (statChannel t_nb){neighbour= t_nb;                         }
    public void  isFront      (boolean    t_is ){is_front = t_is;                         }
    public void  isEmpty      (boolean    t_is ){is_empty = t_is;                         }
    public void  setChannel   (Channel _channel){
           setId(_channel.id().asShortText());
           isEmpty(false);
           if(isFront()){
              src.setHostPort( (InetSocketAddress)_channel.remoteAddress());
              dst.setHostPort( (InetSocketAddress)_channel.localAddress());
           }
           else{
              dst.setHostPort( (InetSocketAddress)_channel.remoteAddress());
              src.setHostPort( (InetSocketAddress)_channel.localAddress());
           }
     }
    private void  _addOut      (long       t_out){_out.addAndGet(t_out);}
    private void  _addIn       (long       t_in ){_in.addAndGet(t_in);  }
    public void  addOut      (long       t_out){_addOut(t_out); if(neighbour!=null)neighbour._addIn(t_out); } 
    public void  addIn       (long       t_in ){_addIn(t_in);   if(neighbour!=null)neighbour._addOut(t_in );}

    public String toString   (){
           return   "id:"  +  id 
                 + " user:" +  user
                 + " src:" +  src.getHostPort()
                 + " dst:" +  dst.getHostPort()
                 + " out:" +  _out.get()
                 + " in:"  +  _in.get() 
                 ;
    }

    public String getString   (){
           return   "id:"  +  getId     ()
                 + " user:" + getUser   ()
                 + " src:" +  getSrc    ()
                 + " dst:" +  getDst    ()
                 + " out:" +  getOut    ()
                 + " in:"  +  getIn     ()
                 ;
    }


} 