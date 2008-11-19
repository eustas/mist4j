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
import java.io.Writer;

/**
 * Interface implemented by generated renderer.
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public interface ITemplate {
	/**
	 * Call this method to render template.
	 * 
	 * Implementation will invoke {@link IFastRenderer#getOut()} to get output,
	 * and then will sequentially put literal text and call
	 * <tt><b>void render</b><i>XXX</i><b>()</b></tt> methods of victim.
	 * 
	 * @param victim
	 *            source-object, that will be invoked to render substitution
	 *            parts
	 * @throws IOException
	 *             thrown by {@link Writer#write(char[], int, int)} or
	 *             source-object render methods
	 */
	void process(IFastRenderer victim) throws IOException;
}
