/**
 *  File name:     QREncoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   QRDecoder and QREncoder JUnit test class.
 */

package net.sqs2.barcode.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.TestCase;
import net.sqs2.barcode.impl.ZxingBarcodeCodec;
import net.sqs2.barcode.impl.ZxingGeneralBarcodeCodec;
import net.sqs2.barcode.support.BarcodeEncode;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
	
/**
 * QRDecoder and QREncoder JUnit test class. They use ZXing open-source barcode image 
 * processing library.
 * 
 * More about QR code: http://en.wikipedia.org/wiki/QR_code
 * ZXing library home page: http://code.google.com/p/zxing/
 * ZXing documentation: http://zxing.org/w/docs/javadoc/index.html
 */
public class TestQRCode extends TestCase {
	
    /**
     * Tests both QRDecoder and QREncoder regular main functionality.
     * 
     * @throws Exception
     */
    public void testEncoderAndDecoder() throws Exception {
        final String myText = "https://issues.apache.org/jira/secure/Dashboard.jspa";
        QREncoder encoder = new QREncoder();
        BufferedImage bufferedImage = encoder.encode(myText);
        assertNotNull(bufferedImage);
        final String myTextDecoded = new ZxingBarcodeCodec().decode(bufferedImage);
        assertEquals(myTextDecoded, myText);
        final String myTextDecoded2 = new ZxingGeneralBarcodeCodec().decode(bufferedImage);
        assertEquals(myTextDecoded2, myText);

    }
    /**
     * Tests both QRDecoder and QREncoder regular main functionality.
     * 
     * @throws Exception
     */
    public void testGoogleEncoderAndDecoder() throws Exception {
        final String myText = "https://issues.apache.org/jira/secure/Dashboard.jspa";
        GoogleQREncoder encoder = new GoogleQREncoder();
        final BarcodeEncode request = new BarcodeEncode();
        request.setText(myText);
        BufferedImage bufferedImage = encoder.encode(request);
        assertNotNull(bufferedImage);
        final String myTextDecoded = new ZxingBarcodeCodec().decode(bufferedImage);
        assertEquals(myTextDecoded, myText);
        final String myTextDecoded2 = new ZxingGeneralBarcodeCodec().decode(bufferedImage);
        assertEquals(myTextDecoded2, myText);

    }
    
	/**
	 * Tests QREncoder regular main functionality.
	 * 
	 * @throws WriterException
	 * @throws IOException
	 */
	public void testEncoder() throws WriterException, IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// cannot write file in tests..
		//File file = getResource("output-image-01.png");
		
		final QREncoder qrEncoder = new QREncoder();
		qrEncoder.setHeight(600);
		qrEncoder.setWidth(800);
        final BufferedImage result = qrEncoder.encode(TEXT);
		ImageIO.write(result, "png", baos);
		
	}
	
	// MIk I removed the UTF-8 chars.. please do an UTF-8 test
	// but use \\uxxxx escaping
    final String TEXT = "who would've thought";
    
	/**
	 * Tests QRDecoder regular main functionality.
	 * 
	 * @throws IOException
	 * @throws NotFoundException
	 * @throws WriterException 
	 */
	public void testDecoder() throws Exception
	{	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    {
            final String text = "who would've thought";
            
            final QREncoder qrEncoder = new QREncoder();
            qrEncoder.setHeight(600);
            qrEncoder.setWidth(800);
            final BufferedImage result = qrEncoder.encode(text);
            ImageIO.write(result, "png", baos);
            
	    }    
		final BufferedImage image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
		final String result = new ZxingBarcodeCodec().decode(image);
		assertEquals(TEXT, result);
	}
}
