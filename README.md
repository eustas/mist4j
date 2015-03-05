# MIcro Seconds Templates for Java
## Benefits
A very fast Java template renderer. Web pages are really fast with it – about microseconds (one thousandth of millisecond) per common template.

## Definition & Example
A template is a file with s simple mark-up:
```
Text here {substituteName}...
More text {moreSubstituteName}.
```

When template is rendered – the literal text is written to the given java.io.Writer, then source-object void parameterless method is invoked. The method name is: "*render*_!SubstituteName".

*Example:* if in template we have "{theSomething}", the invoked method will be "renderTheSometing"

## The Know-How
To gain speed – dynamic byte-code generation is equipped. So ASM library is a dependency.

# USAGE
## Terminology
 * *(template) framework* – a set of classes and interfaces of mist4j library
 * *raw template* – a `String` with a special mark-up, that describes textual and substitutional data to be rendered
 * *source* – a class that performs substitutes rendering
 * *template (processor)* – a class that performs textual data rendering and *source* invocation

## Public classes and interfaces
There are four public interfaces and one public class in mist4j framework.

## IFastRenderer
The most basic is `IFastRenderer` interface. Any *source* _should_ implement it.

Although the runtime invocation of method `getOut()` doesn't depend on interface implementation, it is kind of mandatory requirement (_must_). Really `TemplateLoader` accepts any class as a *source* specifying parameter, however it is declared to be `Class<? extends IFastRenderer>` (because of generic type erasure). Also it is possible to change interface `ITemplate` to accept any object as a parameter to avoid class cast. But changes like are strictly forbidden OO-concept.

Declared medhods:
 * `getOut` – _should_ return the same writer as used by *source* object to render itself

## ITemplate
This interface represents generated *template* classes.

Declared methods:
 * `process` – performs template rendering to `Writer` acquired from the given *source*

## ITemplateSource
Implementation of this interface provides `TemplateLoader` with *raw template* data. `ITemplateSource` doesn't load data itself, instead it produces `ITemplateResource` instances.

Declared methods:
 * `getResource` – get `ITemplateResource` instance that will be used to access *raw template*

## ITemplateResource
Instances implementing this interface are produced with `getResource` method of `ITemplateSource` implementation on `TemplateLoader` request.

_In future_ `ITemplateResource` _should_ become "update-aware". It means that `TemplateLoader` (or an object of higher level) could ask the instance of `ITemplateResource` if the data, taken last time, is "stale" (the underlying resource has been updated).

To ease the common-case implementation of this interface an inner static class `Utils` is built into interface.

Declared methods:
 * `getContent` – load *raw template* data from underlying resource
 * `Utils.loadContent` – a static method, loads the content of a given `java.io.InputStream` to `String`

## TemplateLoader
This class is responsible for *template* loading. Is is initialized with `ITemplateSource` instance.

Public methods:
 * `loadTemplate` – returns instance of `ITemplate` for given template name, *source* class, and (optionally) writer class; *raw template* is accessed through `ITemplateSource` given on instance initialization

# Examples
## All-in-one example
```
public class TemplateLoaderTest {

	// A simplest possible yet configurable implementation
	public static class FakeResource implements ITemplateResource {
		private final String text;

		public FakeResource(String text) {
			this.text = text;
		}

		@Override
		public String getContent() throws IOException {
			return text;
		}
	}

	// A simplest possible yet configurable implementation
	public static class FakeSource implements ITemplateSource {
		// Can set this runtime or instantiation time
		private String text;

		public FakeSource(String text) {
			this.text = text;
		}

		@Override
		public ITemplateResource getResource(String fileName) {
			return new FakeResource(text);
		}
	}

	// A simple source example
	public static class FakeRenderer implements IFastRenderer {
		// After template processing we could check results
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
		// here we configure our fake resource-source with a raw template
		FakeSource fakeSource = new FakeSource("Hello {stub} World");

		// instantiate loader
		// should have one loader per application context
		// avoid reinitialization is a good practice
		// however it gives no benefits in this case
		TemplateLoader loader = new TemplateLoader(fakeSource);

		// generate/instantiate template class
		// should be cached to avoid performance issues
		ITemplate template = loader.loadTemplate("fake", FakeRenderer.class);


		// new source instance
		FakeRenderer victim = new FakeRenderer();

		// template processor invocation
		// could be done many times
		template.process(victim);

		// result checking
		Assert.assertEquals("Hello Cruel World", victim.getOut().toString(), "Incorrect template processing result");
	}
}
```

## Typical web-application ITemplateSource implementation
```
public class TemplateSourceImpl implements ITemplateSource {

	/**
	 * Resources directory, where templates are hold.
	 */
	private static final String TEMPLATES_DIR = "WEB-INF/templates/";

	/**
	 * All template files has the name "<i>templateName</i><b>{@value} </b>". So
	 * it is the suffix.
	 */
	private static final String TEMPLATES_SUFFIX = ".html";

	private final ServletContext loader;

	public TemplateSourceImpl(ServletContext loader) {
		this.loader = loader;
	}

	@Override
	public ITemplateResource getResource(String fileName) {
		String resourceName = TEMPLATES_DIR + fileName + TEMPLATES_SUFFIX;
		return new TemplateResourceImpl(resourceName);
	}

	public class TemplateResourceImpl implements ITemplateResource {

		private final String resourceName;

		public TemplateResourceImpl(String resourceName) {
			this.resourceName = resourceName;
		}

		@Override
		public String getContent() throws IOException {
			return Utils.loadContent(loader.getResourceAsStream(resourceName));
		}
	}
}
```

## Notes
Oh, and of course, *any feedback will make me happy*. Write your comments on these pages, any – positive, negative – response is appreciated. Especially – "feature requests".

----
(С) Klyuchnikov Eugene
