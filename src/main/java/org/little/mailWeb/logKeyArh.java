package org.little.mailWeb;
             
import org.little.lmsg.lMessage;

public interface logKeyArh{
       public void open(commonDB cfg);
       public void close();
       public void print(lMessage msg);
}
