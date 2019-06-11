import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WatchDisk {

    public static int count = 0;
    private Thread t1,t2;
    private boolean exit=false;
    private ResFileByWatchDisk rf;
    private JFrameMain main;

    public WatchDisk(JFrameMain Main){
        main=Main;
        File[] dir = File.listRoots();

    }
    public void start(){
        this.exit=false;
        rf = new ResFileByWatchDisk(File.listRoots(),main);
        t1 = new Thread(new ProducerByWatchDisk(rf));
        t2 = new Thread(new ConsumerByWatchDisk(rf));
        t1.start();
        t2.start();
    }

    public void stop(){
        this.exit=true;
    }

    public boolean isRun(){
        if(t1==null||t2==null)
            return false;
        return t1.isAlive()&&t2.isAlive();
    }

    //消费者
    class ConsumerByWatchDisk implements Runnable {

        private ResFileByWatchDisk rf = null;

        public ConsumerByWatchDisk(ResFileByWatchDisk rf) {
            this.rf = rf;
        }

        @Override
        public void run() {
            while (!exit) {
                rf.discoverDisk();
            }
        }

    }

    //生产者
    class ProducerByWatchDisk implements Runnable {

        private ResFileByWatchDisk rf = null;

        public ProducerByWatchDisk(ResFileByWatchDisk rf) {
            this.rf = rf;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (!exit) {
                rf.watchDisk();
            }
        }

    }
}

//资源
class ResFileByWatchDisk {
    private File[] oldDisk = null;
    //判断是否有设备插入的标记
    private boolean flag;
    JFrameMain main;
    private File[] dirs;
    public File[] newDisk=null;

    public ResFileByWatchDisk(File[] OldDisk,JFrameMain Main) {
        oldDisk = OldDisk;
        main=Main;
        flag=false;
    }

    //查找资源--生产者使用
    public synchronized void watchDisk() {
        //如果flag为true，说明检测出有设备插入，则等待；
        //如果flag为false，说明没有设备插入
        if (flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dirs = File.listRoots();
        //一但有设备插入，当前盘符数会大于系统一开始的盘符数
        if (dirs.length != oldDisk.length) {
            flag = true;
            notify();
        }
    }
    //消费资源--消费者使用
    public synchronized void discoverDisk() {
        if (!flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        main.refreshDisk();
        if (dirs.length > oldDisk.length) {
            List<File> oldList=Arrays.asList(oldDisk);
            List<File> newList=new ArrayList<File>();
            for (File t : dirs) {
                if (!oldList.contains(t)) {
                    newList.add(t);
                }
            }
            newDisk=newList.toArray(new File[newList.size()]);
            for (int i=0;i<newDisk.length;i++){
                try {
                    if((main.set>>2)%2==1){
                        File a=new File(newDisk[i].getPath()+File.separator+"autorun.inf");
                        if(a.exists())
                            Runtime.getRuntime().exec("C:\\WINDOWS\\system32\\notepad.exe "+a.getPath());
                    }
                    if((main.set>>3)%2==1){
                        Runtime.getRuntime().exec("C:\\WINDOWS\\explorer.exe "+newDisk[i].getPath());
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                new Thread(new autoScan(newDisk[i],main)).start();
            }
        }
        flag = false;
        notify();
        oldDisk=dirs;
    }
}

class autoScan implements Runnable{
    private File disk;
    private JFrameMain main;
    public autoScan(File Disk,JFrameMain Main){
        disk=Disk;
        main=Main;
    }
    @Override
    public void run() {
        if((main.set>>5)%2==1){
            main.trayIcon.displayMessage("通知","发现新磁盘"+disk, TrayIcon.MessageType.INFO);
            new Thread(new Scan(disk.getPath(),main.log,main.set)).start();
        }
        else
            new Warning(main,true,disk,"发现新磁盘"+disk);
    }
}