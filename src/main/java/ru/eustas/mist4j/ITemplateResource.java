package ru.eustas.mist4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public interface ITemplateResource {
	String getContent() throws IOException;

	//boolean isActual();

	public final static class Utils {
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
