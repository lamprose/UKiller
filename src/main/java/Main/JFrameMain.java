import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class JFrameMain extends JFrame {
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JMenuBar menubar;
    private JMenuItem settingsMenu, aboutMenu;
    private JPanel topPanel,mainPanel;
    private JButton startScan,clearLog;
    private JComboBox<File> diskList=new JComboBox<>(File.listRoots());
    JTextArea log;
    private Settings settings=new Settings(JFrameMain.this,true);
    private About about=new About(JFrameMain.this,true);
    int set=settings.getSettings();
    private Thread scanThread;
    public WatchDisk watchDiskThread=new WatchDisk(JFrameMain.this);
    TrayIcon trayIcon;//托盘图标 new
    private SystemTray systemTray;//系统托盘  //

    public JFrameMain(){
        setLayout(new GridBagLayout());
        init();
        setSize(600,600);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocation(400,80);
        setResizable(false);
        setVisible(true);
        CheckUpdate();
        addWindowListener(new WindowAdapter() {
            @Override
            //
            public void windowClosing(WindowEvent e) {
                if(set%2==1)
                    return;
                Object[] options ={ "最小化托盘(默认)", "退出" };  //自定义按钮上的文字
                int ret= JOptionPane.showOptionDialog(JFrameMain.this,"确定关闭？","提醒",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
                if(ret==1)//直接退出
                    System.exit(0);
                else{//最小化托盘
                    trayIcon.displayMessage("通知：", "程序最小化到系统托盘", TrayIcon.MessageType.INFO);
                    set=set^1;
                    settings.setSettings(set);
                    settings.saveSettings(set);
                    return;
                }

            }
        });
        if(SystemTray.isSupported()){//判断当前平台是否支持系统托盘
            systemTray = SystemTray.getSystemTray();//获得系统托盘的实例
            try {
                PopupMenu pop = new PopupMenu(); // 增加托盘右击菜单
                MenuItem showSetting=new MenuItem("设置");
                MenuItem show = new MenuItem("还原");
                MenuItem exit = new MenuItem("退出");
                MenuItem showAbout = new MenuItem("关于");

                showSetting.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setVisible(true);
                        setExtendedState(JFrame.NORMAL);
                        settings.showSetting(getX()+200,getY()+180);
                    }
                });

                show.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) { // 按下还原键
                        setVisible(true);
                        setExtendedState(JFrame.NORMAL);
                        toFront();
                    }

                });

                exit.addActionListener(new ActionListener() { // 按下退出键
                    public void actionPerformed(ActionEvent e) {
                        systemTray.remove(trayIcon);
                        System.exit(0);
                    }

                });

                showAbout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setVisible(true);
                        setExtendedState(JFrame.NORMAL);
                        about.showAbout(getX()+200,getY()+250);
                    }
                });
                pop.add(showSetting);
                pop.add(show);
                pop.add(showAbout);
                pop.add(exit);
                Image image = Toolkit.getDefaultToolkit().getImage("src/shield.png");
                trayIcon = new TrayIcon(image,"UKiller",pop);
                systemTray.add(trayIcon);//设置托盘的图标，shield.png与该类文件同一目录
            }
            catch (AWTException e2) {e2.printStackTrace();}
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowIconified(WindowEvent e) {
                    dispose();//窗口最小化时dispose该窗口
                }
            });
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getClickCount() == 2){//双击托盘窗口再现
                        setExtendedState(Frame.NORMAL);
                        setVisible(true);
                    }
                }
            });
        }
    }

    public void init() {
        menubar = new JMenuBar();
        menubar.setLayout(new FlowLayout());
        settingsMenu = new JMenuItem("设置"); // JMnud的实例就是一个菜单
        aboutMenu = new JMenuItem("关于");
        settingsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.showSetting(getX()+200,getY()+180);
            }
        });
        aboutMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                about.showAbout(getX()+200,getY()+250);
            }
        });
        menubar.add(settingsMenu); // 菜单条中加入菜单
        menubar.add(aboutMenu);
        setJMenuBar(menubar); // 添加一个菜单条

        //上侧的工具选择面板
        topPanel = new JPanel();
        this.add(topPanel, new GBC(0,0,2,1).
                setFill(GBC.BOTH).setIpad(0, 0).setWeight(100, 0));
        topInit();

        //左侧的具体工具面板
        mainPanel = new JPanel();
        this.add(mainPanel,new GBC(0,1).
                setFill(GBC.BOTH).setIpad(70, 90).setWeight(100, 100));
        mainInit();


        watchDiskThread.start();

    }

    private void topInit(){
        topPanel.setLayout(new GridLayout(1,4));
        topPanel.add(new JLabel("请选择磁盘:"));
        topPanel.add(diskList);
        startScan=new JButton("开始扫描");
        startScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScanDisk((File)diskList.getSelectedItem());
            }
        });
        topPanel.add(startScan);
        clearLog=new JButton("安全打开");
        clearLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Runtime.getRuntime().exec("C:\\WINDOWS\\explorer.exe "+diskList.getSelectedItem());
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        });
        topPanel.add(clearLog);
    }

    private void mainInit(){
        mainPanel.setLayout(new GridLayout(1,1));
        log=new JTextArea();
        log.setWrapStyleWord(true);
        log.setLineWrap(true);
        JScrollPane js=new JScrollPane(log,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        File logPath=new File("log.txt");
        if(logPath.exists()&&!logPath.isDirectory()){
            try{
                FileInputStream fis=new FileInputStream(logPath);
                byte[] buf = new byte[1024];
                StringBuffer sb=new StringBuffer();
                while((fis.read(buf))!=-1){
                    sb.append(new String(buf));
                    buf=new byte[1024];//重新生成，避免和上次读取的数据重复
                }
                log.setText(sb.toString());
                fis.close();
            }catch (Exception e){
                System.exit(0);
            }
        }

        mainPanel.add(js);
        mainPanel.setBorder(BorderFactory.createTitledBorder("日志"));
    }


    public void refreshDisk(){
        diskList.removeAllItems();
        for(File f:File.listRoots())
            diskList.addItem(f);
    }

    public void mainScanDisk(File disk){
        scanThread=new Thread(new Scan(disk.toString(),log,set));
        scanThread.start();
    }

    private void CheckUpdate(){
        new Update(JFrameMain.this).CheckUpdate();
    }
}