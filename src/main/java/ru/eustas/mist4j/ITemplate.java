package ru.eustas.mist4j;

import java.io.IOException;
import java.io.Writer;

/**
 * Interface implemented by generated renderer.
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public interface ITemplate {
	/**
	 * Call this method to render template.
	 * 
	 * Implementation will invoke {@link IFastRenderer#getOut()} to get output,
	 * and then will sequentially put literal text and call
	 * <tt><b>void render</b><i>XXX</i><b>()</b></tt> methods of victim.
	 * 
	 * @param victim
	 *            source-object, that will be invoked to render substituton
	 *            parts
	 * @throws IOException
	 *             thrown by {@link Writer#write(char[], int, int)} or
	 *             source-object render methods
	 */
	void process(IFastRenderer victim) throws IOException;
}
