import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class About extends JDialog {

    public About(JFrame owner, boolean modal) {
        super(owner,"关于",modal);
        init();
        setSize(200,100);
        setVisible(false);
    }

    public void showAbout(int X,int Y){
        setLocation(X,Y);
        setVisible(true);
    }

    void init() {
        setLayout(new FlowLayout()); // 设置布局

        JButton OK=new JButton("知道了");
        OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(new JLabel("李鹏书161310523"));
        add(OK);
    }
}
