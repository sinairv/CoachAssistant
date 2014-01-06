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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This dialog enables the user to assign a player's region, by specifying the
 * numeric values for the coefficients.
 * <p>
 * This dialog is referred to, in the program as <i>Specify ball and player
 * regions</i>.
 * 
 * @author Sina
 * 
 */
public class DlgRenderFormula extends JDialog implements ActionListener
{
    private static final long serialVersionUID = -2951496459661156620L;

    public boolean            isCanceled       = true;

    JLabel                    lblSyntax        = new JLabel(
                                                       "Syntax: (((pt ball) * (pt c1 c2)) + (pt o1 o2))");

    JTextField                txtC1            = new JTextField("0.0");
    JTextField                txtC2            = new JTextField("0.0");
    JTextField                txtO1            = new JTextField("0.0");
    JTextField                txtO2            = new JTextField("0.0");
    JLabel                    lblC1            = new JLabel("C1:");
    JLabel                    lblC2            = new JLabel("C2:");
    JLabel                    lblO1            = new JLabel("O1:");
    JLabel                    lblO2            = new JLabel("O2:");

    JTextField                txtX1            = new JTextField("0.0");
    JTextField                txtY1            = new JTextField("0.0");
    JTextField                txtX2            = new JTextField("0.0");
    JTextField                txtY2            = new JTextField("0.0");
    JLabel                    lblX1            = new JLabel("X1:");
    JLabel                    lblX2            = new JLabel("X2:");
    JLabel                    lblY1            = new JLabel("Y1:");
    JLabel                    lblY2            = new JLabel("Y2:");
    JCheckBox                 cbLoad           = new JCheckBox("Load:");
    JLabel                    lblBallReg       = new JLabel("Ball Region:");
    JComboBox                 comboRegs        = new JComboBox();
    JButton                   btnOK            = new JButton("OK");
    JButton                   btnCancel        = new JButton("Cancel");

    JPanel                    panelBallReg;

    StrategyData              strategyData     = null;

    public DlgRenderFormula(JFrame parent, Rect4d curBallReg, Coefs4d coefs)
    {
        super(parent, "Render Formula", true);

        setResizable(false);

        this.setLayout(new BorderLayout());

        lblSyntax.setFont(new Font("Courier", 0, 12));
        lblSyntax.setPreferredSize(new Dimension(20, 40));
        lblSyntax.setHorizontalAlignment(JLabel.CENTER);

        panelBallReg = new JPanel(null);
        panelBallReg.setBorder(BorderFactory.createEtchedBorder());
        lblX1.setBounds(10, 10, 50, 20);
        txtX1.setBounds(40, 10, 80, 20);
        lblY1.setBounds(150, 10, 50, 20);
        txtY1.setBounds(180, 10, 80, 20);
        lblX2.setBounds(10, 50, 50, 20);
        txtX2.setBounds(40, 50, 80, 20);
        lblY2.setBounds(150, 50, 50, 20);
        txtY2.setBounds(180, 50, 80, 20);
        panelBallReg.add(lblX1);
        panelBallReg.add(txtX1);
        panelBallReg.add(lblY1);
        panelBallReg.add(txtY1);
        panelBallReg.add(lblX2);
        panelBallReg.add(txtX2);
        panelBallReg.add(lblY2);
        panelBallReg.add(txtY2);

        JPanel panelWest = new JPanel(null);
        panelWest.setPreferredSize(new Dimension(290, 180));
        lblBallReg.setBounds(10, 10, 150, 20);
        panelBallReg.setBounds(10, 30, 270, 90);
        cbLoad.setBounds(30, 140, 60, 20);
        comboRegs.setBounds(90, 140, 150, 20);

        panelWest.add(lblBallReg);
        panelWest.add(panelBallReg);
        panelWest.add(cbLoad);
        panelWest.add(comboRegs);

        JPanel panelEast = new JPanel(null);
        panelEast.setPreferredSize(new Dimension(140, 160));
        lblC1.setBounds(10, 20, 30, 20);
        txtC1.setBounds(40, 20, 90, 20);
        lblC2.setBounds(10, 60, 30, 20);
        txtC2.setBounds(40, 60, 90, 20);
        lblO1.setBounds(10, 100, 30, 20);
        txtO1.setBounds(40, 100, 90, 20);
        lblO2.setBounds(10, 140, 30, 20);
        txtO2.setBounds(40, 140, 90, 20);

        panelEast.add(lblC1);
        panelEast.add(txtC1);
        panelEast.add(lblC2);
        panelEast.add(txtC2);
        panelEast.add(lblO1);
        panelEast.add(txtO1);
        panelEast.add(lblO2);
        panelEast.add(txtO2);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.add(btnOK);
        panelButtons.add(btnCancel);

        add(lblSyntax, BorderLayout.NORTH);
        add(panelWest, BorderLayout.CENTER);
        add(panelEast, BorderLayout.EAST);
        add(panelButtons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnOK);

        if (curBallReg != null)
        {
            setRect(curBallReg);
        }

        if (coefs != null)
        {
            setCoefs(coefs);
        }

        strategyData = CoachAssistant.getInstance().strategyData;

        Util.addToCombo(comboRegs, strategyData.getRegionNamesIterator());
        comboRegs.setSelectedIndex(-1);

        comboRegs.setEnabled(false);

        btnOK.addActionListener(this);
        btnCancel.addActionListener(this);
        cbLoad.addActionListener(this);
        comboRegs.addActionListener(this);

        pack();
        setLocation(parent.getLocation().x + parent.getWidth() / 2
                - this.getWidth() / 2, parent.getLocation().y
                + parent.getHeight() / 2 - this.getHeight() / 2);
        setVisible(true);
    }

    public void setCoefs(Coefs4d cs)
    {
        txtC1.setText("" + Util.roundDefault(cs.c1));
        txtC2.setText("" + Util.roundDefault(cs.c2));
        txtO1.setText("" + Util.roundDefault(cs.o1));
        txtO2.setText("" + Util.roundDefault(cs.o2));
    }

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

    public boolean checkFormats()
    {
        try
        {
            getRect();
            getCoefs();
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

    public Coefs4d getCoefs()
    {
        return new Coefs4d(Double.parseDouble(txtC1.getText()), Double
                .parseDouble(txtC2.getText()), Double.parseDouble(txtO1
                .getText()), Double.parseDouble(txtO2.getText()));
    }

    public void actionPerformed(ActionEvent e)
    {
        Object sender = e.getSource();
        if (sender == btnOK)
        {
            if (checkFormats())
            {
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
        else if (sender == cbLoad)
        {
            Util.enableTree(panelBallReg, !cbLoad.isSelected());
            comboRegs.setEnabled(cbLoad.isSelected());
        }
        else if (sender == comboRegs)
        {
            setRect(strategyData.getRectForRegion((String) comboRegs
                    .getSelectedItem()));
        }
    }

}
