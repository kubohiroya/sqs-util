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
 * Abstracts a factory for {@link BarcodeCodec}.
 */
public abstract class BarcodeCodecFactory {
    /**
     * Retrieves a {@link BarcodeCodec} for the passed
     * barcode flavor, eg: <code>qrcode</code>.
     * @param flavor A {@link String}, never <code>null</code>.
     * @return A {@link BarcodeCodec} or <code>null</code>
     *  if cannot find an appropriate one.
     *  
     */
    public abstract BarcodeCodec getBarcodeCodec(String flavor);
}
