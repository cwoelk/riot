/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.cachius.persistence.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.riotfamily.cachius.persistence.PersistenceItem;

public class FilePersistenceItem extends PersistenceItem {

	private File file;

	public FilePersistenceItem(File file) {
		this.file = file;
	}

	public int size() {
		return (int) file.length();
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(file);
	}

	public void delete() {
		file.delete();
	}
	
}
