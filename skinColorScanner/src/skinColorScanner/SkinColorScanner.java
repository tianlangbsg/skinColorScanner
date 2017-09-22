package skinColorScanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.management.RuntimeErrorException;
import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.cdm.CaptureDeviceManager;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SkinColorScanner extends JFrame {

	public Player player;
	private CaptureDeviceInfo deviceInfo;
	private MediaLocator mediaLocater;
	public long time;
	public int[][] binArray = null;
	public int[][] source = null;

	JFrame jf = new JFrame();
	JInternalFrame inner = new JInternalFrame("11");
	JButton getCapBt = new JButton("抓取并进行识别");
	JLabel label = new JLabel("识别结果");// 标签

	public static void main(String[] args) {
		new SkinColorScanner();
	}

	public SkinColorScanner() {

		jf.setTitle("皮肤颜色识别");
		jf.setBounds(50, 20, 1280, 700);
		jf.setDefaultCloseOperation(3);
		jf.setLayout(new FlowLayout());
		// jf.add(getCapBt,BorderLayout.SOUTH);
		// jf.add(label,BorderLayout.NORTH);
		jf.add(getCapBt);
		jf.add(label);
		String str2 = "vfw:Microsoft WDM Image Capture (Win32):0";
		deviceInfo = CaptureDeviceManager.getDevice(str2);
		mediaLocater = deviceInfo.getLocator();
		try {
			player = Manager.createRealizedPlayer(mediaLocater);
			player.start();
			Component comp = null;
			if ((comp = player.getVisualComponent()) != null) {
				// inner.add(comp, BorderLayout.NORTH);
				jf.add(comp, BorderLayout.NORTH);
				// inner.add(comp);
				// jf.add(inner);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		getCapBt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameGrabbingControl fgc = (FrameGrabbingControl) player
						.getControl("javax.media.control.FrameGrabbingControl");
				Buffer buffer = fgc.grabFrame();
				BufferToImage bufferToImage = new BufferToImage(
						(VideoFormat) buffer.getFormat());
				Image image = bufferToImage.createImage(buffer);
				try {
					saveImage(image, getTimeStr());// 设置照片名称

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		jf.setVisible(true);
	}

	private void saveImage(Image image, String path) throws IOException {

		BufferedImage bi = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		try {
			JPanel p4 = new JPanel();
			Graphics g = bi.createGraphics();
			g.drawImage(image, 0, 0, image.getWidth(null),
					image.getHeight(null), p4);
			// ImageIO.write(bi, "jpg", new File(path));

			time = System.currentTimeMillis();
			// 压缩图片
			BufferedImage buffImg = null;
			buffImg = new BufferedImage(320, 180, BufferedImage.TYPE_INT_RGB);
			buffImg.getGraphics().drawImage(
					bi.getScaledInstance(320, 180, Image.SCALE_SMOOTH), 0, 0,
					null);
			//保存图片
			try {
				ImageIO.write(buffImg, "jpg", new File("F:/pic/" + getTimeStr()
						+ "raw.jpg"));
				// ImageIO.write(bi, "jpg", new File(getTimeStr()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			// GaussianBlur2.blur(bi);
			GaussianBlur2.blur(buffImg);
//			int[][] result = Image_Utility.open(Image_Utility.imageToArray(buffImg), 1) ;
			//开闭运算
//			int[][] result = Image_Utility.imageToArray(buffImg) ;
//			System.out.println(result.toString());
//			buffImg = Image_Utility.arrayToGreyImage(result);
			
			//导入外部测试图片入口
			String imgPath = "F:/pic/"+"发红皮肤"+".jpg";
			buffImg = ImageIO.read(new FileInputStream(imgPath));
			
			calculate(buffImg);
			g.dispose();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getTimeStr() {
		Date currentTime = new Date(); // 时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");// 格式化时间
		String dateString = formatter.format(currentTime);
		// return "F:\\pic\\" + dateString + ".jpg"; // 转换成字符串
		return dateString; // 转换成字符串
	}

	public void calculate(BufferedImage bi) {
		double sum = 0;
		int pixel = 0;

		int width = bi.getWidth();
		int height = bi.getHeight();
		int s = width * height;
		double count = 0;
		double result = 0.2;
		binArray = new int[width][height];
		
		// time = System.currentTimeMillis();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = bi.getRGB(i, j);
				
				/*
				 * 应为使用getRGB(i,j)获取的该点的颜色值是ARGB，
				 * 而在实际应用中使用的是RGB，所以需要将ARGB转化成RGB， 即bufImg.getRGB(i, j) &
				 * 0xFFFFFF。
				 */
				int r = (rgb & 0xff0000) >> 16;
				int g = (rgb & 0xff00) >> 8;
				int b = (rgb & 0xff);
				int max = Math.max(r, Math.max(g, b));
				int min = Math.min(r, Math.min(g, b));
//				if (isSkinByYCrCb(r, g, b)) {
				if (isSkinByYCrCb(r, g, b)) {
					//将数值写入到二维数组中
					if((r<220&&r>178&&g<160&&g-b<25)||(r>190&&(g+b)<365&&Math.abs(g-b)<20/*1<Math.abs(g-b)&&g-b<18*/)){
						bi.setRGB(i, j, 0xff0000);
						binArray[i][j] = 2;//2表示红色皮肤区域
					}else if(max-min<20&&max-min>8&&r>g&&400<r+g+b&&r+g+b<630){
						bi.setRGB(i, j, 0x0000ff);
						binArray[i][j] = 3;//3表示青色皮肤区域
					}
					else{
						bi.setRGB(i, j, 0xffffff);
						binArray[i][j] = 1;//1表示白色皮肤区域
					}
					count++;
				} else {
					bi.setRGB(i, j, 0x000000);
					binArray[i][j] = 0;//0表示黑色
				}
			}
		}
		//腐蚀二维数组图像
		//腐蚀两次
		source = correde(binArray);
		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);
//		source = correde(source);

		source = dilate(source);
		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		source = dilate(source);
//		//开运算，先腐蚀再膨胀
//		source = correde(binArray);
//		source = dilate(source);
//		//闭运算，先膨胀再腐蚀
//		source = dilate(source);
//		source = correde(source);
//		//开运算
//		source = correde(binArray);
//		source = dilate(source);
//		//闭运算，先膨胀再腐蚀
//		source = dilate(source);
//		source = correde(source);

		//二维数组转换为图像并保存
		BufferedImage bufimg = arrayToImage(source);
//		BufferedImage bufimg = arrayToImage(binArray);
		try {
			ImageIO.write(bufimg, "jpg", new File("F:/pic/wwww.jpg"));
			// ImageIO.write(bi, "jpg", new File(getTimeStr()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		time = System.currentTimeMillis() - time;
		System.out.println(time);// 输出消耗的时间，精确到毫秒
		// System.out.println(count+"___"+(1-result));
		result = count / 57600;
		DecimalFormat df = new DecimalFormat("00.00 ");
		String rate = df.format((result) * 100);
		System.out.println("通过像素数：" + count + "_皮肤区域占比：" + rate + "%");
		if (result > 0.37) {
			label.setText("识别通过！＿耗时" + time + "毫秒" + "_皮肤区域占比：" + rate + "%");
			label.setBackground(new Color(0x00FF00));
		} else {
			label.setText("识别失败！＿耗时" + time + "毫秒" + "_皮肤区域占比：" + rate + "%");
			label.setBackground(new Color(0xFF0000));
		}

		try {
			File file = new File("F:/pic/" + getTimeStr()+ "done.jpg");
			ShowImage si = new ShowImage();
			ImageIO.write(bi, "jpg",file );
			si.openFile(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		validate();
	}

	/**
	 * 皮肤颜色判断核心算法
	 * 
	 * @param tr
	 * @param tg
	 * @param tb
	 * @return
	 */
	public static boolean isSkinByRGB(int R, int G, int B) {
		int max = Math.max(R, Math.max(G, B));
		int min = Math.min(R, Math.min(G, B));
		int rg = Math.abs(R - G);
		if (R > 55 && G > 20 && B > 20 && G > 5 && (max - min) > 5
				&& (R + G + B) > 120 && (R + G + B) < 750 && (R - B) < 100
				&& (G - B) < 70 && R - B > -10 && B < 200) {
			return true;
		} else {
			return false;
		}

	}
	public static boolean isSkinByYCrCb(int r, int g, int b) {
	    double y = 0.257*r+0.564*g+0.098*b+16;  
	    double Cb =-0.148*r-0.291*g+0.439*b+128;  
	    double Cr = 0.439*r-0.368*g-0.071*b+128;  
	    
	    if (137<Cr&&Cr<171&&99<Cb&&Cb<128) {
//	    if (128<Cr&&Cr<190&&99<Cb&&Cb<130) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * 腐蚀算法
	 * @param source
	 * @return
	 */
	private static int[][] correde(int[][] source) {
		int height = source[0].length;
		int width = source.length;

		int[][] result = new int[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// /边缘不进行操作，边缘内才操作
				if (i > 0 && j > 0 && i < width - 1 && j < height - 1) {
					//当元素不为黑色时
					if(!(source[i][j]==0)){
						//与结构元素进行比较
						if((source[i+1][j]!=0)&&(source[i][j+1]!=0)){
							result[i][j] = source[i][j];
						}
					}

				} else {
					// /直接赋值
					//result[i][j] = source[i][j];

				}

			}
		}

		return result;
	}
	
	/**
	 * 膨胀算法
	 * @param source
	 * @return
	 */
	private static int[][] dilate(int[][] source) {
		int height = source[0].length;
		int width = source.length;

		int[][] result = new int[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// /边缘不进行操作，边缘内才操作
				if (i > 0 && j > 0 && i < width - 1 && j < height - 1) {
					//当元素为黑色时
					if(source[i][j]==0){
						//与结构元素进行比较
						if((source[i+1][j]!=0||(source[i][j+1]!=0))){
							
							if (source[i + 1][j + 1]==1) {
								result[i][j] = 1;
							}else if(source[i + 1][j + 1]==2){
								result[i][j] = 2;
							}
							else if(source[i + 1][j + 1]==3){
								result[i][j] = 3;
							}
						}
					} else {
						//直接赋值
						result[i][j] = source[i][j];
					}


				}
			}
		}// /end of outer for clause

		return result;
	}
	
	
	/**
	 * 数组转为灰度图像
	 * 
	 * @param sourceArray
	 * @return
	 */
	public static BufferedImage arrayToImage(int[][] sourceArray) {
		int height = sourceArray[0].length;
		int width = sourceArray.length;
		BufferedImage targetImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int greyRGB = sourceArray[i][j];
				if(sourceArray[i][j]==0){
					greyRGB = 0x000000;
				}else if(sourceArray[i][j]==1){
					greyRGB = 0xffffff;
				}else if(sourceArray[i][j]==2){
					greyRGB = 0xff0000;
				}else{
					greyRGB = 0x0000ff;
				}
				targetImage.setRGB(i, j, greyRGB);
			}
		}

		return targetImage;
	}
	
	public static int[] rgbToYcrCb(int tr, int tg, int tb) {  
	    double sum = tr + tg + tb;  
	    double r = ((double)tr)/sum;  
	    double g = ((double)tg)/sum;  
	    double b = ((double)tb)/sum;  
//	    double y = 65.481 * r + 128.553 * g + 24.966 * b + 16.0d;  
//	    double Cr = -37.7745 * r - 74.1592 * g + 111.9337 * b + 128.0d;  
//	    double Cb = 111.9581 * r -93.7509 * g -18.2072 * b + 128.0d;  
	    double y = 0.257*r+0.564*g+0.098*b+16;  
	    double Cb =-0.148*r-0.291*g+0.439*b+128;  
	    double Cr = 0.439*r-0.368*g-0.071*b+128;  
	    return new int[]{(int)y, (int)Cr, (int)Cb};  
	}  
	
}
