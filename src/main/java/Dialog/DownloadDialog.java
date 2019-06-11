package Dialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import Thread.Download;
import Main.JFrameMain;

public class DownloadDialog extends JDialog {

    private JFrameMain main;
    private JProgressBar jpb;
    private String downloadUri;
    int unitProgress = 0; //用于保存当前进度(1~100%)

    public DownloadDialog(JFrameMain owner, boolean modal, String DownloadUri) {
        super(owner,"更新进度:",modal);
        setUndecorated(true);
        downloadUri=DownloadUri;
        main=owner;
        init();
        setSize(400,30);
        setLocation(owner.getX()+100,owner.getY()+285);
        Download d=new Download(downloadUri,"./UKiller_new.jar");
        new Thread(d).start();
        new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpb.setValue(d.getPercent());
                jpb.setString("下载进度:"+d.getPercent()+"%");
            }
        }).start();
        setVisible(true);
    }

    private void init(){
        jpb=new JProgressBar(0,100);
        jpb.setStringPainted(true);
        jpb.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(jpb.getValue()==100){
                    File batFile=new File("reboot.bat");
                    try{
                        if(!batFile.exists())
                            batFile.createNewFile();
                        FileWriter fileWriter = new FileWriter(batFile,false);
                        fileWriter.write("@echo off\n" +
                                ":circle\n" +
                                "tasklist|find /i \"qq.exe\" || goto start\n" +
                                "taskkill /im qq.exe /f\n" +
                                "ping 127.1 -n 3 >nul 2>nul\n" +
                                "goto circle\n" +
                                "\n" +
                                ":start\n" +
                                "del /f /s /q UKiller.jar\n" +
                                "ren UKiller_new.jar Ukiller.jar\n"+
                                "del %0");
                        fileWriter.close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    new CompleteDialog(DownloadDialog.this,false,"1");
                }
            }
        });
        add(jpb);
    }
}
