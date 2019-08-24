/**
 *  File name:     QREncoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        eddie
 *  Year:          2012
 *  Description:   
 */

package net.sqs2.barcode.impl;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import junit.framework.TestCase;
import net.sqs2.barcode.BarcodeCodec;
import net.sqs2.barcode.BarcodeTestSuite;
import net.sqs2.barcode.impl.ZxingBarcodeCodec;
import net.sqs2.barcode.impl.ZxingGeneralBarcodeCodec;
import net.sqs2.barcode.service.DefaultBarcodeCodecFactory;

/**
 * 
 */
public class TestQRCode_RealCases extends TestCase
{

    public void testEncodeAndDecode() throws Exception
    {
        final String myText = "https://issues.apache.org/jira/secure/Dashboard.jspa";
        QREncoder encoder = new QREncoder();
        BufferedImage result = encoder.encode(myText);
        assertNotNull(result);
        final String myTextDecoded = new ZxingBarcodeCodec().decode(result);
        assertEquals(myText, myTextDecoded);
    }

//    /**
//     * Doesn't work without resize (now works with patch in decoder for smaller
//     * images)
//     * 
//     * @throws Exception
//     */
//    public void testDecodeFirstScan() throws Exception
//    {
//        InputStream is = BarcodeTestSuite.class.getResourceAsStream("scan.png");
//        final BufferedImage bufferedImage = getBufferedImage(is, 0);
//        final String myTextDecoded = new ZxingBarcodeCodec().decode(bufferedImage);
//        System.out.println(myTextDecoded);
//        
//    }
    public void testDecodeFirstScan2() throws Exception
    {
        InputStream is = BarcodeTestSuite.class.getResourceAsStream("8.png");
        final BufferedImage bufferedImage = getBufferedImage(is, 0);
        final String myTextDecoded = new ZxingBarcodeCodec().decode(bufferedImage);
        //System.out.println(myTextDecoded);
        assertEquals("SQS00008", myTextDecoded);
    }

    

    private void assertGeneralBarcode(String img, String expected) throws IOException,
                                                           Exception
    {
        assertGeneralBarcode(img, expected, 0);
    }

    private void assertGeneralBarcode(String img, String expected, int crop) throws IOException,
                                                                     Exception
    {
        assertBarcode(img, expected, crop, new ZxingGeneralBarcodeCodec());

    }

    private void assertBarcode(String img,
        String expected,
        int crop,
        BarcodeCodec impl) throws Exception
    {
        //
        InputStream is = BarcodeTestSuite.class.getResourceAsStream(img);
        final BufferedImage bufferedImage = getBufferedImage(is,
            crop);
        final String myTextDecoded = impl.decode(bufferedImage);
        assertEquals(expected, myTextDecoded);

    }

    private void assertDatamatrix(String img, String expected) throws IOException,
                                                              Exception
    {
        //
        InputStream is = BarcodeTestSuite.class.getResourceAsStream(img);
        final BufferedImage bufferedImage = getBufferedImage(is, 0);
        final String myTextDecoded = DefaultBarcodeCodecFactory.getInstance().getBarcodeCodec("general").decode(bufferedImage);
        assertEquals(expected, myTextDecoded);

    }

    

    public void testDecodeGoogle() throws Exception
    {
        assertGeneralBarcode("google.png",
            "BEGIN:VCARD\nVERSION:2.1\nFN:Pizzuto Frankuzzo\nN:Frankuzzo;Pizzuto;\nEND:VCARD");
    }

    public void testDecodeMoroviaCom21x21() throws Exception
    {
        assertGeneralBarcode("21x21_AAAAAA 1.png", "AAAAA 1");
    }

    public void testDecodeMoroviaCom21x21_1px() throws Exception
    {
        assertGeneralBarcode("21x21_AAAAAA 1 1px.png", "AAAAA 1");
    }

    public void testDecodeDataMatrix() throws Exception
    {
        assertDatamatrix("220px-Datamatrix.svg.png",
            "Wikipedia, the free encyclopedia");
    }
    public void testDecodeDataMatrixAsGeneralBarcode() throws Exception
    {
        assertGeneralBarcode("220px-Datamatrix.svg.png",
            "Wikipedia, the free encyclopedia");
    }

    public void testDecodeDataMatrix2_SmallImage() throws Exception
    {
        assertDatamatrix("Data_Matrix.jpg", "FLYMARKER");
    }

