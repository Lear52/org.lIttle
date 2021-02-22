package org.little.rcmd.rsh;

import java.io.BufferedInputStream;


public interface rCMD {
        public static String [] str_null=new String [0];

        //public boolean  run(rShell sh);

        public boolean  run(rShell sh,BufferedInputStream buf_input);

        //     type cmd (classname)
        public String   type();

        //     out request cmd
        public String[] print();

        public byte[]   getBuffer();
        public void     setBuffer(byte[] buffer);

}