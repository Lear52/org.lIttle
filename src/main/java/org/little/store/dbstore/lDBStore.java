package org.little.store.dbstore;

import java.util.ArrayList;
//import org.little.util.Logger;
//import org.little.util.LoggerFactory;
//import org.little.util.Except;

import org.little.store.lFolder;
import org.little.store.lStore;



public class lDBStore   implements lStore  {
    //private static final Logger logger = LoggerFactory.getLogger(lStore.class);


    public lDBStore(String _name) {
    }

    
    @Override
    public    ArrayList<lFolder >    getListFolder  (){return null;}


	@Override
	public lFolder getInboxFolder() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public lFolder createInboxFolder() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public lFolder getOutboxFolder() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public lFolder createOutboxFolder() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public lFolder getFolder(String name_folder) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public lFolder createFolder(String name_folder) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public lFolder getFolder(lFolder parent, String name_folder) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public lFolder createFolder(lFolder parent, String name_folder) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
        public void close(){return ;}


	@Override
	public lFolder getDelboxFolder() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public lFolder createDelboxFolder() {
		// TODO Auto-generated method stub
		return null;
	}

   

}

