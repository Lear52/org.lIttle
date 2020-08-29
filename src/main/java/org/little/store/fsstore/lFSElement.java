package org.little.store.fsstore;

import java.io.File;


public abstract class lFSElement {

    private String              name;
    private String              full_name;
    private lFSElement          parent;


    protected           lFSElement() {clear();}

    protected           lFSElement(lFSElement _parent,String _name) {
                        clear();
                        setName(_name); 
                        setParent(_parent);
    }                  
    protected void      clear(){
                        parent   =null;   
                        name     =null;     
                        full_name=null;
    }
    protected  void     setName(String _name){name=_name;}
    private    void     setFullName(String _name){full_name=_name;}
    protected  void     setFullName(){
                        if(parent!=null)setFullName(parent.getFullName()+File.separator+getPrefix()+getName());
                        else setFullName(getName());
    };

    protected void        setParent  (lFSElement _parent) {
                          parent=_parent;
                          setFullName();
    }

    protected boolean     checkType(String f_n){return new File(f_n).isFile();};


    protected  String     getPrefix  (){return "?_";};
    public     String     getName    (){return name;};
    public     String     getFullName(){return full_name;};
    public     String     getRelativeName(){if(parent!=null)return parent.getRelativeName()+"/"+name;else return name;};
    public     lFSElement getParent  () {return parent;}

    public     String     toString() {
	           String s = getName();
	           if (s != null) return s;
	           else  return "";
    }
}
