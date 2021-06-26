package org.little.mq.stream.ufps;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ufpsParser1 extends DefaultHandler {
       final private static String CLASS_NAME="prj0.stream.ufpsParser1";
       final private static int    CLASS_ID  =1606;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}

       private String currentElement = null;
     
       public void startElement(String uri, String local_name, String raw_name, Attributes attr) throws SAXException {



              currentElement = local_name;

       }
       public void endElement(String uri, String local_name, String raw_name) throws SAXException {
              currentElement=null;
       }
     
       public void startDocument() throws SAXException {
              System.out.println("start document");
       }
     
       public void endDocument() throws SAXException {
              System.out.println("end   document");
       }
     
       public void characters(char[] ch, int start, int length) throws SAXException {
              if(currentElement==null)return;
              String value = new String(ch,start,length);
              if (!Character.isISOControl(value.charAt(0))) {
                   System.out.println("Element:"+currentElement+" vol:" + value);
              }
       }
     
     
       public static void main(String[] args) {
              SAXParserFactory spf;
              SAXParser        saxParser;
              XMLReader        xmlReader;
              ufpsParser1       parser;
              try {
                  parser    = new ufpsParser1();
                  spf       = SAXParserFactory.newInstance();
                  spf.setNamespaceAware(true);
                  saxParser = spf.newSAXParser();

                  xmlReader = saxParser.getXMLReader();

                  xmlReader.setContentHandler(parser);

                  FileInputStream fis = new FileInputStream(args[0]);
                  byte[] data = new byte[fis.available()];
                  fis.read(data);
                  fis.close();

                  ByteArrayInputStream is = new ByteArrayInputStream(data);
                  xmlReader.parse(new InputSource(is));

              } 
              catch(Exception e){
                   System.out.println(e);
              }
       }


}
