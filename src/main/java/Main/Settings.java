package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Settings extends JDialog {

    private int settings=0,tempSettings=0;
    private JCheckBox[] settingJCheckboxes=new JCheckBox[7];
    private File settingFile=new File("setting.ini");
    private String[] settingLabels={"1.自动开始扫描","2.自动打开Autorun.inf","3.自动安全打开磁盘","4.自动清除Autorun.inf",
            "5.删除脚本中调用文件","6.退出时最小化托盘","7.系统开机自启动"};
    private JFrameMain main;

    public Settings(JFrameMain owner, boolean modal) {
        super(owner,"设置",modal);
        main=owner;
        init();
        setSize(200,260);
        setVisible(false);
    }

    private void initCheckBox(int s){
        for(int i=0;i<6;i++){
            settingJCheckboxes[i].setSelected((s>>(6-i))%2==1);
        }
    }

    public void setSettings(int Setttings){
        tempSettings=settings=Setttings;
        initCheckBox(settings);
    }

    public int getSettings(){
        return settings;
    }

    public void showSetting(int X,int Y){
        setLocation(X,Y);
        initCheckBox(settings);
        setVisible(true);
    }

    void init() {
        if(settingFile.exists()){
            try{
                BufferedReader reader = null;
                reader = new BufferedReader(new FileReader(settingFile));
                String tempSetting;
                if((tempSetting = reader.readLine()) != null){
                    tempSettings=settings=Integer.parseInt(tempSetting);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        setLayout(new FlowLayout()); // 设置布局
        for(int i=0;i<7;i++){
            add(new JLabel(settingLabels[i]));
            settingJCheckboxes[i]=new JCheckBox("",(settings>>(6-i))%2==1);
            settingJCheckboxes[i].addItemListener(new checkListener(i));
            add(settingJCheckboxes[i]);
        }
        if(!settingJCheckboxes[0].isSelected()){
            settingJCheckboxes[1].setEnabled(false);
            settingJCheckboxes[2].setEnabled(false);
        }

        JButton OK=new JButton("确认");
        OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings=tempSettings;
                saveSettings(settings);
                Startup su=new Startup();
                if(settings%2==1){
                    su.createLink();
                }else{
                    su.deleteLink();
                }
                main.set=settings;
                setVisible(false);
            }
        });
        JButton checkUpdate=new JButton("检查更新");
        checkUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Update(main).CheckUpdate();
            }
        });
        add(checkUpdate);
        add(OK);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tempSettings=settings;
            }
        });
    }
    private class checkListener implements ItemListener {
        private int settingItem;
        public checkListener(int SettingItem){
            settingItem=SettingItem;
        }
        @Override
        public void itemStateChanged(ItemEvent e) {
            tempSettings=tempSettings^(1<<6-settingItem);
            if(settingItem==0){
                if(!settingJCheckboxes[0].isSelected()){
                    settingJCheckboxes[1].setSelected(false);
                    settingJCheckboxes[1].setEnabled(false);
                    settingJCheckboxes[2].setSelected(false);
                    settingJCheckboxes[2].setEnabled(false);
                }
                else {
                    settingJCheckboxes[1].setEnabled(true);
                    settingJCheckboxes[2].setEnabled(true);
                }
            }
        }

    }

    public void saveSettings(int set){
        try{
            if(!settingFile.exists())
                settingFile.createNewFile();
            FileWriter fileWriter = new FileWriter(settingFile,false);
            fileWriter.write(new Integer(set).toString());
            fileWriter.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}