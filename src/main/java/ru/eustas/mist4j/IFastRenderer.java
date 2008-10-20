package ru.eustas.mist4j;

import java.io.Writer;

/**
 * A complenent interface to {@link Template}.
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.07 - initial version
 */
public interface IFastRenderer {
	/**
	 * @return Writer to be used both by renderer and template engine
	 */
	public Writer getOut();
}
