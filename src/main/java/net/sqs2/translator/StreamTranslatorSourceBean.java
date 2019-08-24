package net.sqs2.translator;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.InputStream;
import java.util.Map;
import javax.xml.transform.URIResolver;

public class StreamTranslatorSourceBean extends TranslatorSourceBean implements StreamTranslatorSource
{
	byte[] sourceBytes;

	public StreamTranslatorSourceBean(String systemId, byte[] sourceBytes, URIResolver resolver){
		this(sourceBytes, resolver, null);
		setSystemId(systemId);
	}

	public StreamTranslatorSourceBean(File file, byte[] sourceBytes, URIResolver resolver){
		this(sourceBytes, resolver, null);
		setInputFile(file);
	}
	
	private StreamTranslatorSourceBean(byte[] sourceBytes, URIResolver resolver, Map<String,String> params){
        this.sourceBytes = sourceBytes;
        setUriResolver(resolver);
        if(params != null){
        	putParameters(params);
        }
    }

    public final InputStream createInputStream(){
        return new ByteArrayInputStream(this.sourceBytes);
    }
    
    public byte[] getSourceBytes(){
    	return this.sourceBytes;
    }

}
