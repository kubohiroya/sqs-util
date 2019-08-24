package net.sqs2.translator;

import java.util.Map;

import javax.xml.transform.URIResolver;

public abstract interface TranslatorSource {

	public abstract String getSystemId();

	public abstract URIResolver getUriResolver();

	public abstract Map<String,String> getParameters();

	public abstract boolean hasParameters();

	public abstract void clearParameters();

}