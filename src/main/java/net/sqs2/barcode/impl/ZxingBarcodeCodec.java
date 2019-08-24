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
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;


/**
 * A {@link BarcodeCodec} implemented on the top
 * of Zxing. 
 */
public class ZxingBarcodeCodec extends BarcodeCodec {

    
    public String decode(BufferedImage image) throws Exception
    {
        return decode(image, "UTF-8");
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
        return barcodeFlavor.equalsIgnoreCase("qrcode");
    }


    /**
     * Decodes QR coded image to text.
     * 
     * @param image Image to decode.
     * @param charset optional charset/encoding which should be used when decoding,
     *     by default is UTF-8
     * @return Decoded text.
     * @throws NotFoundException
     */
    String decode(BufferedImage image, String charset) throws Exception {
        try {
            return _decode(image, charset);
        } catch (NotFoundException e) {
            //if (image.getWidth()*image.getHeight() < 22500) 
            {
                //final BufferedImage  bufferedImageResized = ImageUtils.resample(image, 300, 300);
                //final BufferedImage  bufferedImageResized = ImageUtils.resample(image, image.getWidth()*2, image.getHeight()*2);
                //return _decode(bufferedImageResized, charset);
            }
            throw e;
        }
    }

    private static String _decode(BufferedImage image, String charset) throws NotFoundException, FormatException, Exception
    {
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        if(charset!=null)hints.put(DecodeHintType.CHARACTER_SET, charset);
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
        
        final Result[] result = new QRCodeMultiReader().decodeMultiple(binaryBitmap, hints);
        if( result.length==0) throw new Exception("no qrcode found");
        return result[0].getText();

    }

    

}
