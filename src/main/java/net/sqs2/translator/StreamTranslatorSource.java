package net.sqs2.translator;

import java.io.InputStream;


/**
 * A source for a {@link Translator}
 * 
 * <p>One of <em>InputStream</em> or <em>SystemId</em>
 * must be present, URI resolver is optional.
 *
 */
public abstract interface StreamTranslatorSource extends TranslatorSource
{
	public abstract InputStream createInputStream();
	public abstract byte[] getSourceBytes();
}