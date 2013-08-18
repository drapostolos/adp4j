package org.sdp;

import java.util.HashMap;
import java.util.Map;

final class HashMapDiffer<K, V> {
	private Map<K, V> m1, m2, added, removed;

	HashMapDiffer(Map<K, V> m1, Map<K, V> m2) {
		this.m1 = m1;
		this.m2 = m2;
		setAdded();
		setRemoved();
	}
	
	
//	Set<String> removedKeys = new HashSet<String>(beforeMap.keySet());
//	removedKeys.removeAll(afterMap.keySet());
//
//	Set<String> addedKeys = new HashSet<String>(afterMap.keySet());
//	addedKeys.removeAll(beforeMap.keySet());
//
//	Set<Entry<String, String>> changedEntries = new HashSet<Entry<String, String>>(
//	        afterMap.entrySet());
//	changedEntries.removeAll(beforeMap.entrySet());
//
//	System.out.println("added " + addedKeys);
//	System.out.println("removed " + removedKeys);
//	System.out.println("changed " + changedEntries);

	private void setAdded() {
		added = new HashMap<K, V>(m2);
		for(K key : m1.keySet()){
			added.remove(key);
		}
	}

	private void setRemoved() {
		removed = new HashMap<K, V>(m1);
		for(K key : m2.keySet()){
			removed.remove(key);
		}
	}

	/**
	 * Returns the element that are in m2, but not in m1.
	 * @return
	 */
	Map<K, V> getAdded() {
		return added;
	}

	/**
	 * Returns the element that are in m1, but not in m2.
	 * @return
	 */
	Map<K, V> getRemoved() {
		return removed;
	}
	
	/**
	 * 
	 * @return
	 */
	boolean hasDiff() {
		return !m1.equals(m2);
	}

	public String toString(){
		return "Removed: " + getRemoved() + ", Added: " + getAdded();
	}
}
