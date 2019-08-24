/**
 *  File name:     QREncoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   QRDecoder and QREncoder JUnit test class.
 */

package net.sqs2.barcode;

import java.awt.image.BufferedImage;

import net.sqs2.barcode.BarcodeCodec;
import net.sqs2.barcode.service.DefaultBarcodeCodecFactory;
import net.sqs2.barcode.support.BarcodeEncode;

import junit.framework.TestCase;
	
/**
 * {@link BarcodeCodec} JUnit test class. 
 */
public class TestBarcodeCodec extends TestCase {
	
    /**
     * Tests returns null for unknown 
     * 
     * @throws Exception
     */
    public void testGetUnknownCodec() throws Exception {
        
        assertNull(DefaultBarcodeCodecFactory.getInstance().getBarcodeCodec("unknownxxxxxxxx"));
    }
    /**
     * Tests returns not null for qrcode
     * 
     * @throws Exception
     */
    public void testGetQrcodeCodec() throws Exception {
        
        assertNotNull("? no codec for qrcode ?", getQRCodeCodec());
    }
    /**
     * Tests encode for qrcode
     * ...and tests decoding
     * 
     * @throws Exception
     */
    public void testEncodeQrcodeCodec() throws Exception {
        
        BarcodeCodec codec = getQRCodeCodec();
        BarcodeEncode encode = new BarcodeEncode();
        encode.setText("MyBarcode");
        BufferedImage bi = codec.encode(encode);
        assertNotNull(bi);
        assertEquals(120, bi.getWidth());
        assertDecode(bi, "MyBarcode");
    }
    /**
     * Tests encode for qrcode with dimensions
     * ...and tests decoding
     * 
     * @throws Exception
     */
    public void testEncodeQrcodeCodecWithDimension() throws Exception {
        
        BarcodeCodec codec = getQRCodeCodec();
        BarcodeEncode encode = new BarcodeEncode();
        encode.setText("MyBarcode");
        encode.setDimension(200);
        BufferedImage bi = codec.encode(encode);
        assertNotNull(bi);
        assertEquals(200, bi.getWidth());
        assertEquals(200, bi.getHeight());
        
        assertDecode(bi, "MyBarcode");
    }
    private void assertDecode(BufferedImage bi, String string) throws Exception
    {
        BarcodeCodec codec = getQRCodeCodec();
        String text = codec.decode(bi);
        assertEquals("decoded qrcode value does not match", string, text);
    }
    
    protected BarcodeCodec getQRCodeCodec()
    {
        return DefaultBarcodeCodecFactory.getInstance().getBarcodeCodec("qrcode");
    }
	
}
