import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateDialog extends JDialog {

    private JFrameMain main;
    private String updateLog;

    public UpdateDialog(JFrameMain owner, boolean modal, String UpdateLog) {
        super(owner,"更新日志:",modal);
        updateLog=UpdateLog;
        main=owner;
        init();
        setSize(200,250);
        setLocation(owner.getX()+200,owner.getY()+175);
        setVisible(true);
    }

    private void init(){
        JPanel jp=new JPanel();
        setLayout(new FlowLayout()); // 设置布局

        JButton OK=new JButton("知道了");
        OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JTextArea updateJT=new JTextArea(updateLog);
        updateJT.setRows(10);
        updateJT.setColumns(15);
        updateJT.setLineWrap(true);
        jp.add(updateJT);
        jp.add(new JScrollPane(updateJT,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        add(jp);
        add(OK);
    }
}
