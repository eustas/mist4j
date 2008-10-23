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
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Assert;

public class TemplateLoaderTest {

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

	public static class FakeRenderer implements IFastRenderer {
		private StringWriter writer;

		public FakeRenderer() {
			writer = new StringWriter();
		}

		public void renderStub() {
			writer.write("Cruel");
		}

		@Override
		public Writer getOut() {
			return writer;
		}
	}

	public void testCommonTemplate() throws Exception {
		FakeSource fakeSource = new FakeSource("Hello {stub} World");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
		FakeRenderer victim = new FakeRenderer();
		template.process(victim);
		Assert.assertEquals("Hello Cruel World", victim.getOut().toString());
	}

	public void testTaillessTemplate() throws Exception {
		FakeSource fakeSource = new FakeSource("Hello {stub}");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
		FakeRenderer victim = new FakeRenderer();
		template.process(victim);
		Assert.assertEquals("Hello Cruel", victim.getOut().toString());
	}

	public void testHeadlessTemplate() throws Exception {
		FakeSource fakeSource = new FakeSource("{stub} World");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
		FakeRenderer victim = new FakeRenderer();
		template.process(victim);
		Assert.assertEquals("Cruel World", victim.getOut().toString());
	}

	public void testPlainTemplate() throws Exception {
		FakeSource fakeSource = new FakeSource("Hello World");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
		FakeRenderer victim = new FakeRenderer();
		template.process(victim);
		Assert.assertEquals("Hello World", victim.getOut().toString());
	}

	public void testFormatTemplate() throws Exception {
		FakeSource fakeSource = new FakeSource("{stub}{stub}");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
		FakeRenderer victim = new FakeRenderer();
		template.process(victim);
		Assert.assertEquals("CruelCruel", victim.getOut().toString());
	}

	public void testIncorrectTemplate() throws Exception {
		FakeSource fakeSource = new FakeSource("{stub}{buts}");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
		FakeRenderer victim = new FakeRenderer();
		NoSuchMethodError ex = null;
		try {
			template.process(victim);
		} catch (NoSuchMethodError e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
	}

	public void testReloadTemplate() throws Exception {
		FakeSource fakeSource = new FakeSource("Text1");
		TemplateLoader loader = new TemplateLoader(fakeSource);
		ITemplate template1 = loader.loadTemplate("fake", FakeRenderer.class);
		fakeSource.text = "Text2";
		ITemplate template2 = loader.loadTemplate("fake", FakeRenderer.class);
		FakeRenderer victim1 = new FakeRenderer();
		template1.process(victim1);
		Assert.assertEquals("Text1", victim1.getOut().toString());
		FakeRenderer victim2 = new FakeRenderer();
		template2.process(victim2);
		Assert.assertEquals("Text2", victim2.getOut().toString());
	}

	// public void testPerformance1() throws Exception {
	// FakeSource fakeSource = new FakeSource(
	// "text text text text text text text text text text text text {stub}"
	// + "text text text text text text text text text text text text {stub}"
	// + "text text text text text text text text text text text text {stub}"
	// + "text text text text text text text text text text text text {stub}");
	// TemplateLoader loader = new TemplateLoader(fakeSource);
	// ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
	// for (int i = 0; i < 100000; i++) {
	// FakeRenderer victim = new FakeRenderer();
	// template.process(victim);
	// victim.writer.toString();
	// }
	// System.gc();
	// Thread.sleep(500);
	// long n0 = System.nanoTime();
	// for (int i = 0; i < 100000; i++) {
	// FakeRenderer victim = new FakeRenderer();
	// template.process(victim);
	// victim.writer.toString();
	// }
	// long n1 = System.nanoTime();
	// System.out.println("~= " + (((n1 - n0) / 100000) / 1000.0) + "mks");
	// }
	//
	// public void testPerformance2() throws Exception {
	// FakeSource fakeSource = new FakeSource(
	// "text text text text text text text text text text text text "
	// + "text text text text text text text text text text text text "
	// + "text text text text text text text text text text text text {stub}"
	// + "text text text text text text text text text text text text ");
	// TemplateLoader loader = new TemplateLoader(fakeSource);
	// ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);
	// for (int i = 0; i < 100000; i++) {
	// FakeRenderer victim = new FakeRenderer();
	// template.process(victim);
	// victim.writer.toString();
	// }
	// System.gc();
	// Thread.sleep(500);
	// long n0 = System.nanoTime();
	// for (int i = 0; i < 100000; i++) {
	// FakeRenderer victim = new FakeRenderer();
	// template.process(victim);
	// victim.writer.toString();
	// }
	// long n1 = System.nanoTime();
	// System.out.println("~= " + (((n1 - n0) / 100000) / 1000.0) + "mks");
	// }

}
