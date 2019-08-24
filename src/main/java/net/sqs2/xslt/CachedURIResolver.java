/**
 * 
 */
package net.sqs2.xslt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

/**
 * An URIResolver that caches the sources retrieved by its parent resolver,
 * when possible; if the parent returns null and the 
 * URI is an http: or https: this class will load and cache its bytes.
 * 
 * <p>Note: for SQS this class caches for example the user images,
 * and the other URI(s) that is asked by SQS, that are:
 * <li><code>param.xsl, class://net.sqs2.translator.impl.TranslatorJarURIContext/xslt/convert3.xsl</code>
 * <li><code>class://net.sqs2.impl.TranslatorJarURIContext/xslt/marking-examples_it.svg, null</code>
 */
public class CachedURIResolver extends FilterURIResolver 
{
    public CachedURIResolver(URIResolver parent)
    {
        super(parent);
        if (isDebugEnabled()) debug("new CachedURIResolver(" + parent+ ")");
        
    }

    public Source resolve(String href, String base) throws TransformerException
    {
        try
        {
            return _resolve(href, base);
        }
        catch (IOException e)
        {
            throw new TransformerException("IO Exception for [" + href + "]: " + e.getMessage(), e);
        }
        catch (URISyntaxException e)
        {
            throw new TransformerException("URISyntaxException for [" + href + "]: " + e.getMessage(), e);
        }
    }
    private Source _resolve(String href, String base) throws TransformerException, IOException, URISyntaxException
    {
        if (isDebugEnabled()) debug("resolve(" + href + ", " + base+ ") - ENTER");
        // Simple impl, do not resolve..
        final URI baseObj = base == null ? null : new URI(base);
        final URI hrefObj = new URI(href);
        final URI resolvedObj = baseObj==null ? hrefObj : baseObj.resolve(hrefObj);
        final String key = resolvedObj.toString();
        final Source test = getCached(key);
        if (test != null) return test;
        
        final Source parentLoaded = parent.resolve(href, base);
        final Source loaded;
        if (parentLoaded == null) {
            if (href.startsWith("http:") || href.startsWith("https:")) {
                if (isDebugEnabled()) debug("Create stream source with url " + href);
                loaded = new StreamSource(key);
            } else { 
                if (isDebugEnabled()) debug("Parent returned null.");
                return null;
            }
        } else {
            loaded =parentLoaded;
        }
        
        byte[] bytes = null;
        if (loaded instanceof StreamSource) {
            final StreamSource stream = (StreamSource) loaded;
            final InputStream is = stream.getInputStream();
            if (is != null) {
                bytes = IOUtils.toByteArray(is);
                if (isDebugEnabled()) debug("Loaded bytes from StreamSource, " + bytes.length + " bytes.");
            } 
        }
        if (bytes == null) {
            final String systemId = loaded.getSystemId();
            final URL url = new URL(systemId);
            bytes = IOUtils.toByteArray(url.openStream());
            if (isDebugEnabled()) debug("Loaded bytes from URL, " + bytes.length + " bytes.");
            
        }
        if (bytes!=null) {
            cache(key, bytes);
            return getCached(key);
        }
        return loaded;
        
        
    }

    private Source getCached(final String key)
    {
        final byte[] test = cache.get(key);
        if (test != null) {
            if (isDebugEnabled()) debug("Found cached for (" + key + "), " + test.length + " bytes.");
            final ByteArrayInputStream bais = new ByteArrayInputStream(test);
            return new StreamSource(bais, key);
        }
        else {
            if (isDebugEnabled()) debug("Not Found cached for (" + key + ").");
            return null;
        }
    }
    final HashMap<String,byte[]> cache = new HashMap<String,byte[]>();
    private void cache(String key, byte[] bytes)
    {
        if (isDebugEnabled()) debug("Caching for (" + key + "), " + bytes.length + " bytes.");
        
        cache.put(key, bytes);
    }

    
    private void debug(String string)
    {
        System.out.println(string);
    }

    private boolean isDebugEnabled()
    {
        return false;
    }
}