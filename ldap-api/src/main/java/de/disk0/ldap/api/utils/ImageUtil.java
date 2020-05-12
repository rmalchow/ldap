package de.disk0.ldap.api.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

public class ImageUtil {
	
	private static byte[] scale(InputStream in, int maxWidth, int maxHeight, String outputFormat) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(in);
		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();
		
		double xf = (double)w / (double)maxWidth;
		double yf = (double)h / (double)maxHeight;
		
		double f = Math.max(xf, yf);
		
		w = (int)Math.floor(w / f);
		h = (int)Math.floor(h / f);
		
		Image scaled = bufferedImage.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
		
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = bi.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		g.drawImage(scaled, 0, 0, null);
		
		// write the bufferedImage back to outputFile
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, outputFormat, baos);
		baos.flush();
	
		return baos.toByteArray();
	}
	
	public static byte[] toPng(InputStream in, int maxWidth, int maxHeight) throws IOException {
		return scale(in, maxWidth, maxHeight, "png");
	}
	
	public static byte[] toJpg(InputStream in, int maxWidth, int maxHeight) throws IOException {
		return scale(in, maxWidth, maxHeight, "jpg");
	}
	
	public static byte[] generateIdenticons(String text, int image_width, int image_height) throws IOException, NoSuchAlgorithmException{
	    int width = 5, height = 5;
	
	    byte[] hash = text.getBytes(StandardCharsets.UTF_8);
	
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(hash);
        hash = md.digest();
        
	    BufferedImage identicon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    WritableRaster raster = identicon.getRaster();
	
	    int [] background = new int [] {255,255,255, 0};
	    int [] foreground = new int [] { (int)hash[0] & 0xFF, (int)hash[1] & 0xFF, (int)hash[2] & 0xFF, 255};
	    
	    for(int x=0 ; x < width ; x++) {
	        //Enforce horizontal symmetry
	        int i = x < 3 ? x : 4 - x;
	        for(int y=0 ; y < height; y++) {
	            int [] pixelColor;
	            //toggle pixels based on bit being on/off
	            if((hash[i] >> y & 1) == 1)
	                pixelColor = foreground;
	            else
	                pixelColor = background;
	            raster.setPixel(x, y, pixelColor);
	        }
	    }
	
	    BufferedImage finalImage = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_ARGB);
	
	    //Scale image to the size you want
	    AffineTransform at = new AffineTransform();
	    at.scale(image_width / width, image_height / height);
	    AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    finalImage = op.filter(identicon, finalImage);
	
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    
	    ImageIO.write(finalImage, "PNG", baos);
	    
	    baos.flush();
	    return baos.toByteArray();
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		ImageUtil.generateIdenticons("abc", 60, 60);
		ImageUtil.generateIdenticons("efg", 60, 60);
		ImageUtil.generateIdenticons("hij", 60, 60);
	}
	
	
}
