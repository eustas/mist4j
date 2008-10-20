package ru.eustas.mist4j;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.Type;

import ru.eustas.mist4j.TemplateData.Range;

public class TemplateLoader extends ClassLoader {

	private static final String CLASS_PREFIX = "/runtime/generated/Template";

	/**
	 * All template files has the name "<i>templateName</i><b>{@value} </b>". So
	 * it is the suffix.
	 */
	private static final String TEMPLATES_SUFFIX = ".html";

	private final ITemplateSource loader;

	private final AtomicInteger index = new AtomicInteger();

	public TemplateLoader(ITemplateSource _loader) {
		super(ITemplate.class.getClassLoader());
		loader = _loader;
		try {
			new ITemplate() {
				@Override
				public void process(IFastRenderer victim) throws IOException {
				}
			}.process(null);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private final static String camelizeName(String templateName) {
		StringBuilder out = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(templateName, " -_");
		while (tokenizer.hasMoreTokens()) {
			writeCamel(out, tokenizer.nextToken());
		}
		return out.toString();
	}

	private final static String toClassName(String internalName) {
		return internalName.replace('/', '.');
	}

	private final static String toInternalName(String className) {
		return className.replace('.', '/');
	}

	public final static void writeCamel(StringBuilder out, String token) {
		out.append(token.substring(0, 1).toUpperCase()).append(
				token.substring(1));
	}

	/**
	 * Load resource as String. Resource file-name is constructed from template
	 * name, {@value #TEMPLATES_DIR} and {@value #TEMPLATES_SUFFIX}.
	 */

	/**
	 * Load resource as String and construct template-instance from it.
	 */
	public ITemplate loadTemplate(String templateName,
			Class<? extends IFastRenderer> cls) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IOException {
		String pkg = toInternalName(cls.getPackage().getName());
		String genCls = pkg + CLASS_PREFIX + camelizeName(templateName)
				+ index.incrementAndGet();
		String victimName = Type.getType(cls).getInternalName();

		String template = loader.getResource(templateName + TEMPLATES_SUFFIX)
				.getContent();
		TemplateData data = parseTemplate(template);

		byte[] b = TemplateAsm.dump(genCls, victimName, data.getRanges(), data
				.getInvokers());

		Class<?> defineClass = defineClass(toClassName(genCls), b, 0, b.length);
		resolveClass(defineClass);
		char[] literals = data.getLiterals();
		Constructor<?> constructor = defineClass.getConstructor(literals
				.getClass());
		Object instance = constructor.newInstance(literals);
		return (ITemplate) instance;
	}

	private TemplateData parseTemplate(String source) {
		int last = 0;
		int now = 0;
		int length = source.length();
		CharArrayWriter literals = new CharArrayWriter(length);

		List<Range> rangeLst = new ArrayList<Range>();
		List<String> invokerLst = new ArrayList<String>();
		char[] src = source.toCharArray();

		while (now < length) {
			if (src[now] == '{') {
				int litLen = now - last;
				rangeLst.add(new Range(literals.size(), litLen));
				literals.write(src, last, litLen);
				now++;
				last = now;
				while (src[now] != '}') {
					now++;
				}
				String var = new String(src, last + 1, now - last - 1);
				invokerLst.add("render" + Character.toUpperCase(src[last])
						+ var);
				now++;
				last = now;
			} else {
				now++;
			}
		}

		int litLen = now - last;
		rangeLst.add(new Range(literals.size(), litLen));
		literals.write(src, last, litLen);

		Range[] ranges = new Range[rangeLst.size()];
		ranges = rangeLst.toArray(ranges);

		String[] exprs = new String[invokerLst.size()];
		exprs = invokerLst.toArray(exprs);

		return new TemplateData(exprs, ranges, literals.toCharArray());
	}
}
