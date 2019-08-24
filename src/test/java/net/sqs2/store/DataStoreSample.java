package net.sqs2.store;


import java.io.UnsupportedEncodingException;

import org.xtreemfs.babudb.BabuDBFactory;
import org.xtreemfs.babudb.api.BabuDB;
import org.xtreemfs.babudb.api.DatabaseManager;
import org.xtreemfs.babudb.api.database.Database;
import org.xtreemfs.babudb.api.database.DatabaseInsertGroup;
import org.xtreemfs.babudb.api.database.DatabaseRequestResult;
import org.xtreemfs.babudb.api.database.ResultSet;
import org.xtreemfs.babudb.api.exception.BabuDBException;
import org.xtreemfs.babudb.config.ConfigBuilder;

public class DataStoreSample {
	
	public static final String ENCODING = "UTF-8";
	public DataStoreSample(){
		try{
			BabuDB databaseSystem = BabuDBFactory.createBabuDB(new ConfigBuilder().setDataPath("/tmp/babudb").build());
			DatabaseManager dbm = databaseSystem.getDatabaseManager();
			
			Database db = null;

			Object context = null;

			try{
				db = dbm.getDatabase("myDB");
			}catch(BabuDBException ex){
				db = dbm.createDatabase("myDB", 1);
			}

			DatabaseInsertGroup ig = db.createInsertGroup();
			ig.addInsert(0, "key".getBytes(ENCODING), "value".getBytes(ENCODING));
			ig.addInsert(0, "key2".getBytes(ENCODING), "value2".getBytes(ENCODING));
			DatabaseRequestResult<Object> result = db.insert(ig, context);
			Object obj = result.get();
			
			prefixLookup(db, context);
			
			db.shutdown();
			databaseSystem.shutdown();
			
		}catch(UnsupportedEncodingException ex){
			ex.printStackTrace();
		}catch(BabuDBException ex){
			ex.printStackTrace();
		}
	}

	private void lookup(Database db, Object context)
			throws UnsupportedEncodingException, BabuDBException {
		DatabaseRequestResult<byte[]> result = db.lookup(0, "key".getBytes(ENCODING), context);
		String value = new String(result.get(), ENCODING);
		System.out.println("value:"+value);
	}

	private void prefixLookup(Database db, Object context)
			throws UnsupportedEncodingException, BabuDBException {
		DatabaseRequestResult<ResultSet<byte[], byte[]>> result= db.prefixLookup(0, "k".getBytes(ENCODING), context);
		ResultSet<byte[], byte[]> resultSet = result.get();
		
		while(resultSet.hasNext()) {
		    java.util.Map.Entry<byte[], byte[]> keyValuePair = resultSet.next();

		    String key = new String(keyValuePair.getKey(), ENCODING);
		    String value = new String(keyValuePair.getValue(), ENCODING);
		    
		    System.out.println("key:"+key);
		    System.out.println("value:"+value);
		    System.out.println();
		    
		}
		resultSet.free();
	}
	
	public static void main(String args[]){
		new DataStoreSample();
	}
}
