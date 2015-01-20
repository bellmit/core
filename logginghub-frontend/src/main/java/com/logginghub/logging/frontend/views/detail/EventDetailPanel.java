package com.logginghub.logging.frontend.views.detail;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;

import com.logginghub.logging.LogEvent;
import com.logginghub.logging.frontend.ComponentKeys;
import com.logginghub.logging.frontend.Utils;

public class EventDetailPanel extends JPanel {
    private JLabel timestampLabel = new JLabel();
    private JLabel sourceApplicationLabel = new JLabel();
    private JLabel classLabel = new JLabel();
    private JLabel threadLabel = new JLabel();
    private JLabel levelLabel = new JLabel();
    private JLabel sourceHostLabel = new JLabel();
    private JLabel methodLabel = new JLabel();
    private JLabel loggerLabel = new JLabel();

    private JTextArea exceptionArea = new JTextArea();
    private JTextArea messageArea = new JTextArea();

    private MigLayout migLayout;
    private JScrollPane exceptionScroller;
    private JScrollPane messageScroller;

    // private JScrollPane exceptionAreaScroller;
    // private JSplitPane splitPane;

    public EventDetailPanel() {
        setName("eventDetailPanel");

        setBackground(Color.white);
        migLayout = new MigLayout("insets 0, gap 0", "[grow,fill]", "[fill][grow,fill][growprio 10,grow,fill]");
        // migLayout = new MigLayout("fill, insets 2", "[fill]", "[fill][grow,fill][fill]");
        setLayout(migLayout);

        JPanel topPanel = new JPanel(new MigLayout("insets 1, fill", "[][grow][][grow]", "[][][][]"));
        // JPanel topPanel = new JPanel(new MigLayout("", "[][][][]", "[][][][]"));

        JLabel a = new JLabel("Received timestamp");
        JLabel b = new JLabel("Level");
        JLabel c = new JLabel("Source application");
        JLabel d = new JLabel("Source host");
        JLabel e = new JLabel("Class");
        JLabel f = new JLabel("Method");
        JLabel g = new JLabel("Thread");
        JLabel h = new JLabel("Logger");

        addCopyListener(timestampLabel);
        addCopyListener(sourceApplicationLabel);
        addCopyListener(classLabel);
        addCopyListener(threadLabel);
        addCopyListener(levelLabel);
        addCopyListener(sourceHostLabel);
        addCopyListener(methodLabel);
        addCopyListener(loggerLabel);
        addCopyListener(messageArea);
        addCopyListener(exceptionArea);

        topPanel.add(a);
        topPanel.add(timestampLabel);
        topPanel.add(b);
        topPanel.add(levelLabel, "wrap");
        topPanel.add(c);
        topPanel.add(sourceApplicationLabel);
        topPanel.add(d);
        topPanel.add(sourceHostLabel, "wrap");
        topPanel.add(e);
        topPanel.add(classLabel);
        topPanel.add(f);
        topPanel.add(methodLabel, "wrap");
        topPanel.add(g);
        topPanel.add(threadLabel);
        topPanel.add(h);
        topPanel.add(loggerLabel);

        Font notBold = Font.decode("Arial 10 PLAIN");
        a.setFont(notBold);
        b.setFont(notBold);
        c.setFont(notBold);
        d.setFont(notBold);
        e.setFont(notBold);
        f.setFont(notBold);
        g.setFont(notBold);
        h.setFont(notBold);

        Font bold = Font.decode("Arial 10 BOLD");
        timestampLabel.setFont(bold);
        levelLabel.setFont(bold);
        sourceApplicationLabel.setFont(bold);
        sourceHostLabel.setFont(bold);
        classLabel.setFont(bold);
        methodLabel.setFont(bold);
        threadLabel.setFont(bold);
        loggerLabel.setFont(bold);

        exceptionArea.setLineWrap(true);
        Color exceptionRedColour = Color.decode("#ffdddd");
        exceptionArea.setBackground(exceptionRedColour);
        messageArea.setLineWrap(true);

        topPanel.setBackground(Color.decode("#ddeeff"));

        // JScrollPane messageAreaScroller = new JScrollPane(messageArea);
        // exceptionAreaScroller = new JScrollPane(exceptionArea);

        // splitPane = new JSplitPane();
        // splitPane.setLeftComponent(messageAreaScroller);
        // splitPane.setRightComponent(exceptionAreaScroller);
        // splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        // splitPane.setDividerSize(2);

        topPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        messageArea.setEditable(false);
        messageArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        exceptionArea.setEditable(false);
        exceptionArea.setName("exceptionArea");

        messageScroller = new JScrollPane(messageArea);
        exceptionScroller = new JScrollPane(exceptionArea);

//        messageScroller.setBackground(Color.white);
//        messageScroller.setBorder(BorderFactory.createTitledBorder("Message"));
//        exceptionScroller.setBackground(exceptionRedColour);
//        exceptionScroller.setBorder(BorderFactory.createTitledBorder("Exception stack trace"));
        
        // Disable auto scroll to caret in the message and exception areas
        DefaultCaret messageCaret = (DefaultCaret)messageArea.getCaret();
        messageCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        
        DefaultCaret exceptionCaret = (DefaultCaret)exceptionArea.getCaret();
        exceptionCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        add(topPanel, "cell 0 0");
        add(messageScroller, "cell 0 1");
        add(exceptionScroller, "cell 0 2, hidemode 2");

        exceptionShrunk();

        messageArea.setName(ComponentKeys.EventDetailMessage.name());
        sourceApplicationLabel.setName(ComponentKeys.EventDetailSourceApplication.name());
    }

