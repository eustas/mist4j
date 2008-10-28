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

/**
 * {@link ITemplateResource} factory abstraction.
 * 
 * <p>
 * {@link #getResource(String)} is a typical wrapper for
 * {@link ClassLoader#getResourceAsStream(String)} and
 * ServletContext.getResourceAsStream(String) methods. Implementation is usually
 * based on appropriate resource loader / generator & some rules to wrap
 * template-name to resource name.
 * </p>
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public interface ITemplateSource {
	/**
	 * Get instance of {@link ITemplateResource} corresponding to template name.
	 * 
	 * @param templateName
	 * @return a system-resource wrapper for a given templateName
	 */
	ITemplateResource getResource(String templateName);
}
