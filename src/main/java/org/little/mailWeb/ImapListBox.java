package org.little.mailWeb;
       
import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImapListBox{

        private static final Logger logger   = LoggerFactory.getLogger(ImapListBox.class);
        private ArrayList<ImapLoadBox> list_box;
        private commonArh           cfg;

        public ImapListBox() {
               cfg = new commonArh();
               list_box=new ArrayList<ImapLoadBox>();
        }

        public ArrayList<ImapLoadBox> get(){return list_box;}

        public boolean loadCFG(String xpath){
               boolean ret=cfg.loadCFG(xpath);
               cfg.init();
               Node n = cfg.getNodeBox();
               NodeList glist=n.getChildNodes();     
               for(int i=0;i<glist.getLength();i++){
                     Node nn=glist.item(i);
                     if("box".equalsIgnoreCase(nn.getNodeName())){
                        ImapLoadBox cln=new ImapLoadBox();
                        cln.init(nn);
                        list_box.add(cln);
                     }
               }
               
               return ret;
        }

        public static void main(String[] args) {

            System.setProperty("java.net.preferIPv4Stack", "true");
            logger.trace("Set java property:java.net.preferIPv4Stack=true");
            ImapListBox cln = new ImapListBox();
            String xpath=args[0];
            System.out.println(xpath);

            cln.loadCFG(args[0]);
            ArrayList<ImapLoadBox> list=cln.get();

            for(int i=0;i<list.size();i++)list.get(i).work();


    }


}
