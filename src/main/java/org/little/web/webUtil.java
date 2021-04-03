package org.little.web;

public class webUtil{
       /**
        * Returns a string that is equivalent to the input string, but with
        * special characters converted to HTML escape sequences.
        *
        * @param input  the string to escape (<code>null</code> not permitted).
        *
        * @return A string with characters escaped.
        *
        * @since 1.0.9
        */
       public static String htmlEscape(String input) {
           if(input==null)return null;
           StringBuilder result = new StringBuilder();
           int length = input.length();
           for (int i = 0; i < length; i++) {
               char c = input.charAt(i);
               if (c == '&') {
                   result.append("&amp;");
               }
               else if (c == '\"') {
                   result.append("&quot;");
               }
               else if (c == '<') {
                   result.append("&lt;");
               }
               else if (c == '>') {
                   result.append("&gt;");
               }
               else if (c == '\'') {
                   result.append("&#39;");
               }
               else if (c == '\\') {
                   result.append("&#092;");
               }
               else {
                   result.append(c);
               }
           }
           return result.toString();
       }
      
       /**
        * Returns a string that is equivalent to the input string, but with
        * special characters converted to JavaScript escape sequences.
        *
        * @param input  the string to escape (<code>null</code> not permitted).
        *
        * @return A string with characters escaped.
        *
        * @since 1.0.13
        */
       public static String javascriptEscape(String input) {
           if(input==null)return null;
           StringBuilder result = new StringBuilder();
           int length = input.length();
           for (int i = 0; i < length; i++) {
               char c = input.charAt(i);
               if (c == '\"') {
                   result.append("\\\"");
               }
               else if (c == '\'') {
                   result.append("\\'");
               }
               else if (c == '\\') {
                   result.append("\\\\");
               }
               else {
                   result.append(c);
               }
           }
           return result.toString();
       }
      
       static public String toHtml(String str) {
              //return str==null?"&nbsp;":str;
              return str==null?" ":str;
       }
}     

