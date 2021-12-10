package net.sqs2.store;

import java.util.concurrent.ConcurrentMap;

public class ObjectStoreAccessor{
	ConcurrentMap map;
	ObjectStoreAccessor(ConcurrentMap map){
		this.map = map;
	}
	
	public Object get(String k) {
		return map.get(k);
	}
	
	public void put(String k, Object v) {
		map.put(k, v);
	}
}