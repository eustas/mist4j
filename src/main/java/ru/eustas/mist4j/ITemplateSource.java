package ru.eustas.mist4j;

/**
 * {@link ITemplateResource} factory abstraction.
 * 
 * <p>
 * {@link #getResource(String)} is a typical wrapper for
 * {@link ClassLoader#getResourceAsStream(String)} and
 * ServletContext.getResourceAsStream(String) methods. Implementation is usually
 * based on appropriate resource loader / generator & some rules to wrap
 * template-name to resource name.
 * </p>
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public interface ITemplateSource {
	/**
	 * Get instance of {@link ITemplateResource} corresponding to template name.
	 * 
	 * @param templateName
	 * @return
	 */
	ITemplateResource getResource(String templateName);
}
