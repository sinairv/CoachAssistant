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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * This dialog enables the user to set the options needed to generate the CLang
 * file.
 * 
 * @author Sina
 * 
 */
public class DlgExportCLang extends JDialog implements ActionListener
{
    private static final long serialVersionUID = 3471922636256609349L;

    public boolean            isCanceled       = true;

    public JLabel             lblRuleNameInfo;
    public JCheckBox          cbRuleNamePref;
    public JTextField         txtRuleNamePref;
    public JCheckBox          cbPosRadius;
    public JTextField         txtPosRadius;
    public JCheckBox          cbEnableShooting;

    public JCheckBox          cbUsePlayOn;
    public JCheckBox          cbUseCustomCondition;
    public JTextField         txtCustomCondition;
    public JCheckBox          cbFreedomRadiusCondition;
    public JTextField         txtFreedomRadiusCondition;

    public JButton            btnGenerate;
    public JButton            btnCancel;

    public DlgExportCLang(JFrame parent)
    {
        super(parent, "Export CLang", true);

        setResizable(false);

        this.setLayout(null);

        lblRuleNameInfo = Util.createJLabel(this,
                "Rule Name: PREFIX + RegionName + UNUM", 15, 10);
        txtRuleNamePref = Util.createJTextField(this, "", 100, 40, 220);
        cbRuleNamePref = Util.creteJCheckBox(this, "PREFIX:", 10, 40,
                txtRuleNamePref);

        txtPosRadius = Util.createJTextField(this, "1.5", 250, 70, 70);
        cbPosRadius = Util.creteJCheckBox(this, "Use Positioning Radius:", 10,
                70, txtPosRadius);

        cbEnableShooting = Util.creteJCheckBox(this,
                "Generate Shooting Capabilities", 10, 100, null);

        cbUsePlayOn = Util.creteJCheckBox(this,
                "Add play-mode play-on condition", 10, 140, null);
        txtCustomCondition = Util.createJTextField(this, "", 160, 170, 160);
        cbUseCustomCondition = Util.creteJCheckBox(this,
                "Add custom condition: ", 10, 170, txtCustomCondition);

        txtFreedomRadiusCondition = Util.createJTextField(this, "2.0", 250,
                200, 70);
        cbFreedomRadiusCondition = Util.creteJCheckBox(this,
                "Use Freedom Radius Condition:", 10, 200,
                txtFreedomRadiusCondition);

        btnGenerate = Util.createJButton(this, "Generate", 220, 240, 100, 25,
                this);
        btnCancel = Util.createJButton(this, "Cancel", 115, 240, 100, 25, this);

        cbEnableShooting.setSelected(true);
        cbUsePlayOn.setSelected(true);

        getRootPane().setDefaultButton(btnGenerate);

        setSize(340, 310);
        setLocation(parent.getLocation().x + parent.getWidth() / 2
                - this.getWidth() / 2, parent.getLocation().y
                + parent.getHeight() / 2 - this.getHeight() / 2);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        Object sender = e.getSource();
        if (sender == btnGenerate)
        {
            if (!checkFormats())
            {
                JOptionPane.showMessageDialog(null,
                        "Numeric field not in a proper format");
                return;
            }

            if (!saveCLangFile())
            {
                return;
            }

            CLangGenerator cg = new CLangGenerator(
                    CoachAssistant.getInstance().strategyData, clangFile);

            cg.addPlayon = cbUsePlayOn.isSelected();

            if (cbUseCustomCondition.isSelected())
                cg.customCondition = txtCustomCondition.getText().trim();

            cg.enableShooting = cbEnableShooting.isSelected();

            if (cbFreedomRadiusCondition.isSelected())
                cg.freedomRadius = Double.parseDouble(txtFreedomRadiusCondition
                        .getText().trim());

            if (cbPosRadius.isSelected())
                cg.posRadius = Double
                        .parseDouble(txtPosRadius.getText().trim());

            if (cbRuleNamePref.isSelected())
                cg.ruleNamePrefix = txtRuleNamePref.getText().trim();

            try
            {
                cg.generateCLang();
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null,
                        "Problem in generating the CLang file");
            }

            isCanceled = false;
            setVisible(false);
        }
        else if (sender == btnCancel)
        {
            isCanceled = true;
            setVisible(false);
        }
    }

    private String clangFile = "";

    private boolean saveCLangFile()
    {
        FileChooser fileChooser = CoachAssistant.getInstance().fileChooser;
        fileChooser.setCLangFilter();
        String openedFileName = fileChooser.saveFileDialog();
        if (openedFileName.length() > 0)
        {
            clangFile = openedFileName;
            return true;
        }
        else
        {
            clangFile = "";
            return false;
        }
    }

    private boolean checkFormats()
    {
        if (cbPosRadius.isSelected())
        {
            try
            {
                Double.parseDouble(txtPosRadius.getText().trim());
            }
            catch (NumberFormatException ex)
            {
                return false;
            }
        }

        if (cbFreedomRadiusCondition.isSelected())
        {
            try
            {
                Double.parseDouble(txtFreedomRadiusCondition.getText().trim());
            }
            catch (NumberFormatException ex)
            {
                return false;
            }
        }

        return true;
    }

}
