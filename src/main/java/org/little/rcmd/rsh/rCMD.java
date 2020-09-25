package org.little.rcmd.rsh;

import java.io.BufferedInputStream;


public interface rCMD {

        public boolean run();
        public boolean run(BufferedInputStream bufin);


}