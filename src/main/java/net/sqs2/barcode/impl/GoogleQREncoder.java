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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import net.sqs2.barcode.BarcodeCodec;
import net.sqs2.barcode.BarcodeEncodeRequest;
	
/**
 * This class encodes a text to QR coded image
 * using Google service, fallback when Zxing is not available.
 */
public class GoogleQREncoder extends BarcodeCodec {

    public String decode(BufferedImage image) throws Exception
    {
        throw new UnsupportedOperationException("The Google BarcodeCodec is not able to decode, only encode.");
    }

    public BufferedImage encode(BarcodeEncodeRequest request) throws Exception
    {
        String url;
        try
        {
            url = "https://chart.googleapis.com/chart?chs=" + request.getWidth() + "x" +request.getHeight()+ "&cht=qr&choe=UTF-8&chld=H&chl=" + URLEncoder.encode(request.getText(), request.getEncoding());
        }
        catch (UnsupportedEncodingException e1)
        {
            throw new RuntimeException("Unicode not supported ?", e1);
        }
        return ImageIO.read(new URL(url).openStream());
        
    }

    public boolean supports(String barcodeFlavor)
    {
        return barcodeFlavor.equals("google") || barcodeFlavor.equals("qrcode");
    }
	
	

}
