/**
 *  File name:     QREncoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   Encodes text into QR code.
 */

package net.sqs2.barcode.impl;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
	
/**
 * This class encodes a text to QR coded image. It uses ZXing open-source barcode image 
 * processing library.
 * <p>
 * Use setters to configure parameters (height, width of the image and text charset) and 
 * then the {@link #encode(String)} method to encode text to a QR coded image.
 * <p>
 * More about QR code: http://en.wikipedia.org/wiki/QR_code
 * Link to home page of the QR barcode image processing library ZXing used in this class:
 * http://code.google.com/p/zxing/
 * ZXing documentation: http://zxing.org/w/docs/javadoc/index.html
 */
class QREncoder {
	
	private static final String DEFAULT_ENCODING = "UTF-8";

    private int width = 400;
	
	private int height = 400;
	
	private String charset = DEFAULT_ENCODING;
	
	
	
	/**
	 * Encodes a text into a QR coded image and returns the image.
	 * 
	 * @param text Text to encode.
	 * @return The encoded QR code image.
	 * @throws WriterException when something goes wrong
	 */
	public BufferedImage encode(String text) throws WriterException {
		final QRCodeWriter writer = new QRCodeWriter();

		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
		hints.put(EncodeHintType.CHARACTER_SET, charset==null ? DEFAULT_ENCODING : charset);
		
		final BitMatrix mtx = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        if (mtx == null) 
            throw new IllegalStateException("Did not return BitMatrix ?");
        final BufferedImage image  = MatrixToImageWriter.toBufferedImage(mtx);
        
		return image;
	}
	
	/////////////The following are configuration methods...
	
	
	public int getWidth() {
		return width;
	}
	/**
	 * Desired width of the qrcode image.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	

	public int getHeight() {
		return height;
	}
    /**
     * Desired height of the qrcode image.
     */

	public void setHeight(int height) {
		this.height = height;
	}
	
	public String getCharset()
    {
        return charset;
    }
	/**
     * Desired charset encoding of the qrcode image.
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

}
