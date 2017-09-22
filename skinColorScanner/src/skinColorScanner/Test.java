package skinColorScanner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String imgPath = "F:/pic/"+"淤青皮肤2"+".jpg";
		BufferedImage bi = ImageIO.read(new FileInputStream(imgPath));
		GaussianBlur2.blur(bi);
		int width = bi.getWidth();
		int height = bi.getHeight();
		int s = width * height;
		double count = 0;
		double result = 0.2;
		int[][] binArray = new int[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = bi.getRGB(i, j);
				int r = (rgb & 0xff0000) >> 16;
				int g = (rgb & 0xff00) >> 8;
				int b = (rgb & 0xff);
				int max = Math.max(r, Math.max(g, b));
				int min = Math.min(r, Math.min(g, b));
			    double y = 0.257*r+0.564*g+0.098*b+16;  
			    double Cb =-0.148*r-0.291*g+0.439*b+128;  
			    double Cr = 0.439*r-0.368*g-0.071*b+128;  
				if (r > 100 && g > 20 && b > 20 && g > 5 && (max - min) > 10
						&& (r + g + b) > 120 && (r + g + b) < 750 && (r - b) < 130
						&& (g - b) < 70 && r - b > -10 && b < 200){
//					if (137<Cr&&Cr<171&&99<Cb&&Cb<128){
					if((r<220&&r>178&&g<160&&g-b<25)||(r>190&&(g+b)<365&&-10<g-b&&g-b<18)){
						bi.setRGB(i, j, 0xff0000);
					}else if(max-min<28&&r>g){
						bi.setRGB(i, j, 0x0000ff);
					}
					else{
						bi.setRGB(i, j, 0xffffff);
					}
				} else {
					bi.setRGB(i, j, 0x000000);
				}
			}
		}

		

		try {
			File file = new File("F:/pic/淤青部位标记.jpg");
			ShowImage si = new ShowImage();
			ImageIO.write(bi, "jpg",file );
			si.openFile(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	}


