package fabnun.jsoupscheduler;

import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TIcon {

    public static void run(final JFrame mainFrame, String iconPath) throws Exception {
        if (!SystemTray.isSupported()) {
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        Image bi = (iconPath == null) ? null : ImageIO.read(TIcon.class.getResource(iconPath));
        mainFrame.setIconImage(bi);
        TrayIcon ti = new TrayIcon(bi, mainFrame.getTitle());
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.setVisible(!mainFrame.isVisible());
            }
        };
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                if (JOptionPane.showConfirmDialog(mainFrame, "CONFIRM EXIT", "EXIT", JOptionPane.YES_OPTION) == 0) {
                    mainFrame.dispose();
                    System.exit(0);
                } else {
                    mainFrame.setVisible(true);
                }
            }
        });
        ti.addMouseListener(ml);
        tray.add(ti);
        mainFrame.setVisible(true);
        mainFrame.addWindowStateListener((WindowEvent e) -> {
            if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                mainFrame.setVisible(false);
                mainFrame.setExtendedState(e.getOldState());
            }
        });
    }
}
