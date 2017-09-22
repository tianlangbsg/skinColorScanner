package skinColorScanner;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class sss {
	static SkinColorScanner scs = new  SkinColorScanner();
	public static void main(String[] args) throws FileNotFoundException, IOException {

		//for (int i = 1; i < 5; i++) {
			String imgPath = "F:/pic/"+"·¢ºìÆ¤·ô"+".jpg";
			read(imgPath);
		//}
	}
	public static void read(String imgPath) throws FileNotFoundException, IOException{
  
		BufferedImage image = ImageIO.read(new FileInputStream(imgPath));
		GaussianBlur2.blur(image);
//		new SkinColorScanner().calculate(image);;
		//int[][] result = Image_Utility.imageToArray(image) ;
//		for(int[] a:result){
//			for(int b:a){
//				System.out.print(b+",");
//			}
//			System.out.println();
//		}
//		image = Image_Utility.arrayToGreyImage(result);
//		scs.calculate(image);
	}
}
