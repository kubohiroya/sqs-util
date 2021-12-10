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
import java.util.concurrent.ConcurrentMap;
import org.mapdb.DBMaker;
/*
import org.xtreemfs.babudb.BabuDBFactory;
import org.xtreemfs.babudb.api.BabuDB;
import org.xtreemfs.babudb.api.DatabaseManager;
import org.xtreemfs.babudb.api.database.Database;
import org.xtreemfs.babudb.api.exception.BabuDBException;
import org.xtreemfs.babudb.config.ConfigBuilder;
*/

public class ObjectStore{

	public static final String ENCODING = "UTF-8";
	
	private static Map<File, ObjectStore> instanceMap = new HashMap<File, ObjectStore>();
	private org.mapdb.DB db;

	public synchronized static ObjectStore getInstance(File dir, String dirname) throws IOException{
		File path = createStorageDirectory(dir, dirname);
		return ObjectStore.getInstance(path);
	}

	private ObjectStore(File directory) {
		this.db = DBMaker.fileDB(directory.getAbsolutePath()).make();
	}

	public ObjectStoreAccessor getAccessor(String name) {
		ConcurrentMap map = db.hashMap(name).createOrOpen();
		return new ObjectStoreAccessor(map);
	}

	public void shutdown() {
		db.commit();
		db.close();
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

	private synchronized static ObjectStore getInstance(File directory) {
		ObjectStore ret = ObjectStore.instanceMap.get(directory);
		if (ret == null) {
			ret = new ObjectStore(directory);
			ObjectStore.instanceMap.put(directory, ret);
		}
		return ret;
	}

}
