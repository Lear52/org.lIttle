package org.little.stream.ufps;

import java.util.HashMap;

public class ufps_manager {
       private HashMap<String,ufps_buffer> mngr;

       public ufps_manager() {
              mngr=new HashMap<String,ufps_buffer>();
       }
       public void        create(String queue){mngr.put(queue, new ufps_buffer());}  
       public ufps_buffer get(String queue){return mngr.get(queue);}     
      // public String[]      list(){return mngr.;}     


}

