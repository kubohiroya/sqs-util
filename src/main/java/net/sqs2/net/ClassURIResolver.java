package net.sqs2.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlgraphics.util.uri.CommonURIResolver;

/**
 * An TrAX {@link URIResolver} implementation
 * that loads resources from the class path
 * if the <code>class:</code> scheme is used,
 * otherwise defers loading to {@link URL}.
 *
 */
public class ClassURIResolver extends CommonURIResolver implements URIResolver {

	public ClassURIResolver(){
	}
	/**
	 * Configures the {@link ClassLoader} to be used
	 * to load the resources with <code>class:</code> pseudo scheme.
	 * @param classLoader A {@link ClassLoader} or <code>null</code>.
	 */
	public void setClassLoader(ClassLoader classLoader) {
	    classURLHelper.setClassLoader(classLoader);
	}
	
	private final ClassURLHelper classURLHelper = new ClassURLHelper();
    
	public Source resolve(final String href, final String base) {
	    try {
			final String urlString;
			if (0 < href.indexOf(":") || base == null) {
				urlString = href;
			} else if (href.indexOf(":") == -1){
				if(0 < base.indexOf(":")) {
					if(base.endsWith("/")){
						urlString = base + href;
					}else{
						urlString = base.substring(0, base.lastIndexOf('/')+1) + href;
					}
				} else {
					throw new RuntimeException("invalid url: " + base + href);
				}	
			}else{
				urlString = href;
			}
			// Here the problem was that ClassURLConnection was used
			// but in web apps is not allowed
			// to install alternate URL handlers and normal url fails
			// on construction when called with a "class:.." scheme.
			// so now use [classURLHelper]
          if(urlString.startsWith("class:")){
                final InputStream inputStream = classURLHelper.getInputStream(urlString);
                if (inputStream != null) {
                    return new StreamSource(inputStream, urlString);
                }
                throw new RuntimeException("class url connection failed:" + urlString);
            }else{
                final URL url = new URL(urlString);
                return new StreamSource( url.openConnection().getInputStream(), urlString); // super.resolve(href, base);
            }
		} catch (IOException ex) {
			throw new RuntimeException("class url connection failed:" + base + href);
		}
	}
}
