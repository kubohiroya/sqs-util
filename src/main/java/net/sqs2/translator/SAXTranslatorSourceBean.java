package net.sqs2.translator;

import org.xml.sax.InputSource;

public class SAXTranslatorSourceBean extends TranslatorSourceBean implements SAXTranslatorSource{

	InputSource saxSource;
	
	public SAXTranslatorSourceBean(InputSource saxSource){
		this.saxSource = saxSource;
	}
	
	@Override
	public InputSource getInputSource() {
		return saxSource;
	}
}
