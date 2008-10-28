package ru.eustas.mist4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class CastTest {
	public static class FakeResource implements ITemplateResource {
		private final String text;

		public FakeResource(String _text) {
			text = _text;
		}

		@Override
		public String getContent() throws IOException {
			return text;
		}
	}

	public static class FakeSource implements ITemplateSource {
		private String text;

		public FakeSource(String _text) {
			text = _text;
		}

		@Override
		public ITemplateResource getResource(String fileName) {
			return new FakeResource(text);
		}
	}

	public static class FakeRenderer {
		private StringWriter writer;

		public FakeRenderer() {
			writer = new StringWriter();
		}

		public void renderStub() {
			writer.write("Cruel");
		}

		public Writer getOut() {
			return writer;
		}
	}

	@SuppressWarnings("unchecked")
	public void testCastSource() throws Exception {
		FakeSource fakeSource = new FakeSource("Hello {stub} World");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		loader.loadTemplate("fake", (Class) FakeRenderer.class);
	}

}
