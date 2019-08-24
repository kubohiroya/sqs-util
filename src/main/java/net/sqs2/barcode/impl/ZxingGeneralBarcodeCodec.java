/**
 *  File name:     QRDecoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   Decodes QR code to text.
 */

package net.sqs2.barcode.impl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import net.sqs2.barcode.BarcodeCodec;
import net.sqs2.barcode.BarcodeEncodeRequest;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

/**
 * A {@link BarcodeCodec} that is able to 
 * scan a wider image to read a barcode, implemented on the top
 * of Zxing. 
 */
public class ZxingGeneralBarcodeCodec extends BarcodeCodec {

    private static String DEFAULT_ENCODING = "UTF-8";

    
    public String decode(BufferedImage image) throws Exception
    {
        return _decode(image, "UTF-8");
    }

    public BufferedImage encode(BarcodeEncodeRequest request) throws Exception
    {
        final QREncoder encoder = new QREncoder();
        encoder.setHeight(request.getHeight());
        encoder.setWidth(request.getWidth());
        return encoder.encode(request.getText());
    }

    public boolean supports(String barcodeFlavor)
    {
        return barcodeFlavor.equalsIgnoreCase("general");
    }
    
    private static String _decode(BufferedImage image, String charset) throws Exception
    {
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        hints.put(DecodeHintType.CHARACTER_SET, charset==null ? DEFAULT_ENCODING : charset);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        //hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        final ArrayList formats = new ArrayList();
        formats.add("qrcode");
        formats.add("datamatrix");
        hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        final BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(image)));
        //final Detector detector = new Detector(binaryBitmap.getBlackMatrix());
        //final DetectorResult dres = detector.detect(hints);
        
        final Result result = new MultiFormatReader().decode(binaryBitmap, hints);
        return result.getText();

    }

    
}