    private void addCopyListener(final JTextArea area) {
        area.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    StringSelection selection = new StringSelection(area.getText());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                }
            }
        });

    }

    private void addCopyListener(final JLabel label) {
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    StringSelection selection = new StringSelection(label.getText());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                }
            }
        });

    }

    public void update(LogEvent event) {

        timestampLabel.setText(Utils.formatTime(event.getOriginTime()));
        levelLabel.setText(event.getLevelDescription());
        sourceApplicationLabel.setText(event.getSourceApplication());
        sourceHostLabel.setText(event.getSourceHost() + " (" + event.getSourceAddress() + ")");
        classLabel.setText(event.getSourceClassName());
        methodLabel.setText(event.getSourceMethodName());
        threadLabel.setText(event.getThreadName());
        loggerLabel.setText(event.getLoggerName());

        if (event.getFormattedException() != null) {
            exceptionArea.setText(event.getFormattedException());
            exceptionNormalSize();
        }
        else {
            exceptionArea.setText("");
            exceptionShrunk();
        }

        messageArea.setText(event.getMessage());
        messageArea.setCaretPosition(0);
        messageArea.getCaret().setMagicCaretPosition(new Point(0,0));
        validate();

//        messageScroller.getVerticalScrollBar().setValue(0);
//        exceptionScroller.getVerticalScrollBar().setValue(0);

    }

    private void exceptionShrunk() {
//        exceptionArea.setVisible(false);
        exceptionScroller.setVisible(false);
        // splitPane.setDividerLocation(1);
        // Dimension dimension = new Dimension(0,0);
        // exceptionArea.setSize(dimension);
        // exceptionArea.setMaximumSize(dimension);
        // exceptionArea.setMinimumSize(dimension);
        // exceptionArea.setPreferredSize(dimension);
    }

    private void exceptionNormalSize() {
//        exceptionArea.setVisible(true);
        exceptionScroller.setVisible(true);
        // splitPane.setDividerLocation(0.5);
        // exceptionArea.setMaximumSize(null);
        // exceptionArea.setMinimumSize(null);
        // exceptionArea.setPreferredSize(null);
    }

    public void clear() {
        messageArea.setText("");
        exceptionArea.setText("");
        timestampLabel.setText("");
        levelLabel.setText("");
        sourceApplicationLabel.setText("");
        sourceHostLabel.setText("");
        classLabel.setText("");
        methodLabel.setText("");
        threadLabel.setText("");
        loggerLabel.setText("");
    }
}
