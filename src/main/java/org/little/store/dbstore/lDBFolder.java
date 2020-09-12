package org.little.store.dbstore;

import java.util.ArrayList;

import org.little.store.lFolder;
import org.little.store.lMessage;
import org.little.store.lStore;


public class lDBFolder  implements lFolder {

    //private lStore                 store;
    private ArrayList<lMessage>    msg;
    



    public         lDBFolder(lStore store,lFolder _parent,String _name) {
                      clear();
                     // this.store = store; 
    }
    protected void    clear(){
                      msg      =new ArrayList<lMessage>(100);
                      //store=null;
    }

    //public lStore  getStore   () {return store; }


    public ArrayList<lMessage>    getMsg(){
           return msg;
    }
	
	@Override
	public boolean open(int _mode) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean save(lMessage msg) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getUID() {
		// TODO Auto-generated method stub
		return 0;
	}

       public ArrayList<String> getList() {
              return null;
       }
	@Override
	public int getUnreadMsg() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getRecentMsg() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getAllMsg() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getNextUID() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getMaxUID() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getFirstUnreadMsg() {
		// TODO Auto-generated method stub
		return 0;
	}
  
}
