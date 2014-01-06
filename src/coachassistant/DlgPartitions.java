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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * This dialog enables the user to create regions using numeric values, remove
 * regions, and add existing regions to partitions.
 * 
 * @author Sina
 * 
 */
public class DlgPartitions extends JDialog implements ActionListener
{
    private static final long serialVersionUID = 8562380956243779618L;

    JButton                   btnCreateReg     = new JButton("Create >");
    JButton                   btnDelReg        = new JButton("Delete");
    JButton                   btnAddToPart     = new JButton(">");
    JButton                   btnDelFromPart   = new JButton("<");
    JLabel                    lblRegs          = new JLabel("All the regions");
    JLabel                    lblParts         = new JLabel("Partitions");

    DefaultListModel          modelListRegs    = new DefaultListModel();
    DefaultListModel          modelListParts   = new DefaultListModel();
    JList                     listRegions      = new JList(modelListRegs);
    JList                     listPartitions   = new JList(modelListParts);

    JTextField                txtX1            = new JTextField("0.0");
    JTextField                txtY1            = new JTextField("0.0");
    JTextField                txtX2            = new JTextField("0.0");
    JTextField                txtY2            = new JTextField("0.0");
    JTextField                txtName          = new JTextField("");
    JLabel                    lblX1            = new JLabel("X1:");
    JLabel                    lblX2            = new JLabel("X2:");
    JLabel                    lblY1            = new JLabel("Y1:");
    JLabel                    lblY2            = new JLabel("Y2:");
    JLabel                    lblSelect        = new JLabel(
                                                       "Specify Region Name:");

    private StrategyData      strategyData     = null;

    public DlgPartitions(JFrame parent)
    {
        super(parent, "Partitions", true);
        setResizable(false);
        this.setLayout(new BorderLayout());

        strategyData = CoachAssistant.getInstance().strategyData;

        final int listWidth = 150;
        final int listHeight = 250;

        JPanel panelWest = new JPanel(null);
        panelWest.setPreferredSize(new Dimension(150, 300));

        lblX1.setBounds(20, 30, 50, 20);
        txtX1.setBounds(50, 30, 80, 20);
        lblY1.setBounds(20, 70, 50, 20);
        txtY1.setBounds(50, 70, 80, 20);
        lblX2.setBounds(20, 110, 50, 20);
        txtX2.setBounds(50, 110, 80, 20);
        lblY2.setBounds(20, 150, 50, 20);
        txtY2.setBounds(50, 150, 80, 20);
        lblSelect.setBounds(20, 190, 150, 20);
        txtName.setBounds(20, 210, 125, 20);

        panelWest.add(lblX1);
        panelWest.add(txtX1);
        panelWest.add(lblY1);
        panelWest.add(txtY1);
        panelWest.add(lblX2);
        panelWest.add(txtX2);
        panelWest.add(lblY2);
        panelWest.add(txtY2);
        panelWest.add(lblSelect);
        panelWest.add(txtName);

        JPanel panelCenter = new JPanel(null);
        panelCenter.setPreferredSize(new Dimension(560, listHeight + 50));

        btnDelReg.setBounds(30, 100, 80, 25);
        btnCreateReg.setBounds(30, 150, 80, 25);

        lblRegs.setBounds(130, 10, listWidth, 20);
        JScrollPane scpListRegs = new JScrollPane(listRegions);
        scpListRegs.setBounds(130, 30, listWidth, listHeight);

        btnAddToPart.setBounds(300, 100, 70, 25);
        btnDelFromPart.setBounds(300, 150, 70, 25);

        lblParts.setBounds(390, 10, listWidth, 20);
        JScrollPane scpListParts = new JScrollPane(listPartitions);
        scpListParts.setBounds(390, 30, listWidth, listHeight);

        panelCenter.add(btnCreateReg);
        panelCenter.add(btnDelReg);
        panelCenter.add(lblRegs);
        panelCenter.add(scpListRegs);
        panelCenter.add(btnAddToPart);
        panelCenter.add(btnDelFromPart);
        panelCenter.add(lblParts);
        panelCenter.add(scpListParts);

        add(panelWest, BorderLayout.WEST);
        add(panelCenter, BorderLayout.CENTER);

        btnAddToPart.addActionListener(this);
        btnCreateReg.addActionListener(this);
        btnDelFromPart.addActionListener(this);
        btnDelReg.addActionListener(this);

        fillRegions();
        fillPartitions();

        pack();
        setLocation(parent.getLocation().x + parent.getWidth() / 2
                - this.getWidth() / 2, parent.getLocation().y
                + parent.getHeight() / 2 - this.getHeight() / 2);
        setVisible(true);
    }

    private void fillPartitions()
    {
        Iterator it = strategyData.getPartitionNamesIterator();
        while (it.hasNext())
        {
            modelListParts.addElement(it.next());
        }
    }

    private void fillRegions()
    {
        Iterator it = strategyData.getRegionNamesIterator();
        while (it.hasNext())
        {
            modelListRegs.addElement(it.next());
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        Object sender = e.getSource();
        if (sender == btnAddToPart)
        {
            int[] is = listRegions.getSelectedIndices();
            if (is.length > 0)
            {
                String name = "";
                for (int i = 0; i < is.length; ++i)
                {
                    name = (String) modelListRegs.getElementAt(is[i]);
                    if (!strategyData.isRegInPartition(name))
                    {
                        strategyData.addRegToPartition(name);
                        modelListParts.addElement(name);
                    }
                }
            }
        }
        else if (sender == btnDelFromPart)
        {
            int[] is = listPartitions.getSelectedIndices();
            if (is.length > 0)
            {
                String name = "";
                for (int i = is.length - 1; i >= 0; --i)
                {
                    name = (String) modelListParts.getElementAt(is[i]);
                    strategyData.removeRegFromPartition(name);
                    modelListParts.removeElement(name);
                }
            }
        }
        else if (sender == btnDelReg)
        {
            int[] is = listRegions.getSelectedIndices();
            if (is.length > 0)
            {
                int result = JOptionPane
                        .showConfirmDialog(
                                null,
                                "Are you sure that you want to delete the selected regions permanently?",
                                "Delete Selected Regs?",
                                JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION)
                {
                    String name = "";
                    for (int i = is.length - 1; i >= 0; --i)
                    {
                        name = (String) modelListRegs.getElementAt(is[i]);
                        modelListRegs.removeElement(name);
                        strategyData.removeRegion(name);
                        modelListParts.removeElement(name);
                    }
                }
            }
        }
        else if (sender == btnCreateReg)
        {
            if (checkFormats())
            {
                String name = txtName.getText().trim();
                if (name.length() != 0)
                {
                    if (strategyData.regionExists(name))
                    {
                        JOptionPane.showMessageDialog(null,
                                "The specified region name already exists!");
                        return;
                    }
                    else
                    {
                        strategyData
                                .addRegion(name, Util.GlobalRect(getRect()));
                        modelListRegs.addElement(name);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null,
                            "Please enter a valid name for the region!");
                    return;
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,
                        "Invalid Format in number fields");
            }
        }
    }

    public boolean checkFormats()
    {
        try
        {
            getRect();
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    public Rect4d getRect()
    {
        return new Rect4d(Double.parseDouble(txtX1.getText()), Double
                .parseDouble(txtY1.getText()), Double.parseDouble(txtX2
                .getText()), Double.parseDouble(txtY2.getText()));
    }
}
