package Dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompleteDialog extends JDialog {
    private JDialog main;
    private String updateLog;

    public CompleteDialog(JDialog owner, boolean modal, String UpdateLog) {
        super(owner,"更新完成!",modal);
        updateLog=UpdateLog;
        main=owner;
        init();
        setSize(200,100);
        setLocation(owner.getX()+100,owner.getY()-35);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    private void init(){
        setLayout(new FlowLayout());
        JButton reboot=new JButton("重启");
        reboot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Runtime.getRuntime().exec("reboot.bat");
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                System.exit(0);
            }
        });
        add(reboot);
    }
}
