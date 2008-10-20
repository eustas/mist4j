package ru.eustas.mist4j;

import java.io.IOException;


public interface ITemplate {
	void process(IFastRenderer victim) throws IOException;
}
