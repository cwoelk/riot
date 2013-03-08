package org.riotfamily.cachius.persistence.memory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.riotfamily.cachius.persistence.PersistenceItem;

/**
 * Disk store which stores files in new directory and cleans it on each restart
 * 
 * @author Boris Vitez
 */
public class InMemoryItem extends PersistenceItem {

	private String id;
	
	private int BUFFER_SIZE = 4086;

	private ByteArrayInputStream inputStream;
	
	private MemoryOutputStream outputStream;
	
	// Set by the MemoryOutputStream implementation
	private int size = -1;
	
	public InMemoryItem(String id) {
		this.id = id;
	}

	protected String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return String.format("%s[id=%s, state=%s]", getClass().getName(), getId(), "");
	}
	
	/**
	 * This method is called many times over.
	 * 
	 * First time immediately after OutputStream.close()
	 * to determine if content can be gzipped.
	 * All other times from serve()
	 */
	public int size() {
		return size;
	}

	public void delete() {
		this.inputStream = null;
	}

	public OutputStream getOutputStream() throws IOException {
		if (outputStream != null) {
			throw new IOException("Output stream was already created.");
		}
		
		outputStream = new MemoryOutputStream(BUFFER_SIZE);
		return outputStream;
	}
	
	/**
	 * The methods using this input stream should be synchronized.
	 */
	public InputStream getInputStream() throws IOException {
		if (outputStream != null && inputStream == null) {
			throw new IOException("Output stream was not closed.");
		}

		// Empty stream implementation - getOutputStream() has never been called
		if (inputStream == null) {
			inputStream = new ByteArrayInputStream(new byte[] {});
			size = 0;
		}
		else {
			// Make sure we serve stream from the beginning
			inputStream.reset();
		}
		
		return inputStream;
	}

	/**
	 * Output steam that sets the size and lastModified properties of the main class on close().
	 * Relies on the fact that CachiusResponse.stopCapturing() closes the stream after capturing.
	 */
	class MemoryOutputStream extends ByteArrayOutputStream {
		/**
		 * Only this constructor is supported
		 * @param size
		 */
		public MemoryOutputStream(int size) {
			super(size);
		}
		
		@Override
		public void close() throws IOException {
			super.close();
			closeStream();
		}

		/**
		 * Prepares input stream, size and lastModified.
		 */
		private void closeStream() {
			//lastModified = System.currentTimeMillis();

			// Set size
			size = outputStream.size();

			// Creates input stream
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());

			// Mark the stream eligible for garbage collection
			outputStream = null;
		}

	}
	
}
