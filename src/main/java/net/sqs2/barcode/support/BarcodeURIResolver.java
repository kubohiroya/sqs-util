/**
 * 
 */
package net.sqs2.barcode.support;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import net.sqs2.barcode.BarcodeCodec;
import net.sqs2.barcode.service.DefaultBarcodeCodecFactory;
import net.sqs2.xslt.FilterURIResolver;

/**
 * Resolves barcode pseudo URI(s) providing
 * an image of a barcode.
 * 
 * <p>The barcode URI has the following ABNF syntax:
 * <pre>
 * "qrcode:" [ width "x" height ":"]  value
 * </pre>
 * <p>
 * For example:
 * <li>qrcode:100x100:My value
 * <li>qrcode:My other value
 * <p>Default width and height is 120.
 */
public class BarcodeURIResolver extends FilterURIResolver
{
    private static final String QRCODE_SCHEME_PREFIX = "qrcode:";
    
    final BarcodeCodec codec;
    
    public BarcodeURIResolver(URIResolver parent)
    {
        super(parent);
        this.codec = DefaultBarcodeCodecFactory.getInstance().getBarcodeCodec("qrcode");
    }
    public Source resolve(String href, String base) throws TransformerException
    {
        //System.out.println("QRCODE " + href + " " + base);
        final String uri;
        if (href.startsWith(QRCODE_SCHEME_PREFIX)) {
            uri = href;
        } else {
            uri = null;
        }
        //System.out.println("URI is " + uri);
        if (uri!=null) {
            final String toParse = uri.substring(QRCODE_SCHEME_PREFIX.length());
            final int next = toParse.indexOf(':');
            final int height;
            final int width;
            final String content;
            if (next==-1) {
                width = 120;
                height = 120;
                content = toParse;
            } else {
                final String dim = toParse.substring(0, next);
                IntXInt dimObj = IntXInt.parse(dim);
                width = dimObj.first;
                height = dimObj.second;
                content = toParse.substring(next+1);
                
            }
            String url;
            try
            {
                url = "https://chart.googleapis.com/chart?chs=" + width + "x" +height + "&cht=qr&choe=UTF-8&chld=H&chl=" + URLEncoder.encode(content, "UTF-8");
            }
            catch (UnsupportedEncodingException e1)
            {
                throw new RuntimeException("Unicode not supported ?", e1);
            }
            final StreamSource source;
            try
            {
                source = new StreamSource(new URL(url).openStream());
            }
            catch (IOException e)
            {
                throw new TransformerException(e);
            }
            System.out.println("RETURN GOOGLE QRCODE source " + url);
            return source;
        }
        else return parent.resolve(href, base);
    }
    
}