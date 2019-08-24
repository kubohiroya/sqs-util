/**
 *  File name:     QRDecoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   Decodes QR code to text.
 */

package net.sqs2.barcode;

import java.awt.image.BufferedImage;

/**
 * Abstracts a class able to decode  and encode barcode images. 
 */
public abstract class BarcodeCodec {

    /**
	 * Decodes a Barcode image to text. 
	 * 
	 * @param image Image to decode.
	 * @return Decoded text.
	 * @throws Exception
	 */
	public abstract String decode(BufferedImage image) throws Exception;
	/**
	 * Encodes text to a Barcode image.
	 * @param request A {@link BarcodeEncodeRequest}, never <code>null</code>.
	 * @return A {@link BufferedImage}, never <code>null</code>.
     */
	public abstract BufferedImage encode(BarcodeEncodeRequest request) throws Exception;
	/**
	 * Returns whether this {@link BarcodeCodec} supports
	 * the passed barcode flavor, for example <code>qrcode</code>.
	 * @param barcodeFlavor A {@link String}, never <code>null</code>.
	 * @return true if supports the flavor.
	 */
	public abstract boolean supports(String barcodeFlavor);
}
