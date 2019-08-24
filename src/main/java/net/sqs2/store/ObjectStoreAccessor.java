package net.sqs2.store;

import java.io.IOException;
import java.io.InvalidClassException;

import net.sqs2.store.ObjectStore.ObjectMarshalizer;
import net.sqs2.store.ObjectStore.ObjectStoreException;

import org.xtreemfs.babudb.api.database.Database;
import org.xtreemfs.babudb.api.database.DatabaseRequestResult;
import org.xtreemfs.babudb.api.exception.BabuDBException;

public class ObjectStoreAccessor{
	Database db;
	ObjectStoreAccessor(Database db){
		this.db = db;
	}
	
	public String getName(){
		return this.db.getName();
	}
	
	public synchronized void shutdown() throws ObjectStoreException{
		try{
			this.db.shutdown();
		}catch(BabuDBException ex){
			throw new ObjectStoreException(ex);
		}
	}

	public synchronized Object get(String k) throws ObjectStoreException{
		try{
			DatabaseRequestResult<byte[]> result = this.db.lookup(0, k.getBytes(ObjectStore.ENCODING), null);
			byte[] value = result.get();
			if(value == null){
				return null;
			}
			return ObjectMarshalizer.toObject(value);
		}catch(InvalidClassException ex){
			return null;
		}catch(ClassNotFoundException ex){
			return null;
		}catch(IOException ex){
			throw new ObjectStoreException(ex);
		}catch(BabuDBException ex){
			throw new ObjectStoreException(ex);
		}
	}
	
	public synchronized void put(String k, Object v) throws ObjectStoreException{
		try{
			//DatabaseInsertGroup insert = this.db.createInsertGroup();
			//insert.addInsert();
			DatabaseRequestResult<Object> result = this.db.singleInsert(0,
						k.getBytes(ObjectStore.ENCODING), 
						ObjectMarshalizer.toBytes(v), 
						null);
			result.get();
			return;
		}catch(IOException ex){
			throw new ObjectStoreException(ex);
		}catch(BabuDBException ex){
			throw new ObjectStoreException(ex);
		}
	}

	public Object get(Object k) throws ObjectStoreException{
		try{
			DatabaseRequestResult<byte[]> result = this.db.lookup(0, ObjectMarshalizer.toBytes(k), null);
			byte[] value = result.get();
			if(value == null){
				return null;
			}
			return /*(V)*/ ObjectMarshalizer.toObject(value);
		}catch(IOException ex){
			throw new ObjectStoreException(ex);
		}catch(ClassNotFoundException ex){
			throw new ObjectStoreException(ex);
		}catch(BabuDBException ex){
			throw new ObjectStoreException(ex);
		}
	}
	
	public synchronized void put(Object k, Object v) throws ObjectStoreException{
		try{
			//DatabaseInsertGroup insert = this.db.createInsertGroup();
			//insert.addInsert();
			DatabaseRequestResult<Object> result = this.db.singleInsert(0, 
					ObjectMarshalizer.toBytes(k), 
					ObjectMarshalizer.toBytes(v), 
					null);
			result.get();
			return;
		}catch(IOException ex){
			throw new ObjectStoreException(ex);
		}catch(BabuDBException ex){
			throw new ObjectStoreException(ex);
		}
	}
}