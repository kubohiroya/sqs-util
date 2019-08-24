/*
 * 

 PersistentCacheManager.java

 Copyright 2007 KUBO Hiroya (hiroya@cuc.ac.jp).

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.sqs2.store;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.xtreemfs.babudb.BabuDBFactory;
import org.xtreemfs.babudb.api.BabuDB;
import org.xtreemfs.babudb.api.DatabaseManager;
import org.xtreemfs.babudb.api.database.Database;
import org.xtreemfs.babudb.api.exception.BabuDBException;
import org.xtreemfs.babudb.config.ConfigBuilder;

public class ObjectStore{

	public static class ObjectStoreException extends IOException{
		public static final long serialVersionUID = 0L;
		public ObjectStoreException(Exception ex){
			super(ex);
		}
	}
	
	public static class ObjectMarshalizer{
		public static byte[] toBytes(Object o)throws IOException{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(o);
			oos.flush();
			return bos.toByteArray();
		}
		
		public static Object toObject(byte[] b)throws IOException, ClassNotFoundException{
			ByteArrayInputStream bis = new ByteArrayInputStream(b);
			ObjectInputStream ois = new ObjectInputStream(bis);
			return ois.readObject();
		}
	}
	
	public static final String ENCODING = "UTF-8";
	
	private static Map<File, ObjectStore> instanceMap = new HashMap<File, ObjectStore>();
	private BabuDB dbSystem;

	public synchronized static ObjectStore getInstance(File dir, String dirname) throws ObjectStoreException {
		try{
			File path = createStorageDirectory(dir, dirname);
			return ObjectStore.getInstance(path);
		}catch(IOException ex){
			throw new ObjectStoreException(ex);
		}
	}

	private ObjectStore(File directory) throws ObjectStoreException {
		try{
			this.dbSystem = BabuDBFactory.createBabuDB(new ConfigBuilder().setDataPath(directory.getAbsolutePath()).build());
		}catch(BabuDBException ex){
			throw new ObjectStoreException(ex);
		}
	}

	public ObjectStoreAccessor getAccessor(String name) throws ObjectStoreException{
		DatabaseManager dbm = this.dbSystem.getDatabaseManager();
		Database db = null;
		try{
			db = dbm.getDatabase(name);
		}catch(BabuDBException ex){
			try{
				db = dbm.createDatabase(name, 1);
			}catch(BabuDBException ex2){
				throw new ObjectStoreException(ex);
			}
		}
		return new ObjectStoreAccessor(db);
	}

	public void delete(String name)throws ObjectStoreException{
		try{
			DatabaseManager dbm = this.dbSystem.getDatabaseManager();
			if(dbm.getDatabase(name) != null){
				dbm.deleteDatabase(name);	
			}
		}catch(BabuDBException ex){
			throw new ObjectStoreException(ex);
		}
	}
	

	public static void closeAll() throws BabuDBException{
		for(ObjectStore objectStore : ObjectStore.instanceMap.values()) {
			objectStore.close();
		}
	}
	
	public void close() throws BabuDBException{
		shutdown();
	}

	public void shutdown() throws BabuDBException{
		for (Database db: this.dbSystem.getDatabaseManager().getDatabases().values()){
			db.shutdown();
		}
		this.dbSystem.shutdown();
	}

	private static File createStorageDirectory(File dir, String dirname) throws IOException {
		File storageDirectory = new File(dir.getAbsoluteFile() + File.separator + dirname);
		if (!storageDirectory.isDirectory()) {
			storageDirectory.mkdirs();
		}
		if (storageDirectory.exists() && storageDirectory.canWrite()) {
			if (File.separatorChar == '\\') {
				setHiddenFileAttribute(storageDirectory);
			}
			return storageDirectory;
		} else {
			throw new IOException("ReadOnlyFileSystem");
		}
	}

	private static void setHiddenFileAttribute(File storageDirectory) throws IOException {
		String cmd = "attrib.exe +h " + storageDirectory.getAbsolutePath();
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (InterruptedException ignore) {
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private synchronized static ObjectStore getInstance(File directory) throws ObjectStoreException {
		ObjectStore ret = ObjectStore.instanceMap.get(directory);
		if (ret == null) {
			ret = new ObjectStore(directory);
			ObjectStore.instanceMap.put(directory, ret);
		}
		return ret;
	}

}
