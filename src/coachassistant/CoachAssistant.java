/*
 This file is part of Coach Assistant
 Copyright (C) 2004-2006 Sina Iravanian  <sina_iravanian@yahoo.com>

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; version 2 of the License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package coachassistant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This is the main class of the application, and the main frame of it. It hosts
 * all the components of the application. Also the main method is in this class.
 * 
 * @author Sina
 * 
 */
public class CoachAssistant extends JFrame implements ActionListener,
        ClipboardOwner, MouseListener, IStrategyDataListener
{
    private static final long serialVersionUID = -3552671466107760836L;

    // these lables serve as the status bar elements.
    JLabel                    statCoord        = new JLabel("0:0");
    JLabel                    statMessage      = new JLabel("Ready");
    JLabel                    statCoefs        = new JLabel("");

    Pitch                     pitch;

    public String             fileName         = Params.NewFileName;
    public boolean            isModified       = false;
    public boolean            isNew            = true;

    public StrategyData       strategyData     = new StrategyData();
    public String             ballRegName      = "";
    public FileChooser        fileChooser      = new FileChooser();

    /**
     * The pop-up menu, to be shown when the coefficients of the status bar is
     * selected.
     */
    JPopupMenu                popCoefs         = new JPopupMenu("Coefs Options");

    /**
     * The pop-up menu, to be shown when <i>our</i> players are right clicked
     */
    JPopupMenu                popPlayers       = new JPopupMenu(
                                                       "Players Options");

    public CoachAssistant(String title)
    {
        super(title);
        setLayout(new BorderLayout());

        createMenus();
        createToolbars();
        createStatusBar();
        createPopupMenus();

        strategyData.addListener(this);

        pitch = new Pitch(this);
        JPanel p = new JPanel(new GridBagLayout());
        p.add(pitch);
        add(new JScrollPane(p), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                cmdExit();
            }
        });

        pack();
        showCaption();

        centerFrame();
        setVisible(true);
    }

    /**
     * Centers the frame on the screen.
     * 
     */
    private void centerFrame()
    {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int w = Math.max(bounds.width / 2, Math.min(getWidth(), bounds.width));
        int h = Math.max(bounds.height / 2, Math
                .min(getHeight(), bounds.height));
        int x = center.x - w / 2, y = center.y - h / 2;
        setBounds(x, y, w, h);
        if (w == bounds.width && h == bounds.height)
            setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Forces the caption (title) of the form to be rewritten. The caption
     * consists of the name of the application, the name of the currently open
     * file name, and a star if the file has been modified.
     * 
     */
    public void showCaption()
    {
        String str = "CoachAssistant - " + fileName;
        if (isModified)
            str += " *";
        this.setTitle(str);
    }

    JMenuItem            mnuCopy          = new JMenuItem("Copy to clipboard");
    JMenu                mnuSetFor        = new JMenu("Set for ...");
    JMenuItem            mnuSetForOur1    = new JMenuItem("Our 1");
    JMenuItem            mnuSetForOur2    = new JMenuItem("Our 2");
    JMenuItem            mnuSetForOur3    = new JMenuItem("Our 3");
    JMenuItem            mnuSetForOur4    = new JMenuItem("Our 4");
    JMenuItem            mnuSetForOur5    = new JMenuItem("Our 5");
    JMenuItem            mnuSetForOur6    = new JMenuItem("Our 6");
    JMenuItem            mnuSetForOur7    = new JMenuItem("Our 7");
    JMenuItem            mnuSetForOur8    = new JMenuItem("Our 8");
    JMenuItem            mnuSetForOur9    = new JMenuItem("Our 9");
    JMenuItem            mnuSetForOur10   = new JMenuItem("Our 10");
    JMenuItem            mnuSetForOur11   = new JMenuItem("Our 11");

    JMenuItem            mnuSetSpRegion   = new JMenuItem(
                                                  "Set specified region...");
    JRadioButtonMenuItem mnuNullPartition = new JRadioButtonMenuItem(
                                                  "I'm invisible");
    ButtonGroup          rdMenuGroup;

    private void createPopupMenus()
    {
        popCoefs.add(mnuCopy);
        popCoefs.addSeparator();
        popCoefs.add(mnuSetFor);

        mnuSetForOur1.addActionListener(this);
        mnuSetForOur2.addActionListener(this);
        mnuSetForOur3.addActionListener(this);
        mnuSetForOur4.addActionListener(this);
        mnuSetForOur5.addActionListener(this);
        mnuSetForOur6.addActionListener(this);
        mnuSetForOur7.addActionListener(this);
        mnuSetForOur8.addActionListener(this);
        mnuSetForOur9.addActionListener(this);
        mnuSetForOur10.addActionListener(this);
        mnuSetForOur11.addActionListener(this);

        mnuSetFor.add(mnuSetForOur1);
        mnuSetFor.add(mnuSetForOur2);
        mnuSetFor.add(mnuSetForOur3);
        mnuSetFor.add(mnuSetForOur4);
        mnuSetFor.add(mnuSetForOur5);
        mnuSetFor.add(mnuSetForOur6);
        mnuSetFor.add(mnuSetForOur7);
        mnuSetFor.add(mnuSetForOur8);
        mnuSetFor.add(mnuSetForOur9);
        mnuSetFor.add(mnuSetForOur10);
        mnuSetFor.add(mnuSetForOur11);

        mnuCopy.addActionListener(this);
        
        mnuSetSpRegion.addActionListener(this);
    }

    JButton btnNew, btnOpen, btnSave, btnSpBallReg, btnSpPlayerReg,
            btnSpBallAndPlayer;
    JButton btnExport, btnPartitions, btnLineup;
    JToggleButton btnDrawBallReg, btnDrawPlayerReg, btnMoveBall;

    private void createToolbars()
    {
        btnNew = new JButton();
        btnNew.setToolTipText("New");
        btnNew.setIcon(new ImageIcon("images/new.png"));
        btnNew.addActionListener(this);

        btnOpen = new JButton();
        btnOpen.setToolTipText("Open");
        btnOpen.setIcon(new ImageIcon("images/open.png"));
        btnOpen.addActionListener(this);

        btnSave = new JButton();
        btnSave.setToolTipText("Save");
        btnSave.setIcon(new ImageIcon("images/save.png"));
        btnSave.addActionListener(this);

        btnExport = new JButton();
        btnExport.setToolTipText("Export CLang");
        btnExport.setIcon(new ImageIcon("images/export.png"));
        btnExport.addActionListener(this);

        btnSpBallReg = new JButton();
        btnSpBallReg.setToolTipText("Specify Ball Region");
        btnSpBallReg.setIcon(new ImageIcon("images/spball.png"));
        btnSpBallReg.addActionListener(this);

        btnSpPlayerReg = new JButton();
        btnSpPlayerReg.setToolTipText("Specify Player Region");
        btnSpPlayerReg.setIcon(new ImageIcon("images/spplayer.png"));
        btnSpPlayerReg.addActionListener(this);

        btnDrawBallReg = new JToggleButton();
        btnDrawBallReg.setToolTipText("Draw Ball Region");
        btnDrawBallReg.setIcon(new ImageIcon("images/drball.png"));
        btnDrawBallReg.addActionListener(this);

        btnDrawPlayerReg = new JToggleButton();
        btnDrawPlayerReg.setToolTipText("Draw Player Region");
        btnDrawPlayerReg.setIcon(new ImageIcon("images/drplayer.png"));
        btnDrawPlayerReg.addActionListener(this);

        btnSpBallAndPlayer = new JButton();
        btnSpBallAndPlayer.setToolTipText("Specify Ball and Player Regions");
        btnSpBallAndPlayer.setIcon(new ImageIcon("images/spballplayer.png"));
        btnSpBallAndPlayer.addActionListener(this);

        btnPartitions = new JButton();
        btnPartitions.setToolTipText("View Partitions");
        btnPartitions.setIcon(new ImageIcon("images/partitions.png"));
        btnPartitions.addActionListener(this);

        btnMoveBall = new JToggleButton();
        btnMoveBall.setToolTipText("Move the Ball");
        btnMoveBall.setIcon(new ImageIcon("images/moveball.png"));
        btnMoveBall.addActionListener(this);

        btnLineup = new JButton();
        btnLineup.setToolTipText("Lineup the Players");
        btnLineup.setIcon(new ImageIcon("images/lineup.png"));
        btnLineup.addActionListener(this);

        JToolBar toolbarFile = new JToolBar("File", JToolBar.HORIZONTAL);
        toolbarFile.add(btnNew);
        toolbarFile.addSeparator();
        toolbarFile.add(btnOpen);
        toolbarFile.add(btnSave);
        toolbarFile.addSeparator();
        toolbarFile.add(btnExport);

        JToolBar toolbarRegions = new JToolBar("Regions", JToolBar.HORIZONTAL);
        toolbarRegions.add(btnSpBallReg);
        toolbarRegions.add(btnSpPlayerReg);
        toolbarRegions.addSeparator();
        toolbarRegions.add(btnDrawBallReg);
        toolbarRegions.add(btnDrawPlayerReg);
        toolbarRegions.addSeparator();
        toolbarRegions.add(btnSpBallAndPlayer);
        toolbarRegions.addSeparator();
        toolbarRegions.add(btnPartitions);

        JToolBar toolbarMoveBall = new JToolBar("Move the Ball",
                JToolBar.HORIZONTAL);
        toolbarMoveBall.add(btnMoveBall);
        toolbarMoveBall.add(btnLineup);

        JPanel panelToolBars = new JPanel();
        panelToolBars.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelToolBars.add(toolbarFile);
        panelToolBars.add(toolbarRegions);
        panelToolBars.add(toolbarMoveBall);

        add(panelToolBars, BorderLayout.NORTH);
    }

    JMenuItem mnuNew;
    JMenuItem mnuOpen;
    JMenuItem mnuSave;
    JMenuItem mnuSaveAs;
    JMenuItem mnuExit;
    JMenuItem mnuAbout;

    private void createMenus()
    {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');

        mnuNew = new JMenuItem("New");
        mnuNew.setMnemonic('n');
        mnuNew.addActionListener(this);

        mnuOpen = new JMenuItem("Open");
        mnuOpen.setMnemonic('o');
        mnuOpen.addActionListener(this);

        mnuSave = new JMenuItem("Save");
        mnuSave.setMnemonic('s');
        mnuSave.addActionListener(this);

        mnuSaveAs = new JMenuItem("Save as ...");
        mnuSaveAs.setMnemonic('a');
        mnuSaveAs.addActionListener(this);

        mnuExit = new JMenuItem("Exit");
        mnuExit.setMnemonic('x');
        mnuExit.addActionListener(this);

        fileMenu.add(mnuNew);
        fileMenu.addSeparator();
        fileMenu.add(mnuOpen);
        fileMenu.add(mnuSave);
        fileMenu.add(mnuSaveAs);
        fileMenu.addSeparator();
        fileMenu.add(mnuExit);

        JMenu helpMenu = new JMenu("Help");
        fileMenu.setMnemonic('h');

        mnuAbout = new JMenuItem("About Coach Assistant");
        mnuAbout.setMnemonic('a');
        mnuAbout.addActionListener(this);

        helpMenu.add(mnuAbout);

        JMenuBar bar = new JMenuBar();
        bar.add(fileMenu);
        bar.add(helpMenu);
        setJMenuBar(bar);
    }

    private void createStatusBar()
    {
        statCoord.setBorder(BorderFactory.createEtchedBorder());
        statMessage.setBorder(BorderFactory.createEtchedBorder());
        statCoefs.setBorder(BorderFactory.createEtchedBorder());

        statCoord.setPreferredSize(new Dimension(100, 20));
        statCoefs.setPreferredSize(new Dimension(290, 20));
        statMessage.setPreferredSize(new Dimension(400, 20));
        statCoord.setVerticalAlignment(JButton.CENTER);
        statCoord.setHorizontalAlignment(JButton.CENTER);
        statCoefs.setVerticalAlignment(JButton.CENTER);
        statCoefs.setHorizontalAlignment(JButton.CENTER);
        statMessage.setVerticalAlignment(JButton.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.add(statCoord);
        statusBar.add(statCoefs);
        statusBar.add(statMessage);

        statCoefs.addMouseListener(this);

        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Sets the coordinations to be shown in the status bar. The arguments must
     * be in global coordination. They are changed to the field-coordination,
     * before shown.
     * 
     * @param x
     *            x in global coordination
     * @param y
     *            y in global coordination
     */
    public void setCoord(double x, double y)
    {
        statCoord.setText("" + Util.roundTo(Util.FieldX(x), 2) + " : "
                + Util.roundTo(Util.FieldY(y), 2));
    }

    /**
     * Sets the message to be shown in the message portion of the status bar.
     */
    public void setStatusMessage(String msg)
    {
        statMessage.setText(msg);
    }

    /**
     * Sets the coefficients to be shown in the coefficients portion of the
     * status bar. The coefficients are shown in their CLang counterpart, to
     * enable the user copy and paste the result string, in his/her own
     * hand-coded CLang file.
     */
    public void setStatusCoefs(double c1, double c2, double o1, double o2)
    {
        c1 = Util.roundTo(c1, 2);
        c2 = Util.roundTo(c2, 2);
        o1 = Util.roundTo(o1, 2);
        o2 = Util.roundTo(o2, 2);

        statCoefs.setText("(((pt ball) * (pt  " + c1 + "  " + c2 + ")) + (pt  "
                + o1 + "  " + o2 + "))");
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException e)
        {
        }
        catch (InstantiationException e)
        {
        }
        catch (IllegalAccessException e)
        {
        }
        catch (UnsupportedLookAndFeelException e)
        {
        }

        CoachAssistant ca = new CoachAssistant("Coach Assistant");
        caInstance = ca;
    }

    /**
     * The one and only one instance of the application.
     */
    private static CoachAssistant caInstance = null;

    /**
     * returns the instance, so the application's instance will be globally
     * accessible.
     */
    public static CoachAssistant getInstance()
    {
        return caInstance;
    }

    /**
     * resets all the application components to their initial state. It is
     * called when the user commands "new".
     * 
     */
    public void resetAll()
    {
        pitch.clear();
        ballRegName = "";
        setStatusMessage("Ready");
        statCoefs.setText("");
    }

    /**
     * The general purpose event-handler of the form. It may call the cmdXXX
     * counterpart for the special event.
     */
    public synchronized void actionPerformed(ActionEvent e)
    {
        Object sender = e.getSource();
        if (sender == mnuNew || sender == btnNew)
        {
            cmdNew();
        }
        else if (sender == mnuOpen || sender == btnOpen)
        {
            cmdOpen();
        }
        else if (sender == mnuSave || sender == btnSave)
        {
            cmdSave();
        }
        else if (sender == mnuSaveAs)
        {
            cmdSaveAs();
        }
        else if (sender == mnuExit)
        {
            cmdExit();
        }
        else if (sender == btnSpBallReg || sender == btnSpPlayerReg)
        {
            cmdSpecifyRegion(sender);
        }
        else if (sender == btnExport)
        {
            cmdExportCLang();
        }
        else if (sender == btnPartitions)
        {
            DlgPartitions dlg = new DlgPartitions(this);
            dlg.dispose();
            dlg = null;
        }
        else if (sender == btnSpBallAndPlayer)
        {
            cmdSpecifayBallNPlayer();
        }
        else if (sender == btnDrawBallReg || sender == btnDrawPlayerReg)
        {
            cmdDrawRegion(sender);
        }
        else if (sender == btnMoveBall)
        {
            cmdMoveBall();
        }
        else if (sender == btnLineup)
        {
            pitch.lineupPlayers();
        }
        else if (sender == mnuCopy)
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit()
                    .getSystemClipboard();
            clipboard.setContents(new StringSelection(statCoefs.getText()),
                    this);
        }
        else if (sender == mnuAbout)
        {
            cmdAbout();
        }
        else if(sender == mnuSetSpRegion)
        {
            if (ballRegName.length() > 0)
            {
                int index = pitch.rightClickedPlayerIndex;

                strategyData.setCoefForPlayer(index, ballRegName,
                        getCoefs());
            }
        }
        // this branch checkes for menu items in the coefficients pop-up menu.
        else if (sender == mnuSetForOur1 || sender == mnuSetForOur2
                || sender == mnuSetForOur3 || sender == mnuSetForOur4
                || sender == mnuSetForOur5 || sender == mnuSetForOur6
                || sender == mnuSetForOur7 || sender == mnuSetForOur8
                || sender == mnuSetForOur9 || sender == mnuSetForOur10
                || sender == mnuSetForOur11)
        {
            if (ballRegName.length() > 0)
            {
                JMenuItem mnu = (JMenuItem) sender;
                int number = Integer.parseInt(mnu.getText().substring(4));

                strategyData.setCoefForPlayer(number - 1, ballRegName,
                        getCoefs());
            }
        }
    }

    /**
     * shows the about dialog.
     * 
     */
    private void cmdAbout()
    {
        String strAbout = "Coach Assistant\nVersion 2.1\n\n";
        strAbout += "Copyright  (c)  2004 - 2006\nSina Iravanian  <sina_iravanian@yahoo.com>\n\n";
        strAbout += "Please report bugs to the email address above.\n\n";
        strAbout += "For more information visit:\n";
        strAbout += "http://sina.iravanian.googlepages.com/robocup\n\n";

        JOptionPane.showMessageDialog(this, strAbout, "About Coach Assistant",
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Called whenever the user tries to exit. It first make sure that the file
     * is not modified; if so prompts the user to save it.
     * 
     */
    private void cmdExit()
    {
        if (isModified)
        {
            int result = JOptionPane.showConfirmDialog(null, fileName
                    + "\nhas been modified. Save before exit?",
                    "Save before exit?", JOptionPane.YES_NO_CANCEL_OPTION);

            switch (result)
            {
                case JOptionPane.CANCEL_OPTION:
                    return;
                case JOptionPane.NO_OPTION:
                    break;
                case JOptionPane.YES_OPTION:
                    if (!cmdSave())
                        return;
                    break;
            }
            System.exit(0);
        }
        else
        {
            System.exit(0);
        }
    }

    /**
     * Called whenever the user toggles the move-ball button.
     * 
     */
    private void cmdMoveBall()
    {
        if (btnMoveBall.isSelected())
        {
            pitch.setBallMoveMode();
        }
        else
        {
            pitch.unsetBallMoveMode();
        }
    }

    /**
     * called whenever the user toggles any of the draw-ball-region or
     * draw-player-region buttons.
     */
    private void cmdDrawRegion(Object sender)
    {
        pitch.isDrawingBall = btnDrawBallReg.isSelected();
        pitch.isDrawingPlayer = btnDrawPlayerReg.isSelected();

        if (sender == btnDrawBallReg)
            pitch.isDrawingPlayer = !pitch.isDrawingBall
                    & pitch.isDrawingPlayer;
        else
            pitch.isDrawingBall = !pitch.isDrawingPlayer & pitch.isDrawingBall;

        btnDrawBallReg.setSelected(pitch.isDrawingBall);
        btnDrawPlayerReg.setSelected(pitch.isDrawingPlayer);

        if (pitch.isDrawingBall || pitch.isDrawingPlayer)
        {
            setStatusMessage("Drag on the field to draw your region");
        }
        else
        {
            setStatusMessage("Ready");
        }
    }

    /**
     * called whenever the user presses the Render Ball (aka Specify ball and
     * Player Regions) button.
     */
    private void cmdSpecifayBallNPlayer()
    {
        DlgRenderFormula dlg = new DlgRenderFormula(this, pitch.rectBall,
                getCoefs());
        if (!dlg.isCanceled)
        {
            if (dlg.cbLoad.isSelected())
            {
                ballRegName = (String) dlg.comboRegs.getSelectedItem();
            }
            else
            {
                ballRegName = "";
            }
            pitch.setBallRect(Util.GlobalRect(dlg.getRect()));
            pitch.setPlayerRect(dlg.getRect(), dlg.getCoefs());
        }
        dlg.dispose();
        dlg = null;
    }

    /**
     * Called whenever the user presses Export CLang button.
     * 
     */
    private void cmdExportCLang()
    {
        DlgExportCLang dlg = new DlgExportCLang(this);
        dlg.dispose();
        dlg = null;
    }

    /**
     * Called whenever user presses any of the specify-ball-region or
     * specify-player-region buttons.
     */
    private void cmdSpecifyRegion(Object sender)
    {
        DlgSpReg frame = new DlgSpReg(this, "Specify "
                + (sender == btnSpBallReg ? "Ball" : "Player") + " Region",
                (sender == btnSpBallReg ? pitch.rectBall : pitch.rectPlayer));

        if (!frame.isCanceled)
        {
            Rect4d rc = frame.getRect();
            if (sender == btnSpBallReg)
            {
                pitch.setBallRect(Util.GlobalRect(rc));
                if (frame.rdLoad.isSelected())
                {
                    ballRegName = (String) frame.comboRegs.getSelectedItem();
                }
                else if (frame.cbSave.isSelected())
                {
                    ballRegName = frame.txtName.getText();
                }
                else
                {
                    ballRegName = "";
                }

            }
            else
            {
                pitch.setPlayerRect(Util.GlobalRect(rc));
            }
        }

        frame.dispose();
        frame = null;
    }

    /**
     * Called whenever the user presses the save button or save menu item.
     * Returns true if the file have been saved successfully, or false when
     * there is a problem or the user cancels the operation.
     */
    private boolean cmdSave()
    {
        if (isNew)
        {
            return cmdSaveAs();
        }
        else
        {
            try
            {
                strategyData.saveToFile(fileName);
                isModified = false;
                showCaption();
                return true;
            }
            catch (Exception ex)
            {
                return false;
            }
        }
    }

    /**
     * Called whenever the user presses the save as menu item Returns true if
     * the file have been saved successfully, or false when there is a problem
     * or the user cancels the operation.
     */
    private boolean cmdSaveAs()
    {
        fileChooser.setCASFilter();
        String openedFileName = fileChooser.saveFileDialog();
        if (openedFileName.length() > 0)
        {
            try
            {
                strategyData.saveToFile(openedFileName);
                isNew = false;
                isModified = false;
                fileName = openedFileName;
                pitch.repaint();
                showCaption();
                return true;
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null,
                        "Problem while reading file:\n" + openedFileName);
                return false;
            }
        }
        return false;
    }

    /**
     * Called whenever the user presses the open button or open menu-item.
     */
    public void cmdOpen()
    {
        fileChooser.setCASFilter();
        String openedFileName = fileChooser.openFileDialog();
        if (openedFileName.length() > 0)
        {
            try
            {
                strategyData.readFromFile(openedFileName);
                resetAll();
                isNew = false;
                isModified = false;
                fileName = openedFileName;
                pitch.repaint();
                showCaption();
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null,
                        "Problem while reading file:\n" + openedFileName);
            }
        }
    }

    /**
     * called whenever the user presses the new button or new menu-item.
     */
    public void cmdNew()
    {
        if (isModified)
        {
            int result = JOptionPane.showConfirmDialog(null, fileName
                    + "\nhas changed. Save changes?");
            switch (result)
            {
                case JOptionPane.YES_OPTION:
                    if (cmdSave())
                        break;
                    else
                        return;
                case JOptionPane.NO_OPTION:
                    break;
                case JOptionPane.CANCEL_OPTION:
                    return;
            }
        }
        fileName = Params.NewFileName;
        isModified = false;
        isNew = true;
        strategyData.clear();
        resetAll();
        pitch.repaint();
        showCaption();
    }

    /**
     * calculates and returns the coefficients of the current player and ball
     * regions
     */
    private Coefs4d getCoefs()
    {
        if (pitch.rectPlayer != null && pitch.rectBall != null)
        {
            double c1, c2, o1, o2;

            Rect4d rcBall = Util.FieldRect(pitch.rectBall);
            Rect4d rcPlayer = Util.FieldRect(pitch.rectPlayer);

            if (rcBall.getWidth() == 0)
                c1 = 0;
            else
                c1 = ((double) rcPlayer.getWidth()) / (rcBall.getWidth());

            o1 = rcPlayer.x1 - rcBall.x1 * c1;

            if (rcBall.getHeight() == 0)
                c2 = 0;
            else
                c2 = ((double) rcPlayer.getHeight()) / (rcBall.getHeight());

            o2 = rcPlayer.y1 - rcBall.y1 * c2;

            return new Coefs4d(c1, c2, o1, o2);
        }

        return new Coefs4d(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Calculates the coefficients if possible, and shows them in the status
     * bar.
     * 
     */
    public void showCoefs()
    {
        if (pitch.rectPlayer != null && pitch.rectBall != null)
        {
            Coefs4d cs = getCoefs();
            setStatusCoefs(cs.c1, cs.c2, cs.o1, cs.o2);
        }
    }

    /**
     * called whenever the user has finished dragging the mouse for drawing the
     * region
     * 
     */
    public synchronized void finishedDrawing()
    {
        if (btnDrawBallReg.isSelected())
        {
            btnDrawBallReg.setSelected(false);
            ballRegName = "";
        }
        else
        {
            btnDrawPlayerReg.setSelected(false);
        }

        setStatusMessage("Ready");
    }

    /**
     * a part of the <code>ClipboardOwner</code> interface
     */
    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
        // do nothing
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    /**
     * calls to pop-up the coefficents pop-up menu, if the coefficient portion
     * of the status bar has been right clicked.
     */
    public void mousePressed(MouseEvent e)
    {
        if (e.getSource() == statCoefs)
        {
            if (e.isPopupTrigger())
            {
                popupCoefsMenu(e.getX(), e.getY());
            }
        }
    }

    /**
     * calls to pop-up the coefficents pop-up menu, if the coefficient portion
     * of the status bar has been right clicked.
     */
    public void mouseReleased(MouseEvent e)
    {
        if (e.getSource() == statCoefs)
        {
            if (e.isPopupTrigger())
            {
                popupCoefsMenu(e.getX(), e.getY());
            }
        }
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * pops up the coefficients pop-up menu on the coordination specified.
     */
    public void popupCoefsMenu(int x, int y)
    {
        mnuCopy.setEnabled(statCoefs.getText().trim().length() != 0);
        mnuSetFor.setEnabled(strategyData.isRegInPartition(ballRegName));
        popCoefs.show(statCoefs, x, y);
    }

    /**
     * pops up the players pop-up menu of the specified player on the
     * coordination specified.
     */
    public void popupPlayersMenu(int unum, int x, int y)
    {
        if (strategyData.isPartitionsEmpty())
            return;

        if (strategyData.isRegInPartition(ballRegName))
        {
            mnuSetSpRegion.setEnabled(true);

            Enumeration<AbstractButton> en = rdMenuGroup.getElements();
            AbstractButton btn;
            while (en.hasMoreElements())
            {
                btn = en.nextElement();
                if (btn.getText().compareTo(ballRegName) == 0)
                {
                    btn.setSelected(true);
                    break;
                }
            }
        }
        else
        {
            mnuSetSpRegion.setEnabled(false);
            mnuNullPartition.setSelected(true);
        }

        popPlayers.show(pitch, x, y);
    }

    /**
     * Called whenever the user has finished moving the ball
     * 
     */
    public void finishedMovingBall()
    {
        btnMoveBall.setSelected(false);
        pitch.unsetBallMoveMode();
    }

    public void OnRegionsChanged()
    {
    }

    /**
     * Called whenever the partitions content of the strategyData is modified
     * The code here changes the contents of the player-popup-menu.
     */
    public void OnPartitionsChanged()
    {
        popPlayers.removeAll();

        rdMenuGroup = new ButtonGroup();

        Iterator<String> it = strategyData.getPartitionNamesIterator();
        JRadioButtonMenuItem mnu;
        String name;
        while (it.hasNext())
        {
            name = it.next();
            mnu = new JRadioButtonMenuItem(name);
            rdMenuGroup.add(mnu);

            mnu.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    JMenuItem mnu = (JMenuItem) e.getSource();
                    mnu.setSelected(true);
                    ballRegName = mnu.getText();
                    pitch.setBallRect(strategyData
                            .getRectForRegion(ballRegName));
                    if (strategyData.partitionExistsForPlayer(
                            pitch.rightClickedPlayerIndex, ballRegName))
                        pitch.setPlayerRect(Util.FieldRect(pitch.rectBall),
                                strategyData.getPlayerCoefs(
                                        pitch.rightClickedPlayerIndex,
                                        ballRegName));
                    pitch.repaint();
                }
            });

            popPlayers.add(mnu);
        }

        rdMenuGroup.add(mnuNullPartition);

        popPlayers.addSeparator();
        popPlayers.add(mnuSetSpRegion);
    }

    public void OnCoefsChanged()
    {
    }

    /**
     * Called whenever there is any change made in the strategy-data generally.
     */
    public void OnStrategyChanged()
    {
        isModified = true;
        showCaption();
    }
}
