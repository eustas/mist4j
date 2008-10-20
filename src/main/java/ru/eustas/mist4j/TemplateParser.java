package ru.eustas.mist4j;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

import ru.eustas.mist4j.TemplateData.Range;

/**
 * Helper class, contains static methods for template parsing.
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public class TemplateParser {
	/**
	 * Prefix added to substitute id to get method name.
	 */
	private static final String METHOD_PREFIX = "render";

	/**
	 * Parses String template and create {@link TemplateData} of it.
	 * 
	 * <p>
	 * The algorithm:
	 * <ol>
	 * <li>set marker to the beginning</li>
	 * <li>look for the '{' symbol</li>
	 * <li>all the symbols between the marker and current position goes to
	 * literal text and new range is created</li>
	 * <li>set marker to the current position</li>
	 * <li>look for '}' symbol</li>
	 * <li>all the symbols between the marker and current position are converted
	 * to new substitution id</li>
	 * <li>set marker to the current position</li>
	 * <li>go to (2)</li>
	 * </ol>
	 * </p>
	 * 
	 * @param source
	 *            template content
	 * @return structure describing template
	 */
	static TemplateData parseTemplate(String source) {
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
				// create string omittinf the first character
				String var = new String(src, last + 1, now - last - 1);
				// uppercase first character to get Camelized method name as a
				// result
				invokerLst.add(METHOD_PREFIX + Character.toUpperCase(src[last])
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