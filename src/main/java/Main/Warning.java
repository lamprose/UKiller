package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Warning extends JDialog {

    private String warning;
    private File disk;
    private JFrameMain main;
    public Warning(JFrameMain owner, boolean modal, File Disk, String Warning) {
        super(owner,"提醒:",modal);
        warning=Warning;
        disk=Disk;
        main=owner;
        init();
        setSize(170,100);
        setLocation(owner.getX()+215,owner.getY()+250);
        setResizable(false);
        setVisible(true);
    }

    void init() {
        setLayout(new FlowLayout()); // 设置布局

        add(new JLabel(warning));
        JButton scan=new JButton("开始扫描");
        scan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.mainScanDisk(disk);
                dispose();
            }
        });
        add(scan);
        JButton cancel=new JButton("取消");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(cancel);
    }
}
