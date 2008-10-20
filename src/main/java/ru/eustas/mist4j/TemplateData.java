package ru.eustas.mist4j;

import java.util.Arrays;

/**
 * A data-holder class for library-internal use.
 * 
 * <p>
 * After data of template has been parsed it is stored in this structure.
 * </p>
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
class TemplateData {
	/**
	 * A data-holder class for library-internal use.
	 * 
	 * <p>
	 * Holds information about char-array range. Any literal fragment is
	 * determined by it's start point in array and length to char-sequence.
	 * </p>
	 * 
	 * @author Klyuchnikow Eugene
	 * @version 2008.10.20 - initial version
	 */
	static class Range {
		final int start;
		final int len;

		public Range(int _start, int _len) {
			len = _len;
			start = _start;
		}

		@Override
		public String toString() {
			return "{" + start + "-" + len + "}";
		}
	}

	/**
	 * Array of literal parts
	 */
	final Range[] ranges;

	/**
	 * Array of substitution method names
	 */
	final String[] invokers;

	/**
	 * Literal content
	 */
	final char[] literals;

	/**
	 * Just store given information in fields.
	 * 
	 * @param _invokers
	 * @param _ranges
	 * @param _literals
	 */
	TemplateData(String[] _invokers, Range[] _ranges, char[] _literals) {
		invokers = Arrays.copyOf(_invokers, _invokers.length);
		ranges = Arrays.copyOf(_ranges, _ranges.length);
		literals = Arrays.copyOf(_literals, _literals.length);
	}
}
