package toni.missingmodschecker;

import net.minecraftforge.fml.loading.FMLPaths;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

public final class MissingModsWindow {

    public static void open(List<MissingModsChecker.RequiredMod> missingMods) throws Exception {
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frame = new JFrame("Missing Mods");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(560, 580));

        var iconStream = MissingModsWindow.class
            .getClassLoader()
            .getResourceAsStream("logo.png");

        Image icon = iconStream != null ? ImageIO.read(iconStream) : null;
        if (icon != null) frame.setIconImage(icon);

        frame.add(createHeader(icon), BorderLayout.NORTH);
        frame.add(createModsList(missingMods), BorderLayout.CENTER);
        frame.add(createFooter(missingMods), BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocus();

        synchronized (MissingModsWindow.class) {
            MissingModsWindow.class.wait();
        }
    }

    private static JComponent createHeader(Image icon) {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(new Color(230, 230, 230));
        header.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (icon != null) {
            JLabel iconLabel = new JLabel(new ImageIcon(
                icon.getScaledInstance(48, 48, Image.SCALE_SMOOTH)
            ));
            header.add(iconLabel, BorderLayout.WEST);
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Your modpack is missing the following mods");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel(
            "<html>The following mods are not available on the. " +
                "These mods are required, but were not found in your mods folder. " +
                "Please download them, move them to your mods folder, and restart.</html>"
        );
        subtitle.setFont(subtitle.getFont().deriveFont(14.5f));
        subtitle.setBorder(new EmptyBorder(6, 0, 0, 0));

        textPanel.add(title);
        textPanel.add(subtitle);

        JComponent helpLink = createHelpLink();
        helpLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        helpLink.setBorder(new EmptyBorder(8, 0, 0, 0));
        textPanel.add(helpLink);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    private static JComponent createModsList(List<MissingModsChecker.RequiredMod> mods) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(140, 140, 140));
        listPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        for (int i = 0; i < mods.size(); i++) {
            var mod = mods.get(i);
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);

            int topMargin = (i == 0 ? 0 : 6);
            wrapper.setBorder(new EmptyBorder(topMargin, 12, 6, 12));

            wrapper.add(createModEntry(mod), BorderLayout.CENTER);
            listPanel.add(wrapper);
        }

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(new Color(140, 140, 140));
        scrollWrapper.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollWrapper.add(scrollPane, BorderLayout.CENTER);

        return scrollWrapper;
    }

    private static JComponent createModEntry(MissingModsChecker.RequiredMod mod) {
        ModEntryPanel row = new ModEntryPanel(new Color(240, 240, 240));

        JLabel name = new JLabel(mod.displayName);
        name.setFont(name.getFont().deriveFont(14f));

        JButton download = new JButton("Download");
        download.setPreferredSize(new Dimension(160, 28));
        download.addActionListener(e -> openBrowser(mod.url));

        row.add(name, BorderLayout.CENTER);
        row.add(download, BorderLayout.EAST);
        return row;
    }


    private static JComponent createFooter(List<MissingModsChecker.RequiredMod> mods) {
        JPanel footer = new JPanel(new GridLayout(1, 2, 12, 0));
        footer.setBackground(new Color(230, 230, 230));
        footer.setBorder(new EmptyBorder(12, 12, 12, 12));

        Dimension buttonSize = new Dimension(0, 40);

        JButton close = new JButton("Open Mods Folder");
        close.setBackground(new Color(80, 80, 220));
        close.setForeground(Color.BLACK);
        close.setPreferredSize(buttonSize);
        close.setFont(close.getFont().deriveFont(Font.BOLD, close.getFont().getSize() * 1.2f));
        close.addActionListener(e -> {
            try {
                Path modsFolder = MissingModsChecker.getModsFolder();
                Desktop.getDesktop().open(modsFolder.toFile());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(footer,
                    "Failed to open the mods folder:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton downloadAll = new JButton("Download All");
        downloadAll.setBackground(new Color(76, 175, 80));
        downloadAll.setForeground(Color.BLACK);
        downloadAll.setPreferredSize(buttonSize);
        downloadAll.setFont(downloadAll.getFont().deriveFont(Font.BOLD, downloadAll.getFont().getSize() * 1.2f));
        downloadAll.addActionListener(e -> {
            for (var mod : mods) {
                openBrowser(mod.url);
            }
        });

        footer.add(close);
        footer.add(downloadAll);
        return footer;
    }

    private static JComponent createHelpLink() {
        JLabel link = new JLabel(
            "<html><a href=''>How To Install Mods Manually</a></html>"
        );

        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.setForeground(new Color(33, 150, 243));
        link.setFont(link.getFont().deriveFont(14.5f));

        link.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openBrowser("https://www.youtube.com/watch?v=YOUR_VIDEO_ID");
            }
        });

        return link;
    }

    private static void openBrowser(String url) {
        if (!Desktop.isDesktopSupported()) return;
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ignored) {}
    }

    private static final class DarkScrollBarUI extends BasicScrollBarUI {

        private static final Color TRACK = new Color(100, 100, 100);
        private static final Color THUMB = new Color(190, 190, 190);
        private static final Color THUMB_HOVER = new Color(160, 160, 160);

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(TRACK);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (!c.isEnabled()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(isThumbRollover() ? THUMB_HOVER : THUMB);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
            g2.dispose();
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private JButton zeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }
    }

    public static class ModEntryPanel extends JPanel {
        private final Color backgroundColor;
        private final Color shadowColor;
        private final int arc;
        private final int shadowOffset;

        public ModEntryPanel(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            this.shadowColor = new Color(0, 0, 0, 50);
            this.arc = 12;
            this.shadowOffset = 3;
            setOpaque(false);
            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(shadowColor);
            g2.fillRoundRect(
                shadowOffset, shadowOffset,
                getWidth() - shadowOffset*2, getHeight() - shadowOffset*2,
                arc, arc
            );

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - shadowOffset, getHeight() - shadowOffset, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
