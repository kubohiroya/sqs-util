package net.sqs2.translator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.URIResolver;

public abstract class TranslatorSourceBean implements TranslatorSource {

	private String systemId;
	private URIResolver uriResolver;
	private HashMap<String,String> parameters = new HashMap<String, String>();

	public TranslatorSourceBean() {
		super();
	}

	public final void setInputFile(File file) {
	   String uri = file.getAbsolutePath().replace('\\', '/');
	    if (!uri.startsWith("/")) uri = "/" + uri;
	    this.systemId = "file:" + uri;
	}

	public final String getSystemId() {
	    return systemId;
	}

	public final void setSystemId(String systemId) {
	    this.systemId = systemId;
	}

	public final URIResolver getUriResolver() {
	    return uriResolver;
	}

	public final void setUriResolver(URIResolver uriResolver) {
	    this.uriResolver = uriResolver;
	}

	@Override
	public Map<String, String> getParameters() {
	    // Here may clone..
	    return parameters;
	}

	@Override
	public boolean hasParameters() {
	    return !parameters.isEmpty();
	}

	@Override
	public void clearParameters() {
	    parameters.clear();
	}

	public void putParameters(Map<String, String> params) {
	    this.parameters.putAll(params);
	}

}