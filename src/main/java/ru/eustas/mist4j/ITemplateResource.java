/*
 *  Copyright 2008 Klyuchnikow Eugene (ru.Eustas)
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
package ru.eustas.mist4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Resource abstraction.
 * 
 * Instantiated by {@link ITemplateSource}. In future would report about it's
 * status to make template reload more efficient.
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public interface ITemplateResource {
	/**
	 * Get (load) String representation of underlying resource.
	 * 
	 * @return String representation of underlying resource
	 * @throws IOException
	 *             file-system / net error
	 */
	String getContent() throws IOException;

	// boolean isActual();

	/**
	 * A helper class, with the most common subroutines used by all
	 * {@link ITemplateResource} implementations.
	 * 
	 * @author Klyuchnikow Eugene
	 * @version 2008.10.20 - initial version
	 */
	public final static class Utils {
		/**
		 * Convert InputStream to String.
		 * 
		 * <p>
		 * Aimed to be used by {@link ITemplateResource#getContent()}.
		 * </p>
		 * <p>
		 * Content is loaded "as is" (UTF-8) through 10Kbyte buffer.
		 * </p>
		 * 
		 * @param stream
		 *            input
		 * @return stream content
		 * @throws IOException
		 *             thrown by {@link InputStream#read(byte[])}
		 */
		public final static String loadContent(InputStream stream)
				throws IOException {
			if (stream == null) {
				throw new IllegalArgumentException("stream == null");
			}
			StringBuilder str = new StringBuilder();
			char[] buf = new char[10240];
			int l = 0;
			InputStreamReader reader = new InputStreamReader(stream);
			try {
				while ((l = reader.read(buf)) >= 0) {
					str.append(buf, 0, l);
				}
			} finally {
				reader.close();
			}
			return str.toString();
		}
	}
}
