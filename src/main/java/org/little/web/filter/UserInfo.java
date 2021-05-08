package org.little.web.filter;

import java.util.List;
public interface UserInfo {

    /**
     * Returns a list of info associated with the label.
     * 
     * @param label e.g. name, proxyAddresses, whenCreated
     * @return a list of info associated with the label 
     */
    List<String> getInfo(final String label);
    
    /**
     * Return a list of labels.
     * 
     * @return a list of labels
     */
    List<String> getLabels();
    
    /**
     * Returns true if there is info with the passed-in label. 
     *  
     * @param label e.g. mail, memberOf, displayName
     * @return true true if there is info with the passed-in label
     */
    boolean hasInfo(final String label);
}
