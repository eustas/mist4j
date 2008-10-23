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

import junit.framework.Assert;

public class DirectPerformanceTest {

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

	public static class FakeWriter extends Writer {
		@Override
		public void close() throws IOException {
			// do nothing
		}

		@Override
		public void flush() throws IOException {
			// do nothing
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			// do nothing
		}
	}

	public static class FakeRenderer implements IFastRenderer {
		final Writer out = new FakeWriter();
		char[] str = { '1', '2', '3', '4' };

		@Override
		public final Writer getOut() {
			return out;
		}

		public final void renderThing() throws IOException {
			out.write(str, 0, 4);
		}
	}

	public static class FakeRendererD implements IFastRenderer {
		final FakeWriter out = new FakeWriter();
		char[] str = { '1', '2', '3', '4' };

		@Override
		public final FakeWriter getOut() {
			return out;
		}

		public final void renderThing() throws IOException {
			out.write(str, 0, 4);
		}
	}

	public void testDirectPerformance() throws Exception {
		// startup
		FakeSource fakeSource = new FakeSource("Hello {thing} World");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		// virtual
		ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
		FakeRenderer victim = new FakeRenderer();
		for (int i = 0; i < 50000000; i++) {
			template.process(victim);
		}
		long t0 = System.nanoTime();
		for (int i = 0; i < 50000000; i++) {
			template.process(victim);
		}
		long t1 = System.nanoTime();
		// direct
		ITemplate templateD = loader.loadTemplate("fake", FakeRendererD.class,
				FakeWriter.class);
		FakeRendererD victimD = new FakeRendererD();
		for (int i = 0; i < 50000000; i++) {
			templateD.process(victimD);
		}
		long t0d = System.nanoTime();
		for (int i = 0; i < 50000000; i++) {
			templateD.process(victimD);
		}
		long t1d = System.nanoTime();
		double ratio = (t1d - t0d + 0.0) / (t1 - t0);
		Assert.assertTrue(ratio < 0.225);
	}
}
