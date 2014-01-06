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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * This dialog enables the user to specify the coordination of the regions, by
 * specifying numeric values for them, rather than drawing the region.
 * 
 * @author Sina
 * 
 */
public class DlgSpReg extends JDialog implements ActionListener
{
    private static final long serialVersionUID = 1842100105076614725L;

    JButton                   btnOK            = new JButton("OK");
    JButton                   btnCancel        = new JButton("Cancel");
    JRadioButton              rdCustom         = new JRadioButton("Custom");
    JRadioButton              rdLoad           = new JRadioButton("Load");
    JTextField                txtX1            = new JTextField("0.0");
    JTextField                txtY1            = new JTextField("0.0");
    JTextField                txtX2            = new JTextField("0.0");
    JTextField                txtY2            = new JTextField("0.0");
    JTextField                txtName          = new JTextField("");
    JCheckBox                 cbSave           = new JCheckBox("Save as:");
    JLabel                    lblX1            = new JLabel("X1:");
    JLabel                    lblX2            = new JLabel("X2:");
    JLabel                    lblY1            = new JLabel("Y1:");
    JLabel                    lblY2            = new JLabel("Y2:");
    JLabel                    lblSelect        = new JLabel("Select Reg Name:");
    JComboBox                 comboRegs        = new JComboBox();

    JPanel                    panelCustomInner;
    JPanel                    panelLoadInner;
    ButtonGroup               btngrp;

    public boolean            isCanceled       = true;
    private StrategyData      strategyData     = null;

    public DlgSpReg(JFrame parent, String title, Rect4d curReg)
    {
        super(parent, title, true);

        setResizable(false);

        this.setLayout(new BorderLayout());
        JPanel panelUp = new JPanel(new BorderLayout());
        JPanel panelCenter = new JPanel(new BorderLayout());
        JPanel panelDown = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        panelCustomInner = new JPanel(null);
        panelCustomInner.setPreferredSize(new Dimension(270, 120));
        panelCustomInner.setBorder(BorderFactory.createEtchedBorder());

        lblX1.setBounds(10, 10, 50, 20);
        txtX1.setBounds(40, 10, 80, 20);
        lblY1.setBounds(150, 10, 50, 20);
        txtY1.setBounds(180, 10, 80, 20);
        lblX2.setBounds(10, 50, 50, 20);
        txtX2.setBounds(40, 50, 80, 20);
        lblY2.setBounds(150, 50, 50, 20);
        txtY2.setBounds(180, 50, 80, 20);
        cbSave.setBounds(20, 90, 80, 20);
        txtName.setBounds(110, 90, 135, 20);

        panelCustomInner.add(lblX1);
        panelCustomInner.add(txtX1);
        panelCustomInner.add(lblY1);
        panelCustomInner.add(txtY1);
        panelCustomInner.add(lblX2);
        panelCustomInner.add(txtX2);
        panelCustomInner.add(lblY2);
        panelCustomInner.add(txtY2);
        panelCustomInner.add(cbSave);
        panelCustomInner.add(txtName);

        panelUp.add(rdCustom, BorderLayout.NORTH);
        panelUp.add(panelCustomInner, BorderLayout.CENTER);

        panelLoadInner = new JPanel(null);
        panelLoadInner.setPreferredSize(new Dimension(270, 50));
        panelLoadInner.setBorder(BorderFactory.createEtchedBorder());

        btnOK.setDefaultCapable(true);
        lblSelect.setBounds(10, 10, 100, 20);
        comboRegs.setBounds(110, 10, 135, 20);

        panelLoadInner.add(lblSelect);
        panelLoadInner.add(comboRegs);

        panelCenter.add(rdLoad, BorderLayout.NORTH);
        panelCenter.add(panelLoadInner, BorderLayout.CENTER);

        panelDown.add(btnOK);
        panelDown.add(btnCancel);

        add(panelUp, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelDown, BorderLayout.SOUTH);

        btngrp = new ButtonGroup();
        btngrp.add(rdCustom);
        btngrp.add(rdLoad);

        rdCustom.addActionListener(this);
        rdLoad.addActionListener(this);
        rdCustom.setSelected(true);
        Util.enableTree(panelLoadInner, false);
        cbSave.setSelected(false);
        txtName.setEnabled(false);

        getRootPane().setDefaultButton(btnOK);

        cbSave.addActionListener(this);
        btnOK.addActionListener(this);
        btnCancel.addActionListener(this);
        comboRegs.addActionListener(this);

        if (curReg != null)
        {
            setRect(curReg);
        }

        strategyData = CoachAssistant.getInstance().strategyData;
        Util.addToCombo(comboRegs, strategyData.getRegionNamesIterator());
        comboRegs.setSelectedIndex(-1);

        pack();
        setLocation(parent.getLocation().x + parent.getWidth() / 2
                - this.getWidth() / 2, parent.getLocation().y
                + parent.getHeight() / 2 - this.getHeight() / 2);
        setVisible(true);
    }

