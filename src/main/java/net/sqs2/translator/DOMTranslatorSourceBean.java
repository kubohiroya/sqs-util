package net.sqs2.translator;

import org.w3c.dom.Document;

public class DOMTranslatorSourceBean extends TranslatorSourceBean implements DOMTranslatorSource{

	Document document;
	
	public DOMTranslatorSourceBean(Document document){
		this.document = document;
	}
	
	@Override
	public Document getDocument() {
		return document;
	}


}
