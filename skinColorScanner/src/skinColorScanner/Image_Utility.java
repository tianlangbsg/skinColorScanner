package skinColorScanner;

import java.awt.image.BufferedImage;

/**
 * 图像辅助类
 * 
 * @author Administrator
 * 
 */
public class Image_Utility {

	// /结构元素
	private static int sData[] = { 0, 0, 0, 0, 1, 1, 0, 1, 0 };

	/**
	 * 图像的开运算： 先腐蚀再膨胀
	 * 
	 * @param sourceImage
	 *            此处处理灰度图像或者二值图像
	 * @param shreshold
	 *            :阈值————当膨胀结果小于阈值时，仍然设置图像位置的值为0；而进行腐蚀操作时，
	 *            当灰度值大于等于阈值（小于阈值）时并且结构元素为1（0）时，才认为对应位置匹配上； 如果为二值图像，则应该传入1。
	 * @return
	 */
	public static int[][] open(int[][] source, int threshold) {

		int width = source[0].length;
		int height = source.length;

		int[][] result = new int[height][width];
		// /先腐蚀运算
		//result = correde(source, threshold);
		//result = correde(source, threshold);
		// /后膨胀运算
		//result = dilate(result, threshold);
		//result = dilate(result, threshold);
		/*
		 * for(int j=0;j<height;j++){ for(int i=0;i<width;i++){
		 * System.out.print(result[j][i]+","); } System.out.println(); }
		 */

		return result;
	}

	/**
	 * 腐蚀运算
	 * 
	 * @param source
	 * @param shreshold
	 *            当灰度值大于阈值（小于阈值）时并且结构元素为1（0）时，才认为对应位置匹配上；
	 * @return
	 */
	private static int[][] correde(int[][] source, int threshold) {
		int width = source[0].length;
		int height = source.length;

		int[][] result = new int[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// /边缘不进行操作，边缘内才操作
				if (i > 0 && j > 0 && i < height - 1 && j < width - 1) {
					int max = 0;

					// /对结构元素进行遍历
					for (int k = 0; k < sData.length; k++) {
						int x = k / 3;// /商表示x偏移量
						int y = k % 3;// /余数表示y偏移量

						if (sData[k] != 0) {
							// /不为0时，必须全部大于阈值，否则就设置为0并结束遍历
							if (source[i - 1 + x][j - 1 + y] >= threshold) {
								if (source[i - 1 + x][j - 1 + y] > max) {
//									max = source[i - 1 + x][j - 1 + y];
									max = 1;
								}
							} else {
								// //与结构元素不匹配,赋值0,结束遍历
								max = 0;
								break;
							}
						}
					}

					// //此处可以设置阈值，当max小于阈值的时候就赋为0
					result[i][j] = max;

				} else {
					// /直接赋值
					result[i][j] = source[i][j];

				}// /end of the most out if-else clause .

			}
		}// /end of outer for clause

		return result;
	}

	/**
	 * 膨胀运算
	 * 
	 * @param source
	 * @param threshold
	 *            当与运算结果值小于阈值时，图像点的值仍然设为0
	 * @return
	 */
	private static int[][] dilate(int[][] source, int threshold) {
		int width = source[0].length;
		int height = source.length;

		int[][] result = new int[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// /边缘不进行操作
				if (i > 0 && j > 0 && i < height - 1 && j < width - 1) {
					int max = 0;

					// /对结构元素进行遍历
					for (int k = 0; k < sData.length; k++) {
						int x = k / 3;// /商表示x偏移量
						int y = k % 3;// /余数表示y偏移量

						if (sData[k] != 0) {
							// /当结构元素中不为0时,取出图像中对应各项的最大值赋给图像当前位置作为灰度值
							if (source[i - 1 + x][j - 1 + y] > max) {
//								max = source[i - 1 + x][j - 1 + y];
								max = 1;
							}
						}
					}

					// //此处可以设置阈值，当max小于阈值的时候就赋为0
					if (max < threshold) {
						result[i][j] = 0;
					} else {
						result[i][j] = max;
					}
					// result[i][j]=max;

				} else {
					// /直接赋值
					result[i][j] = source[i][j];
				}

			}
		}

		return result;
	}

	/**
	 * 灰度图像提取数组
	 * 
	 * @param image
	 * @return int[][]数组
	 */
	public static int[][] imageToArray(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();

		int[][] result = new int[height][width];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int rgb = image.getRGB(i, j);
//				int grey = (rgb >> 16) & 0xFF;
//				// System.out.println(grey);
				if(rgb==0x000000){
					result[j][i] = 0;
				}else{
					result[j][i] = 1;
				}
			}
		}
		return result;
	}

	/**
	 * 数组转为灰度图像
	 * 
	 * @param sourceArray
	 * @return
	 */
	public static BufferedImage arrayToGreyImage(int[][] sourceArray) {
		int width = sourceArray[0].length;
		int height = sourceArray.length;
		BufferedImage targetImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int greyRGB = sourceArray[j][i];
				int r = (greyRGB & 0xff0000) >> 16;
				int g = (greyRGB & 0xff00) >> 8;
				int b = (greyRGB & 0xff);
				if(sourceArray[j][i]==0){
					greyRGB = 0x000000;
				}else if(sourceArray[j][i]==1){
					greyRGB = 0xffffff;
				}else{
					greyRGB = 0xff0000;
				}
				targetImage.setRGB(i, j, greyRGB);
			}
		}

		return targetImage;
	}

}