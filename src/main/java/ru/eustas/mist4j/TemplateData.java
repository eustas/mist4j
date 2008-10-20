package ru.eustas.mist4j;

import java.util.Arrays;

public class TemplateData {
	public static class Range {
		private final int start;
		private final int len;

		public final int getStart() {
			return start;
		}

		public final int getLen() {
			return len;
		}

		public Range(int _start, int _len) {
			len = _len;
			start = _start;
		}

		@Override
		public String toString() {
			return "{" + start + "-" + len + "}";
		}
	}

	private final Range[] ranges;
	private final String[] invokers;
	private final char[] literals;

	public final Range[] getRanges() {
		return Arrays.copyOf(ranges, ranges.length);
	}

	public final String[] getInvokers() {
		return Arrays.copyOf(invokers, invokers.length);
	}

	public final char[] getLiterals() {
		return Arrays.copyOf(literals, literals.length);
	}

	public TemplateData(String[] _invokers, Range[] _ranges, char[] _literals) {
		invokers = Arrays.copyOf(_invokers, _invokers.length);
		ranges = Arrays.copyOf(_ranges, _ranges.length);
		literals = Arrays.copyOf(_literals, _literals.length);
	}
}
