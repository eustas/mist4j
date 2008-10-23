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

import java.io.Writer;

/**
 * A complement interface to {@link ITemplate}.
 * 
 * <p>
 * A "source" object must implement this interface, along with
 * <tt><b>void render</b><i>XXX</i><b>()</b></tt> methods.
 * </p>
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public interface IFastRenderer {
	/**
	 * This method is invoked by the generated template renderer, to get stream
	 * to output to.
	 * 
	 * @return {@link Writer} to be used both by renderer and template engine
	 */
	public Writer getOut();
}
