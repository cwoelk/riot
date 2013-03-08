package org.riotfamily.cachius.http.content;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.support.IOUtils;
import org.riotfamily.cachius.persistence.PersistenceItem;


public class BinaryContent implements Content {

	private PersistenceItem persistenceItem;
	
	public BinaryContent(PersistenceItem persistenceItem) {
		this.persistenceItem = persistenceItem;
	}

	public int getLength(HttpServletRequest request, HttpServletResponse response) {
		return persistenceItem.size();
	}

	public void serve(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		IOUtils.serve(persistenceItem.getInputStream(), response.getOutputStream());
	}

	public void delete() {
		persistenceItem.delete();
	}
	
}
