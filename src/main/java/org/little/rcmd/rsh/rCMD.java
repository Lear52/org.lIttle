package org.little.rcmd.rsh;

import java.io.BufferedInputStream;


public interface rCMD {
        public static String [] str_null=new String [0];
        public boolean run(rShell sh);
        public boolean run(rShell sh,BufferedInputStream buf_input);
        public String type();
        public String [] print();


}