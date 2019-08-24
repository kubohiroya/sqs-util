/*
 * 

 XSLTranslator.java

 Copyright 2004-2007 KUBO Hiroya (hiroya@cuc.ac.jp).

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.sqs2.translator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import net.sqs2.util.FileUtil;
import net.sqs2.xml.XMLUtil;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

//public class XSLTranslator extends AbstractTranslator implements Translator {
public class XSLTranslator implements Translator {

	String[] baseURIArray = null;
	protected String[] xsltFilenames = null;
	protected Map<String, ParamEntry[]> xsltParamEntryArrayMap = null;

	public XSLTranslator(String[] baseURIArray, String[] xsltFilenames, Map<String, ParamEntry[]> xsltParamEntryArrayMap) throws TranslatorException {
			/*
		 System.setProperty("javax.xml.transform.TransformerFactory",
				"org.apache.xalan.processor.TransformerFactoryImpl");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.parsers.SAXParserFactory",
				"org.apache.xerces.jaxp.SAXParserFactoryImpl");
		 */

		this.baseURIArray = baseURIArray;
		
		if (xsltFilenames == null) {
			throw new RuntimeException("XSLT filenames cannot be null!");
		} else {
			this.xsltFilenames = xsltFilenames;
		}
		if (xsltParamEntryArrayMap != null) {
			this.xsltParamEntryArrayMap = xsltParamEntryArrayMap;
		}
	}

	public XSLTranslator(String baseURI, String[] xsltFilenames, Map<String, ParamEntry[]> xsltParamEntryArrayMap) throws TranslatorException {
		this(new String[] { baseURI }, xsltFilenames, xsltParamEntryArrayMap);
	}
	
	private boolean isDebugMode(){
		return false;
	}

	private TransformerHandler[] createSAXTransformerHandlers(URIResolver uriResolver) throws TransformerFactoryConfigurationError, TransformerConfigurationException{
		SAXTransformerFactory tFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
		tFactory.setURIResolver(uriResolver);
		
		TransformerHandler[] tHandlers = new TransformerHandler[this.xsltFilenames.length];
		List<String> missingResourceURI = null;
		for (String baseURI : baseURIArray) {
			missingResourceURI = new ArrayList<String>();
			for (int i = 0; i < this.xsltFilenames.length; i++) {
				String xsltFilename = this.xsltFilenames[i];
				InputStream inputStream = null;
				try{
					inputStream = createInputStream(xsltFilename, baseURI, uriResolver);
					StreamSource streamSource = createStreamSource(inputStream);
					streamSource.setSystemId(baseURI + xsltFilename);
					TransformerHandler tHandler = tFactory.newTransformerHandler(streamSource);
					if(tHandler != null){
						tHandler.setSystemId(baseURI + xsltFilename);
						if(this.xsltParamEntryArrayMap != null){
							ParamEntry[] xsltParamEntryArray = this.xsltParamEntryArrayMap.get(xsltFilename);
							if (xsltParamEntryArray != null) {
								setTransformerParameters(tHandler, xsltParamEntryArray);
							}
						}
						tHandlers[i] = tHandler;
						if (0 < i) {
							TransformerHandler prevHandler = tHandlers[i - 1];
							prevHandler.setResult(new SAXResult(tHandler));
						}
					}
					
					if(isDebugMode()) Logger.getLogger(getClass().getSimpleName()).info("FOUND: "+baseURI	+xsltFilename);
					
				}catch(IOException ex){
					if(isDebugMode()) Logger.getLogger(getClass().getSimpleName()).severe("not found: "+baseURI	+xsltFilename);
					missingResourceURI.add(baseURI	+xsltFilename);
				}finally{
					IOUtils.closeQuietly(inputStream);
					//inputStream.close();
				}
			}
			if(missingResourceURI.size() == 0){
				break;
			}
		}
		if(0 < missingResourceURI.size()){
			throw new RuntimeException("baseURI is invalid, resource loading failed:"+missingResourceURI);
		}
		return tHandlers;
	}

	private void setTransformerParameters(TransformerHandler tHandler, ParamEntry[] xsltParams) {
		Transformer transformer = tHandler.getTransformer();
		if (xsltParams == null) {
			return;
		}
		for (ParamEntry entry : xsltParams) {
			transformer.setParameter(entry.getKey(), entry.getValue());
		}
	}

	private StreamSource createStreamSource(InputStream inputStream) throws IOException {
		return new StreamSource(createInputStreamReader(inputStream));
	}
	
	private InputStreamReader createInputStreamReader(InputStream inputStream) throws IOException {
		return new InputStreamReader(inputStream, "UTF-8");
	}
	
	private InputStream createInputStream(String href, String base, URIResolver uriResolver) throws IOException {
        Source source;
        try{
       		source = uriResolver.resolve(href, base);
        }catch (Exception e){
            source = null;
        }
        InputStream inputStream = null;
        if (source instanceof StreamSource) {
            inputStream = ((StreamSource) source).getInputStream();
        } 
        if (inputStream==null) {
            URL u = null; 
            if(base.endsWith("/")){
                u = new URL(base +href);
            }else{
                u = new URL(FileUtil.getBasepath(base) +'/'+ href);
            }
            inputStream = u.openStream();
        }
        return inputStream;
    }

	private TransformerHandler[] createTranslaterHandlers(URIResolver uriResolver, Map<String,String> parameters) throws IOException,TransformerConfigurationException{
		TransformerHandler[] tHandlers = createSAXTransformerHandlers(uriResolver);
		if (parameters != null) {
            for (TransformerHandler transformerHandler : tHandlers) {
                for (Map.Entry<String, String> e : parameters.entrySet()) {
                    transformerHandler.getTransformer().setParameter(e.getKey(), e.getValue());
                }
            }
        }
		return tHandlers;
	}

	public Document translate(DOMTranslatorSource source) throws TranslatorException {
		try{
			Document resultDocument = XMLUtil.createDocumentBuilder().newDocument();
			translate(source, resultDocument);
			return resultDocument;
		}catch(ParserConfigurationException ex){
			throw new TranslatorException(ex);
		}catch(SAXException ex){
			throw new TranslatorException(ex);
		}
	}
	public void translate(DOMTranslatorSource source, Document resultDocument) throws TranslatorException {
		try{
			DOMResult result = new DOMResult(resultDocument);
			
			InputSource inputSource = new DocumentInputSource(source.getDocument());
			inputSource.setSystemId(source.getSystemId());
			inputSource.setEncoding("UTF-8");
			
			TransformerHandler[] tHandlers = createTranslaterHandlers(source.getUriResolver(), source.getParameters());
			tHandlers[tHandlers.length - 1].setResult(result);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(tHandlers[0]);
			reader.parse(inputSource);
		}catch(TransformerConfigurationException ex){
			throw new TranslatorException(ex);
		}catch(SAXException ex){
			throw new TranslatorException(ex);
		}catch(IOException ex){
			throw new TranslatorException(ex);
		}
	}
	
	public void translate(SAXTranslatorSource source, SAXResult saxResult) throws TranslatorException {
		try{
			TransformerHandler[] tHandlers = createTranslaterHandlers(source.getUriResolver(), source.getParameters());
			tHandlers[tHandlers.length - 1].setResult(saxResult);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(tHandlers[0]);
			reader.parse(source.getInputSource());
		}catch(TransformerConfigurationException ex){
			throw new TranslatorException(ex);
		}catch(SAXException ex){
			throw new TranslatorException(ex);
		}catch(IOException ex){
			throw new TranslatorException(ex);
		}finally{
		}
	}

	public void translate(SAXTranslatorSource source, OutputStream outputStream) throws TranslatorException {
		try{
			SAXResult result = XMLUtil.createSAXResult(outputStream);
			TransformerHandler[] tHandlers = createTranslaterHandlers(source.getUriResolver(), source.getParameters());
			tHandlers[tHandlers.length - 1].setResult(result);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(tHandlers[0]);
			reader.parse(source.getInputSource());
		}catch(TransformerConfigurationException ex){
			throw new TranslatorException(ex);
		}catch(SAXException ex){
			throw new TranslatorException(ex);
		}catch(IOException ex){
			throw new TranslatorException(ex);
		}
	}

	public InputStream translate(StreamTranslatorSource source) throws TranslatorException {
		PipedOutputStream tmpOutputStream = new PipedOutputStream();
	       try{
	       PipedInputStream resultReadableInputStream = new PipedInputStream(tmpOutputStream);
	       translate(source, tmpOutputStream);
	       
	       return resultReadableInputStream;
		}catch(IOException ex){
			throw new TranslatorException(ex);
		}finally{
			//IOUtils.closeQuietly(tmpOutputStream);
		}
	}

	public void translate(StreamTranslatorSource source, OutputStream outputStream) throws TranslatorException {
		InputStream is = source.createInputStream();
		try{
			SAXResult result = XMLUtil.createSAXResult(outputStream);
			InputSource inputSource = new InputSource(is);
			inputSource.setSystemId(source.getSystemId());
			inputSource.setEncoding("UTF-8");

			TransformerHandler[] tHandlers = createTranslaterHandlers(source.getUriResolver(), source.getParameters());
			tHandlers[tHandlers.length - 1].setResult(result);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(tHandlers[0]);
			
			reader.parse(inputSource);


			
		}catch(TransformerConfigurationException ex){
			throw new TranslatorException(ex);
		}catch(SAXException ex){
			throw new TranslatorException(ex);
		}catch(IOException ex){
			throw new TranslatorException(ex);
		}finally{
			
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(outputStream);
		}
	}

}
