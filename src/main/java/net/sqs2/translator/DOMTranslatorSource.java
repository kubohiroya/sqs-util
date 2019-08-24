package net.sqs2.translator;

import org.w3c.dom.Document;


/**
 * A source for a {@link Translator}
 * 
 */
public abstract interface DOMTranslatorSource extends TranslatorSource{
	public abstract Document getDocument();
}