package ru.eustas.mist4j;

import java.io.Writer;

/**
 * A complement interface to {@link ITemplate}.
 * 
 * <p>
 * A "source" object must implement this interface, along with
 * <tt><b>void render</b><i>XXX</i><b>()</b></tt> methods.
 * </p>
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public interface IFastRenderer {
	/**
	 * This method is invoked by the generated template renderer, to get stream
	 * to output to.
	 * 
	 * @return {@link Writer} to be used both by renderer and template engine
	 */
	public Writer getOut();
}
