/**
 *  File name:     QRDecoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   Decodes QR code to text.
 */

package net.sqs2.barcode.service;

import java.util.Iterator;
import java.util.ServiceLoader;

import net.sqs2.barcode.BarcodeCodec;
import net.sqs2.barcode.BarcodeCodecFactory;


/**
 * An implementation of {@link BarcodeCodecFactory}
 * that uses the {@link ServiceLoader} framework. 
 */
public class DefaultBarcodeCodecFactory extends BarcodeCodecFactory {

    private static ServiceLoader<BarcodeCodec> barcodeCodecLoader
    = ServiceLoader.load(BarcodeCodec.class);

    
    /**
     * static instance.
     */
    private static final DefaultBarcodeCodecFactory instance = new DefaultBarcodeCodecFactory();

    /**
     * Retrieves a thread safe instance.
     */
    public static DefaultBarcodeCodecFactory getInstance()
    {
        return instance;
    }

    /**
     * Constructor for configurations,
     * other clients may consider using {@link #getInstance()}.
     */
    public DefaultBarcodeCodecFactory()
    {
        //
    }

    

    
    
    public BarcodeCodec getBarcodeCodec(String flavor) {
        final Iterator<BarcodeCodec> iter = barcodeCodecLoader.iterator();
        while(iter.hasNext()) {
            BarcodeCodec bc = iter.next();
            if (bc.supports(flavor)) return bc;
        }
        return null;
    }
}
