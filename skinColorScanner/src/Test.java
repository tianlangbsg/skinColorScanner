import java.awt.BorderLayout;  
import java.awt.Component;  
import javax.media.CaptureDeviceInfo;  
import javax.media.Manager;  
import javax.media.MediaLocator;  
import javax.media.Player;  
import javax.media.cdm.CaptureDeviceManager;  
import javax.swing.JFrame;  
import javax.swing.JPanel;  
public class Test extends JFrame{  
    public  static Player player = null;  
    private CaptureDeviceInfo deviceInfo = null;  
    private MediaLocator mediaLocator = null;  
    private Component component = null;  
    private JPanel vedioPanel = null;  
      
    String   str1   =   "vfw:Logitech   USB   Video   Camera:0";      //获取USB摄像头的字符串  
    String   str2   =   "vfw:Microsoft WDM Image Capture (Win32):0";    //获取本地摄像头的字符串  
    // Creates a new instance of CameraTest   
    public Test() {  
        init();  
    }  
    public void init(){  
        deviceInfo = CaptureDeviceManager.getDevice(str2);  //根据字符串获取采集设备（摄像头）的引用  
     //   System.out.println(deviceInfo);         //显示采集设备(摄像头)的信息  
     //   System.out.println(deviceInfo.getName());     //显示采集设备（摄像头）的设备名称  
        mediaLocator = deviceInfo.getLocator(); //获取采集设备的定位器的引用，需要根据此引用来创建视频播放器  
          
        try{  
            player = Manager.createRealizedPlayer(mediaLocator);// 利用mediaLocator 获取一个player  
            component = player.getVisualComponent();  
            if (component != null){  
                vedioPanel = new JPanel();  
                vedioPanel.add(component, BorderLayout.NORTH);  
                this.add(vedioPanel);  
                this.pack();    // 自动分配窗体大小  
                this.setResizable(false);  
                this.setDefaultCloseOperation(EXIT_ON_CLOSE);  
                this.setVisible(true);  
                player.start();  
            }  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
      
    public static void main(String[] args) {  
        new Test();  
    }  
}  
