/*
 * 

 ImageFactory.java

 Copyright 2007 KUBO Hiroya (hiroya@cuc.ac.jp).

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.sqs2.image;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.xmlgraphics.image.codec.tiff.*;
import org.apache.xmlgraphics.image.codec.util.FileCacheSeekableStream;

import com.itextpdf.text.pdf.*;

public class ImageFactory {

	private static final ImageTypeIO pdfImageTypeIO = new PDFImageTypeIO(); 
	private static final ImageTypeIO gifImageIOImageTypeIO = new ImageIOImageTypeIO("gif");
	private static final ImageTypeIO jpegImageIOImageTypeIO = new ImageIOImageTypeIO("jpeg");
	private static final ImageTypeIO pngImageIOImageTypeIO = new ImageIOImageTypeIO("png");
	//private static final ImageTypeIO tiffImageTypeIO = new ImageIOImageTypeIO("tiff");
	private static final ImageTypeIO tiffImageTypeIO = new BatikTIFFImageTypeIO();
	private static final ImageTypeIO defaultImageTypeIO = new DefaultImageTypeIO();

	private static final Map<String,ImageTypeIO> imageTypeIOMap = new HashMap<String,ImageTypeIO>(){
		private static final long serialVersionUID = 1L;
		public ImageTypeIO get(String imageType){
			ImageTypeIO imageTypeIO = super.get(imageType.toLowerCase());
			if(imageTypeIO == null){
				// Logger.getLogger(ImageFactory.class.getName()).info("Not Supported:"+imageType);
				return defaultImageTypeIO;
			}else{
				return imageTypeIO; 
			}
		}
	};
	
	static{
		imageTypeIOMap.put("gif", gifImageIOImageTypeIO);
		imageTypeIOMap.put("jpeg", jpegImageIOImageTypeIO);
		imageTypeIOMap.put("jpg", jpegImageIOImageTypeIO);
		imageTypeIOMap.put("png", pngImageIOImageTypeIO);
		imageTypeIOMap.put("mtiff", tiffImageTypeIO);
		imageTypeIOMap.put("tiff", tiffImageTypeIO);
		imageTypeIOMap.put("tif", tiffImageTypeIO);
		imageTypeIOMap.put("pdf", pdfImageTypeIO);
	}
	
	
	public static final boolean isSupportedFileName(final String filename) {
		int p = filename.lastIndexOf(".");
		if(p == -1 || p == filename.length()-1){
			return false;
		}
		String imageType = filename.substring(p+1).toLowerCase();
		return isSupportedType(imageType);
	}

	public static final boolean isSupportedType(final String imageType) {
		return imageTypeIOMap.containsKey(imageType);
	}

	public static BufferedImage createImage(String imageType, File file, int index)throws IOException{
		return imageTypeIOMap.get(imageType).createImage(file, index);
	}
	
	public static BufferedImage createImage(File file, int index)throws IOException{
		String imageType = FilenameUtils.getExtension(file.getName());
		try{
			return imageTypeIOMap.get(imageType).createImage(file, index);
		}catch(IOException ex){
			// Logger.getLogger("IOException:"+file+" "+index);
			throw ex;
		}
	}
	
	public static BufferedImage createImage(String imageType, InputStream in)throws IOException{
		return imageTypeIOMap.get(imageType).createImage(in);
	}
	
	public static BufferedImage createImage(String imageType, byte[] bytes, int indexOfMultiPages)throws IOException{
		return imageTypeIOMap.get(imageType).createImage(bytes, indexOfMultiPages);
	}
	
	public static BufferedImage createImage(String imageType, byte[] bytes)throws IOException{
		return imageTypeIOMap.get(imageType).createImage(bytes, 0);
	}
	
	public static BufferedImage createImage(String imageType, InputStream in, int index)throws IOException{
		return imageTypeIOMap.get(imageType).createImage(in, index);
	}
	
	public static byte[] writeImage(String imageType, BufferedImage image)throws IOException{
		return imageTypeIOMap.get(imageType).writeImage(image);
	}
	
	public static void writeImage(String imageType, BufferedImage image, OutputStream out)throws IOException{
		imageTypeIOMap.get(imageType).writeImage(image, out);
	}
	
	public static void writeImage(String imageType, BufferedImage image, File imageFile)throws IOException{
		imageTypeIOMap.get(imageType).writeImage(image, imageFile);
	}
	
	public static int getNumPages(String imageType, InputStream in)throws IOException{
		return imageTypeIOMap.get(imageType).getNumPages(in);
	}
	
	public static int getNumPages(String imageType, File file)throws IOException{
		return imageTypeIOMap.get(imageType).getNumPages(file);
	}
	
	public static HashMap<String,?> getMetadataMap(String imageType, InputStream in)throws IOException{
		return imageTypeIOMap.get(imageType).getMetadataMap(in);
	}

	
	public static interface ImageTypeIO{
		public BufferedImage createImage(InputStream in)throws IOException;
		public BufferedImage createImage(InputStream in, int index)throws IOException;
		public BufferedImage createImage(byte[] bytes, int index)throws IOException;
		public BufferedImage createImage(File file, int index)throws IOException;
		public byte[] writeImage(BufferedImage bufferedImage) throws IOException;
		public void writeImage(BufferedImage bufferedImage, OutputStream out) throws IOException;
		public void writeImage(BufferedImage bufferedImage, File imageFile) throws IOException;
		public int getNumPages(InputStream in)throws IOException;
		public int getNumPages(File file)throws IOException;
		public HashMap<String,?> getMetadataMap(InputStream in)throws IOException;
	}

	public static abstract class AbstractImageTypeIO implements ImageTypeIO{
		public BufferedImage createImage(byte[] bytes, int index)throws IOException{
			return createImage(new ByteArrayInputStream(bytes), index);
		}
		
		public BufferedImage createImage(File file, int index) throws IOException {
			InputStream in = null;
			try{
				return createImage(in = new BufferedInputStream(new FileInputStream(file)), index);
			}finally{
				if(in != null){
					in.close();
				}
			}
		}
		
		public int getNumPages(File file)throws IOException{
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			try{
				return getNumPages(in);
			}finally{
				if(in != null){
					in.close();
				}
			}
		}
		
		public HashMap<String,?> getMetadataMap(InputStream in)throws IOException{
			throw new IOException("not supported");	
		}
		
		public byte[] writeImage(BufferedImage image) throws IOException{
			throw new IOException("not supported");	
		}
		
		public void writeImage(BufferedImage image, OutputStream out) throws IOException{
			throw new IOException("not supported");	
		}
		
		public void writeImage(BufferedImage image, File imageFile) throws IOException{
			throw new IOException("not supported");	//override
		}
		
		public int getNumPages(InputStream in)throws IOException{
			throw new IOException("not supported");
		}
	}

	public static class DefaultImageTypeIO extends AbstractImageTypeIO{
		
		public BufferedImage createImage(InputStream in)throws IOException{
			throw new IOException("not supported");
		}
		
		public BufferedImage createImage(InputStream in, int index)throws IOException{
			throw new IOException("not supported");
		}

		@Override
		public BufferedImage createImage(File file, int index)throws IOException{
			throw new IOException("not supported");
		}
						
	}

	public static class PDFImageTypeIO extends AbstractImageTypeIO implements ImageTypeIO{
		
		public static final boolean IGNORE_PDF_FILES_CREATED_BY_SQS = true; 

		public BufferedImage createImage(InputStream in)throws IOException{
			return createImage(in, 0);
		}

		public BufferedImage createImage(InputStream in, int index)throws IOException{
		     PDFParser pdfParser = new PDFParser(new RandomAccessBuffer(in));
		     pdfParser.parse();
		     PDDocument pdf = pdfParser.getPDDocument();
		     PDPageTree pages = pdf.getDocumentCatalog().getPages();
		     PDPage page = pages.get(index);
		     PDResources resources = page.getResources();
			 for(COSName name: resources.getXObjectNames()){
				 if(resources.isImageXObject(name)){
					 PDXObject obj = resources.getXObject(name);
					 InputStream stream = obj.getStream().createInputStream();
					 return ImageIO.read(stream);
				 }
			 }
		     throw new IOException();
		}
		
		public HashMap<String,?> getMetadataMap(InputStream in)throws IOException{
			PdfReader reader = null;
			try{
				reader = new PdfReader(in);
				HashMap<String,String> map = reader.getInfo();
				map.put("NumberOfPages", String.valueOf(reader.getNumberOfPages()));
				return map;
			} finally{
				try{
					reader.close();
				}catch(Exception ignore){
				}
			}
		}
		
		public int getNumPages(InputStream in)throws IOException{
			int numAddingImageFiles = 0;
			PdfReader reader = null;
			try{
				reader = new PdfReader(in);
				Object producer = reader.getInfo().get("Producer");
				numAddingImageFiles = reader.getNumberOfPages();
				return numAddingImageFiles;
			} finally{
				try{
					reader.close();
				}catch(Exception ignore){
				}
			}
		}

	}

	public static class BatikTIFFImageTypeIO extends AbstractImageTypeIO implements ImageTypeIO{
		
		public BatikTIFFImageTypeIO() {
		}

		public BufferedImage createImage(InputStream in)throws IOException{
			return createImage(in, 0);
		}
		
		public BufferedImage createImage(InputStream in, int index)throws IOException{
			FileCacheSeekableStream seekableStream = null;
			try {
				seekableStream = new FileCacheSeekableStream(in);
				TIFFImageDecoder decoder = new TIFFImageDecoder(seekableStream, new TIFFDecodeParam());
				RenderedImage renderedImage = null;
				if (0 < index) {
					renderedImage = decoder.decodeAsRenderedImage(index);
				} else {
					renderedImage = decoder.decodeAsRenderedImage();
				}
				BufferedImage image = new BufferedImage(renderedImage.getColorModel(), 
						renderedImage.getData().createCompatibleWritableRaster(), true,
						new java.util.Hashtable<String, Object>());
				image.setData(renderedImage.getData());
				return image;
			} finally {
				try {
					seekableStream.close();
				} catch (Exception ignore) {
				}
			}
		}
		
		@Override
		public int getNumPages(InputStream in)throws IOException{
			FileCacheSeekableStream seekableStream = null;
			try {
				seekableStream = new FileCacheSeekableStream(in);
				TIFFImageDecoder decoder = new TIFFImageDecoder(seekableStream, new TIFFDecodeParam());
				return decoder.getNumPages();
			} finally {
				try {
					seekableStream.close();
				} catch (Exception ignore) {
				}
			}
		}
		
		@Override
		public void writeImage(BufferedImage bufferedImage, File imageFile) throws IOException {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(imageFile));
			try {
				
				TIFFEncodeParam param = new TIFFEncodeParam();  
				int pixelSize = bufferedImage.getColorModel().getPixelSize();
				if(1 == pixelSize){
					param.setCompression(CompressionValue.PACKBITS);
				}else if(8 == pixelSize){
					param.setCompression(CompressionValue.DEFLATE);
				}else if(24 == pixelSize){
					param.setCompression(CompressionValue.JPEG_TTN2);
				}

				//param.setWriteTiled(true);
				//param.setTileSize(64,64);
										
				TIFFImageEncoder encoder = new TIFFImageEncoder(out, param);
				encoder.encode(bufferedImage);
			}catch(Exception ex){
				ex.printStackTrace();
			} finally {
				out.flush();
				out.close();
			}
		}

	}

	public static class ImageIOImageTypeIO extends AbstractImageTypeIO implements ImageTypeIO{
		
		String imageType;
		
		public ImageIOImageTypeIO(String imageType){
			this.imageType = imageType;
		}
		
		public BufferedImage createImage(InputStream in, int index)throws IOException{
			if(0 == index){
				return createImage(in);
			}else{
				throw new IllegalArgumentException();
			}
		}
		
		public BufferedImage createImage(InputStream in)throws IOException{
			return ImageIO.read(in);
		}
		
		public int getNumPages(InputStream in)throws IOException{
			return 1;
		}

		@Override
		public void writeImage(BufferedImage bufferedImage, File imageFile) throws IOException {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(imageFile));
			try {
				ImageIO.write(bufferedImage, this.imageType, out);
			} finally {
				out.close();
			}
		}

		@Override
		public byte[] writeImage(BufferedImage bufferedImage) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
			try {
				ImageIO.write(bufferedImage, this.imageType, out);
			} finally {
				out.close();
			}
			return out.toByteArray();
		}
		
		@Override
		public void writeImage(BufferedImage bufferedImage, OutputStream out) throws IOException {
			ImageIO.write(bufferedImage, this.imageType, out);
		}

	}
}
