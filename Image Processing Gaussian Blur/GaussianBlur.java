/*
 * Program that applies Gaussian blurring to an image.
 * Here, instead of using box blur (2D kernel), we use the improved algorithm where we make 2 passes using a 1D kernel.  This brings the runtime
 * down to O(width*height*radius).
 */

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class GaussianBlur {
	
	BufferedImage img = null;
    File f = null;
    
    // Read in image.
    public void readImage(String file_name){
    	try{
    		f = new File(file_name);
    		img = ImageIO.read(f);
    	}catch(IOException e){
    		System.out.println(e);
    	}
    }
    
    // Save new blurred image.
    public void saveImage(BufferedImage image, String name){
    	try{
    	      File file = new File(name);
    	      ImageIO.write(image, "jpg", file);
    	      System.out.println("New image saved.");
    	    }catch(IOException e){
    	      System.out.println(e);
    	    }
    }
    
    // Create a Gaussian filter kernel.
    public double[] getKernel(int r){
    	double[] kernel = new double[r*2+1];
    	double rootPiR = 1 / (Math.sqrt(2*Math.PI)*r);
    	int c = -1*r;
    	double sum = 0;
    	for(int i = 0 ; i < kernel.length ; i++){
    		double x = c*c;
    		kernel[i] = rootPiR * Math.exp(-x / (2*r*r));
    		c++;
    		sum += kernel[i];
    	}
    	for(int i = 0 ; i < kernel.length ; i++){
    		kernel[i] /= sum;
    	}
    	
    	return kernel;
    }
    
    // Perform the actual blurring of an image.
    public BufferedImage blurImage(int r){
    	
    	int width = img.getWidth();
    	int height = img.getHeight();
    	
    	// Temp image (can also use two 1D arrays rather than two 2D matricies)
    	BufferedImage blurred = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	
    	// Get the kernel to be used for blurring.
    	double[] kernel = getKernel(r);
    	
    	// Perform the first (horizontal) pass on the original image.
    	for(int i = 0 ; i < height ; i++){
    		for(int j = 0 ; j < width ; j++){
    			int res = 0;
    			double resR = 0, resG = 0, resB = 0;

    			for(int k = 0 ; k < kernel.length ; k++){
    				int w = Math.min( width-1, Math.max( 0, j+(k-(kernel.length / 2)) ) );
    				
    				// Process each individual color channel.
    				int color = img.getRGB(w, i);
    				int red = (color>>16) & 0xFF;
    				int green = (color>>8) & 0xFF;
    				int blue = color & 0xFF;
    				resR += kernel[k] * (double)red;
    				resG += kernel[k] * (double)green;
    				resB += kernel[k] * (double)blue;
    			}
    			// Write result to intermediate image.
    			int alpha = (img.getRGB(j, i) >> 24) & 0xFF;
    			res = (alpha << 24) | ((int)resR << 16) | ((int)resG << 8) | ((int)resB);
    			blurred.setRGB(j, i, res);
    		}
    	}
    	
    	// Perform the second (vertical) pass on the intermediate image.
    	for(int i = 0 ; i < width ; i++){
    		for(int j = 0 ; j < height ; j++){
    			int res = 0;
    			double resR = 0, resG = 0, resB = 0;
    			
    			for(int k = 0 ; k < kernel.length ; k++){
    				int w = Math.min( height-1, Math.max( 0, j+(k-(kernel.length / 2)) ) );
    				
    				// Process each individual color channel.
    				int color = blurred.getRGB(i, w);
    				int red = (color >> 16) & 0xFF;
    				int green = (color >> 8) & 0xFF;
    				int blue = (color) & 0xFF;
    				resR += kernel[k] * (double)red;
    				resG += kernel[k] * (double)green;
    				resB += kernel[k] * (double)blue;
    			}
    			// Write the final result to the original image.
				int alpha = (blurred.getRGB(i, j) >> 24) & 0xFF;
				res = (alpha << 24) | ((int)resR << 16) | ((int)resG << 8) | ((int)resB);
    			img.setRGB(i, j, res);
    		}
    	}
    	
    	return img;
    }
	
    
    public static void main(String args[])throws IOException{
    	
    	// Read in image, perform blurring, save result.
    	GaussianBlur pic = new GaussianBlur();
    	pic.readImage("Before Image.jpg");
    	BufferedImage res = pic.blurImage(15);
    	pic.saveImage(res, "After Image.jpg");
	
	}

}
