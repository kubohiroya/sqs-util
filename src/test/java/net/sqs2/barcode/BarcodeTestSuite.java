/**
 *  File name:     QREncoder.java
 *  Package name:  pro.meetme.app.qrcode
 *  Project name:  qrlib
 *  Author:        Miloš Mihajlović
 *  Year:          2012
 *  Description:   QRDecoder and QREncoder JUnit test class.
 */

package net.sqs2.barcode;

import net.sqs2.barcode.impl.TestQRCode;
import net.sqs2.barcode.impl.TestQRCode_RealCases;
import junit.framework.TestSuite;
	
/**
 * QRCode JUnit test suite class. 
 * <ol>
 * <li>20120423 7 tests 1f 0e 
 *  (failure for qrcode that needs to be resized as done in other test)
 * <li>
 * <li>
 * <li>
 * <li>
 * </ol>
 */
public class BarcodeTestSuite extends TestSuite{
	
    public static TestSuite suite() {
        BarcodeTestSuite s = new BarcodeTestSuite();
        s.addTestSuite(TestBarcodeCodec.class);
        s.addTestSuite(TestQRCode.class);
        s.addTestSuite(TestQRCode_RealCases.class);
        return s;
    }
}
