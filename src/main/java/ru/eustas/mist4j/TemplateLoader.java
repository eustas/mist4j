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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.Type;

/**
 * A template class loader.
 * 
 * <p>
 * Loaded class name has a package which is sub-package of source-object class
 * package. Also class name is formed of template name and internal counter.
 * This prevents the different template implementations to be loaded under the
 * same class name.
 * </p>
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 * @version 2008.10.20 - added writer class management
 */
public class TemplateLoader extends ClassLoader {

	/**
	 * Suffix added to package name to get generated class package.
	 */
	private static final String PACKAGE_SUFFIX = "/runtime/generated/Template";

	/**
	 * All template files has the name "<i>templateName</i><b>{@value} </b>". So
	 * it is the suffix.
	 */
	private static final String TEMPLATES_SUFFIX = ".html";

	/**
	 * Template source, configured in constructor.
	 */
	private final ITemplateSource loader;

	/**
	 * Internal counter, to make loaded classes different.
	 */
	private final AtomicInteger index = new AtomicInteger();

	/**
	 * Remember resource loader and ensure that super-object knows about
	 * {@link ITemplate}.
	 * 
	 * @param _loader
	 */
	public TemplateLoader(ITemplateSource _loader) {
		super(ITemplate.class.getClassLoader());
		loader = _loader;
		// try {
		// new ITemplate() {
		// @Override
		// public void process(IFastRenderer victim) throws IOException {
		// }
		// }.process(null);
		// } catch (IOException e) {
		// throw new IllegalStateException(e);
		// }
	}

	/**
	 * Divide name of template (which is in a common case - the filename) to
	 * parts and create a Camelized class name of it.
	 * 
	 * <p>
	 * Given string is broken to tokens by ' ', '-' and '_'.
	 * </p>
	 * 
	 * @param templateName
	 * @return Camelized name composed of the given String parts
	 */
	private final static String camelizeName(String templateName) {
		StringBuilder out = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(templateName, " -_");
		while (tokenizer.hasMoreTokens()) {
			writeCamel(out, tokenizer.nextToken());
		}
		return out.toString();
	}

	/**
	 * Convert internal class name to java class name.
	 * 
	 * <p>
	 * Replace slashes with dots.
	 * </p>
	 * 
	 * @param internalName
	 * @return
	 */
	private final static String toClassName(String internalName) {
		return internalName.replace('/', '.');
	}

	/**
	 * Convert java class name to internal class name.
	 * 
	 * <p>
	 * Replace dots with slashes.
	 * </p>
	 * 
	 * @param className
	 * @return
	 */
	private final static String toInternalName(String className) {
		return className.replace('.', '/');
	}

	/**
	 * Append the given String to the given StringBuilder, replacing the first
	 * letter with upper-case variant.
	 * 
	 * @param out
	 * @param token
	 */
	private final static void writeCamel(StringBuilder out, String token) {
		out.append(token.substring(0, 1).toUpperCase()).append(
				token.substring(1));
	}

	/**
	 * Load resource as String and construct template-instance from it.
	 * 
	 * <p>
	 * Resource name is constructed from template name, with appending
	 * {@value #TEMPLATES_SUFFIX}. Then resource is loaded and parsed. After
	 * that byte-code is generated, loaded and resolved. The last thing - class
	 * instance is created, using literal data.
	 * </p>
	 * 
	 * @param templateName
	 *            resource name withour suffix
	 * @param cls
	 *            victim class
	 * @param writerClass
	 *            a {@link Writer} class, whose
	 *            {@link Writer#write(char[], int, int)} will be used by
	 *            template
	 * @return constructed and initialized template instance
	 * @throws SecurityException
	 *             propagated from reflection
	 * @throws NoSuchMethodException
	 *             propagated from reflection
	 * @throws IllegalArgumentException
	 *             propagated from reflection
	 * @throws InstantiationException
	 *             propagated from reflection
	 * @throws IllegalAccessException
	 *             propagated from reflection
	 * @throws InvocationTargetException
	 *             propagated from reflection
	 * @throws IOException
	 *             propagated from resource loading
	 */
	public ITemplate loadTemplate(String templateName,
			Class<? extends IFastRenderer> cls,
			Class<? extends Writer> writerClass) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IOException {
		String pkg = toInternalName(cls.getPackage().getName());
		String genCls = pkg + PACKAGE_SUFFIX + camelizeName(templateName)
				+ index.incrementAndGet();
		String victimName = Type.getType(cls).getInternalName();
		String writerName = Type.getType(writerClass).getInternalName();

		String template = loader.getResource(templateName + TEMPLATES_SUFFIX)
				.getContent();
		TemplateData data = TemplateParser.parseTemplate(template);

		byte[] b = TemplateAsm.dump(genCls, victimName, writerName, data.ranges,
				data.invokers);

		Class<?> defineClass = defineClass(toClassName(genCls), b, 0, b.length);
		resolveClass(defineClass);
		char[] literals = data.literals;
		Constructor<?> constructor = defineClass.getConstructor(literals
				.getClass());
		Object instance = constructor.newInstance(literals);
		return (ITemplate) instance;
	}

	/**
	 * A most common use case of {@link #loadTemplate(String, Class, Class)} -
	 * the writer class is {@link Writer}.
	 * 
	 * @param templateName
	 *            resource name withour suffix
	 * @param cls
	 *            victim class
	 * @return constructed and initialized template instance
	 * @throws SecurityException
	 *             propagated from reflection
	 * @throws NoSuchMethodException
	 *             propagated from reflection
	 * @throws IllegalArgumentException
	 *             propagated from reflection
	 * @throws InstantiationException
	 *             propagated from reflection
	 * @throws IllegalAccessException
	 *             propagated from reflection
	 * @throws InvocationTargetException
	 *             propagated from reflection
	 * @throws IOException
	 *             propagated from resource loading
	 */
	public ITemplate loadTemplate(String templateName,
			Class<? extends IFastRenderer> cls) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IOException {
		return loadTemplate(templateName, cls, Writer.class);
	}
}