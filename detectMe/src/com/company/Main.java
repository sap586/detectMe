package com.company;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Main {

    public static void main(String[] args) {
        CameraFrame cf = new CameraFrame();
    }

}

class CameraFrame extends JFrame {
    CameraFrame() {
        System.load("C:\\opencv\\build\\java\\x64\\opencv_java340.dll");
        CameraPanel cp = new CameraPanel();
        Thread t = new Thread(cp);
        t.start();
        add(cp);
        setDefaultCloseOperation(3);
        setTitle("Face Detection");
        setSize(640, 480);
        setVisible(true);
    }
}

class CameraPanel extends JPanel implements Runnable {
    BufferedImage i;
    JButton b1;
    VideoCapture vc;
    CascadeClassifier faceDetector, eyeDetector;
    MatOfRect mr;
    CameraPanel() {
        faceDetector = new CascadeClassifier(CameraPanel.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1));
        eyeDetector = new CascadeClassifier(CameraPanel.class.getResource("haarcascade_eye.xml").getPath().substring(1));
        mr = new MatOfRect();
        b1 = new JButton("Capture");
        add(b1);
    }

    @Override
    public void run() {
        vc = new VideoCapture(0);
        Mat webcam = new Mat();
        while(true) {
            vc.read(webcam);
            if (!webcam.empty()){
                matToBufferedImage(webcam);
                eyeDetector.detectMultiScale(webcam, mr);
                faceDetector.detectMultiScale(webcam, mr);
                repaint();
            }
        }
    }

    public void matToBufferedImage(Mat m) {
        int w = m.width();
        int h = m.height();
        int ch = m.channels();
        byte[] source = new byte[w * h * ch];
        m.get(0, 0, source);
        i = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        byte[] target = ((DataBufferByte) i.getRaster().getDataBuffer()).getData();
        System.arraycopy(source, 0, target, 0, source.length);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (i == null) {
            return;
        }
        g.drawImage(i, 10, 40, i.getWidth(), i.getHeight(), null);
        for (Rect rect : mr.toArray()) {
            g.drawRect(rect.x+10, rect.y+40, rect.width, rect.height);
        }
    }
}
