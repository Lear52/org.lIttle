package org.little.proxy.util;

import java.util.ArrayList;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;

import org.little.proxy.commonProxy;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class statProxy  extends NotificationBroadcasterSupport implements statProxyMBean {
       private static final Logger LOG = LoggerFactory.getLogger(statProxy.class);
      
       //private int       nbChanges;
       //private int       nbResets;
      
       public statProxy(){
              //nbChanges = 0;          
              //nbResets  = 0;           
       }
      
      
       public ArrayList<String> getPointHost(){
              return commonProxy.get().getPointHost().getArrayString();
       }
      
       public ArrayList<String> getAllStatChannel(){
             return commonProxy.get().getChannel().getAllArrayString();
       }
      
       public int               getFrontCountChannel(){
             return commonProxy.get().getChannel().getFrontCountChannel();
       }
      
       public int               getBackCountChannel(){
             return commonProxy.get().getChannel().getBackCountChannel();
       }
      
      
       public int               getAllCountChannel(){
             return commonProxy.get().getChannel().getAllCountChannel();
       }
      
       public void reset() {
           /*
          AttributeChangeNotification acn =  new AttributeChangeNotification(this
                                                                             , 0,0
                                                                             ,"NbChanges reset"
                                                                             ,"NbChanges"
                                                                             ,"Integer"
                                                                             ,new Integer(nbChanges)
                                                                             ,new Integer(0));
           */                                                                  
           //state     = new ArrayList<String>(10);
           //for(int i=0;i<7;i++)state.add("initial state"+i+"///"+i);
           //LOG.trace(" statProxy.reset() size:"+state.size());
           //nbChanges = 0;
          //nbResets  = 0;
           commonProxy.get().loadCFG();
           commonProxy.get().reinit();
           LOG.trace("reset parametr");
          //sendNotification(acn);
       }
      
      
       public MBeanNotificationInfo[] getNotificationInfo() {
           String[]                  msg       = new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE };
           String                    msg2      = "This notification is emitted when the reset() method is called.";
           String                    class_name= AttributeChangeNotification.class.getName();
           MBeanNotificationInfo     mb_info   = new MBeanNotificationInfo( msg, class_name,msg2);
           MBeanNotificationInfo []  ret       = new MBeanNotificationInfo[1];
           ret[0]=mb_info;
           LOG.trace("getNotificationInfo");
           return ret;
       }

}
