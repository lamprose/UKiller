package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

public class About extends JDialog {

    public About(JFrame owner, boolean modal) {
        super(owner,"关于",modal);
        init();
        setSize(250,140);
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
        add(new JLabel("李鹏书    161310523"));
        JLabel uri=new JLabel("<html><u>https://github.com/lamprose/UKiller</u></html>");

        uri.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try{
                    Desktop d=Desktop.getDesktop();
                    URI path=new URI("https://github.com/lamprose/UKiller");
                    d.browse(path);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                uri.setForeground(Color.BLUE);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(uri.getForeground()!=Color.BLUE)
                    uri.setForeground(Color.RED);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(uri.getForeground()!=Color.BLUE)
                    uri.setForeground(Color.BLACK);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        add(uri);
        add(OK);
    }
}
