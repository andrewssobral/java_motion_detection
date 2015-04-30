package javaopencv;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public final class Main
{
  static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
  
  private JFrame window;
  private JButton startButton, stopButton;
  private ImagePanel image;

  public Main()
  {
    buildGUI();
  }

  private void buildGUI()
  {
    window = new JFrame("Test Webcam Panel");

    startButton = new JButton("Start");
    stopButton = new JButton("Stop");

    window.add(startButton, BorderLayout.WEST);
    window.add(stopButton, BorderLayout.EAST);

    image = new ImagePanel(new ImageIcon("figs/320x240.gif").getImage());
    window.add(image, BorderLayout.CENTER);

    window.setSize(449, 268); //window.pack();
    window.setLocationRelativeTo(null);
    window.setVisible(true);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);

    startButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        start();
      }
    });

    stopButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        stop();
      }
    });
  }
  
  private Boolean begin = false;
  private VideoCapture video = null;
  private CaptureThread thread = null;
  
  private void start()
  {
    System.out.println("You clicked the start button!");
    
    if(begin == false)
    {
      video = new VideoCapture(0);

      if(video.isOpened())
      {
        thread = new CaptureThread();
        thread.start();
        begin = true;
      }
    }
  }
  
  private MatOfByte matOfByte = new MatOfByte();
  private BufferedImage bufImage = null;
  private InputStream in;
  private Mat frameaux = new Mat();
  private Mat frame = new Mat(240,320,CvType.CV_8UC3);
  
  class CaptureThread extends Thread
  {
    @Override
    public void run()
    {
      if(video.isOpened())
      {
        while(begin == true)
        {
          //video.read(frameaux);
          video.retrieve(frameaux);
          Imgproc.resize(frameaux, frame, frame.size());
          
          Highgui.imencode(".jpg", frame, matOfByte);
          byte[] byteArray = matOfByte.toArray();

          try
          {
            in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
          }
          catch(Exception ex)
          {
            ex.printStackTrace();
          }

          //image.updateImage(new ImageIcon("figs/lena.png").getImage());
          image.updateImage(bufImage);
          
          try{ Thread.sleep(5); } catch(Exception ex){}
        }
      }
    }
  }
  
  private void stop()
  {
    System.out.println("You clicked the stop button!");
    begin = false;
    try{ Thread.sleep(1000); } catch(Exception ex){}
    video.release();
  }

  public static void main(String[] args)
  {
    new Main();
  }
}
