/**
 *  File name:     QRDecoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   Decodes QR code to text.
 */

package net.sqs2.barcode.support;

import net.sqs2.barcode.BarcodeEncodeRequest;

/**
 * Abstracts a request to encode a value
 * into a barcode image. 
 */
public class BarcodeEncode extends BarcodeEncodeRequest {

    private int width = 120;
    private int height = 120;
    private String text = null;
    private String encoding = "UTF-8";
    public final void setDimension(int square)
    {
        setWidth(square);
        setHeight(square);
    }
    
    
    public final int getWidth()
    {
        return width;
    }
    public final void setWidth(int width)
    {
        this.width = width;
    }
    public final int getHeight()
    {
        return height;
    }
    public final void setHeight(int height)
    {
        this.height = height;
    }
    public final String getText()
    {
        return text;
    }
    public final void setText(String text)
    {
        this.text = text;
    }


    public final String getEncoding()
    {
        return encoding;
    }


    public final void setEncoding(String encoding)
    {
        this.encoding = encoding==null ? "UTF-8" : encoding;
    }


    
    
}