    /**
     * Sets the contents of the argument as the texts for the text boxes.
     * 
     * @param rect
     *            the rectangle, to be initialy shown. Obviously it must be in
     *            field coordination to make sense.
     */
    public void setRect(Rect4d rect)
    {
        if (rect == null)
            return;

        Rect4d rcField = Util.FieldRect(rect);
        txtX1.setText("" + Util.roundDefault(rcField.x1));
        txtY1.setText("" + Util.roundDefault(rcField.y1));
        txtX2.setText("" + Util.roundDefault(rcField.x2));
        txtY2.setText("" + Util.roundDefault(rcField.y2));
    }

    public void actionPerformed(ActionEvent e)
    {
        Object sender = e.getSource();

        if (sender == rdLoad || sender == rdCustom)
        {
            Util.enableTree(panelCustomInner, btngrp.isSelected(rdCustom
                    .getModel()));
            Util.enableTree(panelLoadInner, btngrp
                    .isSelected(rdLoad.getModel()));
            txtName.setEnabled(cbSave.isSelected());
        }
        else if (sender == cbSave)
        {
            txtName.setEnabled(cbSave.isSelected());
        }
        else if (sender == btnOK)
        {
            if (checkFormats())
            {
                if (rdCustom.isSelected() && cbSave.isSelected())
                {
                    String name = txtName.getText().trim();
                    if (name.length() != 0)
                    {
                        if (strategyData.regionExists(name))
                        {
                            JOptionPane
                                    .showMessageDialog(null,
                                            "The specified region name already exists!");
                            return;
                        }
                        else
                        {
                            strategyData.addRegion(name, Util
                                    .GlobalRect(getRect()));
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null,
                                "Please enter a valid name for the region!");
                        return;
                    }
                }
                else if (rdLoad.isSelected())
                {
                    if (comboRegs.getSelectedIndex() < 0)
                    {
                        isCanceled = false;
                        setVisible(false);
                        return;
                    }
                }

                isCanceled = false;
                setVisible(false);
            }
            else
            {
                JOptionPane.showMessageDialog(null,
                        "Invalid Format in number fields");
            }
        }
        else if (sender == btnCancel)
        {
            isCanceled = true;
            setVisible(false);
        }
        else if (sender == comboRegs)
        {
            if (rdLoad.isSelected())
                setRect(strategyData.getRectForRegion((String) comboRegs
                        .getSelectedItem()));
        }
    }

    public boolean checkFormats()
    {
        try
        {
            getX1();
            getX2();
            getY1();
            getY2();
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    public double getX1()
    {
        return Double.parseDouble(txtX1.getText());
    }

    public double getY1()
    {
        return Double.parseDouble(txtY1.getText());
    }

    public double getX2()
    {
        return Double.parseDouble(txtX2.getText());
    }

    public double getY2()
    {
        return Double.parseDouble(txtY2.getText());
    }

    /**
     * returns the rectangle specified by the texts of the text-boxes
     */
    public Rect4d getRect()
    {
        return new Rect4d(getX1(), getY1(), getX2(), getY2());
    }
}
