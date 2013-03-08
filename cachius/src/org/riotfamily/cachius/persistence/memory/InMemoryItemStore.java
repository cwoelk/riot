package org.riotfamily.cachius.persistence.memory;

import java.io.IOException;

import org.riotfamily.cachius.persistence.PersistenceItem;
import org.riotfamily.cachius.persistence.PersistenceStore;

/**
 * Disk store which stores files in memory
 * 
 * @author Boris Vitez
 */
public class InMemoryItemStore implements PersistenceStore {
	
	private static int count = 0;
	
	public PersistenceItem getItem() throws IOException {
		return new InMemoryItem(String.valueOf(count++));
	}

}
