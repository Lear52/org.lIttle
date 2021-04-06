package org.little.mailWeb;
             
import java.util.ArrayList;

import org.json.JSONObject;
import org.little.lmsg.lMessage;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class logFile implements logKeyArh{
       private static final Logger logger = LoggerFactory.getLogger(logFile.class);


       public logFile() {}

   	@Override
       public synchronized void open(commonDB cfg) {

       }

   	@Override
       public synchronized void close() {

       }

   	@Override
       public void print(lMessage msg) {
              logger.info("-- "+msg.toString());
       }


       public static void main(String args[]){
              logger.info("START");

       }

	@Override
	public ArrayList<lMessage> loadArrey() {
		return new ArrayList<lMessage>();
	}

	@Override
	public JSONObject loadJSON() {
		// TODO Auto-generated method stub
		return new JSONObject();
	}

}
