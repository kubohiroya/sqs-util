/**
 *  File name:     QRDecoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   Decodes QR code to text.
 */

package net.sqs2.barcode;


/**
 * Abstracts a request to encode a value
 * into a barcode image. 
 */
public abstract class BarcodeEncodeRequest {

    /**
     * Retrieves the requested output width.
     */
    public abstract int getWidth() ;
    /**
     * Retrieves the requested output height.
     */
    public abstract int getHeight() ;
    
    /**
     * Retrieves the requested output text,
     * never returns null.
     */
    public abstract String getText() ;
    /**
     * Retrieves the requested output encoding,
     * never returns null.
     */
    public abstract String getEncoding() ;
    
    
}