    public void testDecodeScannedWithSomeSigns() throws Exception
    {
        // assertBarcode("scanned_withsomeothersigns.png",
        // "Test Stampa PDF Questionario");
        InputStream is = BarcodeTestSuite.class.getResourceAsStream("scanned_withsomeothersigns.png");
        final BufferedImage bufferedImage = getBufferedImage(is,
            1);
        final String myTextDecoded = new ZxingBarcodeCodec().decode(bufferedImage);
        assertEquals(myTextDecoded, "Test Stampa PDF Questionario");
        // Does not work with general
        //final String myTextDecoded2 = new ZxingGeneralBarcodeCodec().decode(bufferedImage);
        //assertEquals(myTextDecoded2, "Test Stampa PDF Questionario");

    }

    public void testDecodeScannedWithSomeSigns_bigger() throws Exception
    {
        assertBarcode("scanned_withsomeothersigns_bigger.png",
            "Test Stampa PDF Questionario", 0, new ZxingGeneralBarcodeCodec());
    }
    /**
     * This doesn't work with GeneralBarcode but works with normal..
     * @throws Exception
     */

    public void testCutByMik() throws Exception
    {
        assertBarcode("cutbymikscan.jpg", "Test Stampa PDF Questionario", 1,
            new ZxingBarcodeCodec());

    }
    
    /**
     * Returns a {@link BufferedImage} from the passed {@link InputStream}.
     * 
     * @param is A {@link InputStream}, never <code>null</code>.
     * @return A {@link BufferedImage}, never <code>null</code>.
     * @throws IOException
     */
    public static BufferedImage getBufferedImage(InputStream is) throws IOException
    {
        return getBufferedImage(is, 0);
    }

    /**
     * Returns a {@link BufferedImage} from the passed {@link InputStream},
     * optionally applying some symmetric crop as per passed parameter.
     * 
     * @param inputStream A {@link InputStream}, never <code>null</code>.
     * @param cropSize not negative integer, zero for no crop.
     * @return A {@link BufferedImage}, never <code>null</code>.
     * @throws Exception
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static BufferedImage getBufferedImage(InputStream inputStream,
        int cropSize) throws IOException
    {
        if (inputStream == null)
            throw new IllegalArgumentException("null stream");
        // Mik: better work with File, not with String paths (win/linux diffs)
        // Mik2: changed to input stream..
        final BufferedImage bufferedImage;
        try
        {
            bufferedImage = ImageIO.read(inputStream);
        }
        finally
        {
            inputStream.close();
        }

        if (bufferedImage == null)
            throw new IllegalArgumentException("invalid stream passed, cannot create image");
        if (cropSize != 0)
        {
            try
            {
                int cropWidth = bufferedImage.getWidth() - 2 * cropSize;
                int cropWHeight = bufferedImage.getHeight() - 2 * cropSize;
                return crop(bufferedImage,
                    cropSize,
                    cropSize,
                    cropWidth,
                    cropWHeight);
            }
            catch (RuntimeException e)
            {
                throw new IllegalArgumentException("Could not crop image with [" + cropSize
                    + "] border, \ndetails:"
                    + e.getMessage(),
                    e);
            }
        }
        else
        {
            return bufferedImage;
        }

    }

    /**
     * Crop a BufferedImage from coordinates x and y with size of width and
     * height
     * 
     * @param bi image to crop
     * @oaram x starting point x coordinate
     * @param y starting point y coordinate
     * @param width starting image width
     * @param height starting image height
     * @return cropped image;
     */
    public static BufferedImage crop(BufferedImage bi,
        int x,
        int y,
        int width,
        int height)
    {
        Rectangle goal = new Rectangle(width, height);
        try
        {
            Rectangle clip = goal.intersection(new Rectangle(bi.getWidth(),
                bi.getHeight()));
            BufferedImage clippedImg = bi.getSubimage(x,
                y,
                clip.width,
                clip.height);
            return clippedImg;
        }
        catch (RasterFormatException e)
        {
            throw new RuntimeException("RasterFormatException while cropping (x:" + x
                + ",y:"
                + y
                + ",width:"
                + width
                + ",height:"
                + height
                + ") an image with dimensions "
                + bi.getWidth()
                + "x"
                + bi.getHeight()
                + "\ndetails:"
                + e.getMessage());
        }
    }


    

}
