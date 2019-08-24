package net.sqs2.translator;

import org.xml.sax.InputSource;

/**
 * A source for a {@link Translator}
 * 
 */
public abstract interface SAXTranslatorSource extends TranslatorSource{
	public InputSource getInputSource() ;
}