
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class test2 extends JFrame {
	/**
	 * 可以应用JMF从摄像头和麦克风中采集数据，采集后的数据可以被处理、保存、渲染为以后应用。 捕获媒体数据需要做以下工作。
	 * 1.定位所需要用的捕获设备，可以通过查询CaptureDeviceManager来定位
	 * 2.获取这个捕获设备的信息CaptureDeviceInfo对象
	 * 3.从CaptureDeviceInfo对象中获取捕获设备的位置Medialocator 4.利用MediaLocator创建DataSource
	 * 5.使用DataSource创建Player或是Processor。6.然后启动Player就开始了媒体的捕获。
	 */
	private JPanel contentPane;
	private JPanel avPane;
	private JButton startBtn, stopBtn;
	private JPanel p4;
	private CaptureDeviceInfo device;
	private Player player;
	Component comV;
	// 摄像头驱动名字
	String deviceName = "vfw:Microsoft WDM Image Capture (Win32):0";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test2 frame = new test2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public test2() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setOpaque(false);
		setContentPane(contentPane);

		startBtn = new JButton("start");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// jbInit();
					start();

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}

		});
		startBtn.setBounds(159, 529, 63, 23);
		stopBtn = new JButton("stop");
		stopBtn.setBounds(368, 529, 57, 23);
		contentPane.setLayout(null);

		contentPane.add(startBtn);
		contentPane.add(stopBtn);

	}

	/**
	 * 开启视频监控
	 * 
	 * @throws IOException
	 * @throws CannotRealizeException
	 * @throws NoPlayerException
	 * @throws InterruptedException
	 */
	public void start() throws NoPlayerException, CannotRealizeException,
			IOException, InterruptedException {

		System.out.println("开始监控！");
		p4 = new JPanel();
		p4.setLayout(new BorderLayout());
		p4.setOpaque(false);
		// 获取视频设备
		 device = CaptureDeviceManager.getDevice(deviceName);

		String locatorString = "vfw://0";
		MediaLocator locator = new MediaLocator(locatorString);
		player = Manager.createRealizedPlayer(locator);
		Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, new Boolean(true));
		player.start();

		comV = player.getVisualComponent();
		if (comV != null) {
			p4.add(comV);
		}
		p4.setVisible(true);
		contentPane.add(p4, "Center");
		// MediaTracker类跟踪一个Image对象的装载，完成图像加载
		// MediaTracker 类是一个跟踪多种媒体对象状态的实用工具类。媒体对象可以包括音频剪辑和图像，但目前仅支持图像。

		MediaTracker mt = new MediaTracker(this.p4);
		Image image = null;
		mt.addImage(image, 0); // 装载图像

		mt.waitForID(0); // 等待图像全部装载
		int w =200;
		int h = 300;
		
		BufferedImage buffImg = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);

		Graphics g = buffImg.createGraphics();

		g.drawImage(image, 0, 0, w, h, this.p4);
		ImageIO.write(buffImg, "PNG", new File("F:/pictest/JMF.png"));
		g.dispose();
	}

	
}