package org.little.mailWeb.imap;
       
import java.util.ArrayList;

import org.little.mailWeb.commonArh;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.scheduler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImapListBox{

        private static final Logger    logger   = LoggerFactory.getLogger(ImapListBox.class);
        private ArrayList<ImapLoadBox> list_box;
        private commonArh              cfg;

        public ImapListBox() {
               cfg      = new commonArh();
               list_box = new ArrayList<ImapLoadBox>();
        }

        public ArrayList<ImapLoadBox> get(){return list_box;}

        public boolean loadCFG(String xpath){
               boolean ret=cfg.loadCFG(xpath);

               cfg.init();

               cfg.getFolder().create();

               Node     n    = cfg.getNodeBox();
               NodeList glist=n.getChildNodes();     

               for(int i=0;i<glist.getLength();i++){
                     Node nn=glist.item(i);
                     if("box".equalsIgnoreCase(nn.getNodeName())){
                        ImapLoadBox cln=new ImapLoadBox();
                        cln.init(nn);
                        cln.setFolder(cfg.getFolder());
                        list_box.add(cln);
                     }
               }
               
               return ret;
        }
        public void start(scheduler runner){
               for(int i=0;i<list_box.size();i++) {
                   ImapLoadBox cln = list_box.get(i);
                   int st=(cln.getTimeout()*i)/(list_box.size());
                   cln.setStart(st);
                   runner.add(cln);
               }
        }
        public void stop(scheduler runner){
               for(int i=0;i<list_box.size();i++) {
                   ImapLoadBox cln = list_box.get(i);
                   cln.KILL();
                   runner.del(cln);
               }
               list_box.clear();
               cfg.getFolder().close();
               cfg=null;
        }
        public static void main(String[] args) {

            System.setProperty("java.net.preferIPv4Stack", "true");
            logger.trace("Set java property:java.net.preferIPv4Stack=true");
            ImapListBox cln = new ImapListBox();
            String xpath=args[0];
            System.out.println(xpath);

            cln.loadCFG(args[0]);
            ArrayList<ImapLoadBox> list;

            list=cln.get();
            for(int i=0;i<list.size();i++)list.get(i).work();


    }


}
