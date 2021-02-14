package org.little.util;

import java.util.List;

public class ACL {
       
       public static final String STD_RIGHTS = "lrswipkxtea";

       public static char l_Lookup_RIGHT         = 'l';
       public static char r_Read_RIGHT           = 'r';
       public static char s_WriteSeenFlag_RIGHT  = 's';
       public static char w_Write_RIGHT          = 'w';
       public static char i_Insert_RIGHT         = 'i';
       public static char p_Post_RIGHT           = 'p';
       public static char k_CreateBox_RIGHT      = 'k';
       public static char x_DeleteBox_RIGHT      = 'x';
       public static char t_DeleteObject_RIGHT   = 't';
       public static char e_PerformExpunge_RIGHT = 'e';
       public static char a_Administer_RIGHT     = 'a';
       
       public enum EditMode {
              REPLACE, ADD, DELETE
       }
       
       private String object;
       
       private List<ACLEntry> entries;
       
       public String getObject() {
              return object;
       }

       public void setObject(String object) {
              this.object = object;
       }

       public List<ACLEntry> getEntries() {
              return entries;
       }

       public void setEntries(List<ACLEntry> entries) {
              this.entries = entries;
       }

       public static class ACLEntry {

              private String identifier;

              private String rights;

              public ACLEntry() {
              }

              public String getIdentifier() {
                     return identifier;
              }

              public String getRights() {
                     return rights;
              }

              public void setIdentifier(String identifier) {
                     this.identifier = identifier;
              }

              public void setRights(String rights) {
                     this.rights = rights;
              }

       }

}
