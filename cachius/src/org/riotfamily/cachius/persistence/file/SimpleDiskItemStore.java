package org.riotfamily.cachius.persistence.file;

import java.io.File;
import java.io.IOException;

import org.riotfamily.cachius.persistence.PersistenceItem;
import org.riotfamily.cachius.persistence.PersistenceStore;

public class SimpleDiskItemStore implements PersistenceStore {

	private File dir;

	public SimpleDiskItemStore() {
		setBaseDir(new File(System.getProperty("java.io.tmpdir")));
	}
	
	public SimpleDiskItemStore(File dir) {
		setBaseDir(dir);
	}
	
	private void setBaseDir(File baseDir) {
		this.dir = new File(baseDir, "items");
		delete(this.dir);
		this.dir.mkdirs();
	}

	private static void delete(File f) {
        if (f.isDirectory()) {
            File[] entries = f.listFiles();
            for (int i = 0; i < entries.length; i++) {
            	delete(entries[i]);
            }
        }
        f.delete();
    }
	
	public File getFile() throws IOException {
		return File.createTempFile("item", "", dir);
	}

	public PersistenceItem getItem() throws IOException {
		return new FilePersistenceItem(getFile());
	}
	
	
}
