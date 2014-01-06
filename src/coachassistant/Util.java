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

import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * The class <code>Util</code> provides all static utility methods which are
 * globally needed.
 * 
 * The utilities provided here, include utilities for mathematical, string
 * processing, and GUI-based functionalities.
 * 
 * @author Sina
 * 
 */
public class Util
{
    /**
     * returns the converted x-coordination specified by the argument from
     * system GUI to the field coordination.
     * 
     * <p>
     * The Field coordination is the one centered at the center of the game
     * field. The x axis of the field is ranged from -52.5 to +52.5
     * 
     * @param d
     *            the x of the point in system GUI coordination
     * @return the x of the argument in the field coordination
     */
    public static double FieldX(double d)
    {
        double r = d / (double) Params.dFieldScale;

        return r - Params.dHalfWidth;
    }

    /**
     * returns the converted y-coordination specified by the argument from
     * system GUI to the field coordination.
     * 
     * <p>
     * The Field coordination is the one centered at the center of the game
     * field. The y axis of the field is ranged from -34 to +34. The axis is
     * positive downward.
     * 
     * @param d
     *            the y of the point in system GUI coordination
     * @return the y of the argument in the field coordination
     */
    public static double FieldY(double d)
    {
        double r = d / (double) Params.dFieldScale;

        r -= Params.dHalfHeight;

        return r;
    }

    /**
     * returns the converted x-coordination specified by the argument from the
     * field coordination to the system GUI coordination.
     * 
     * <p>
     * The System GUI coordination is the one centered at the upper left corner
     * of the field's view control.
     * 
     * @param d
     *            the x of the point in the field coordination
     * @return the x of the argument in the system GUI coordination
     */
    public static double GlobalX(double d)
    {
        double r = d * Params.dFieldScale;
        r += Params.dHalfWidth * Params.dFieldScale;
        return r;
    }

    /**
     * returns the converted y-coordination specified by the argument from the
     * field coordination to the system GUI coordination.
     * 
     * <p>
     * The System GUI coordination is the one centered at the upper left corner
     * of the field's view control. The y axis is positive downward.
     * 
     * @param d
     *            the y of the point in the field coordination
     * @return the y of the argument in the system GUI coordination
     */
    public static double GlobalY(double d)
    {
        double r = d + Params.dHalfHeight;
        return r * Params.dFieldScale;
    }

    /**
     * returns a rectangle (<code>Rect4d</code> instance) in the field
     * coordination.
     * 
     * @param r
     *            a rectangle in the system GUI coordination
     */
    public static Rect4d FieldRect(Rect4d r)
    {
        return new Rect4d(FieldX(r.x1), FieldY(r.y1), FieldX(r.x2),
                FieldY(r.y2));
    }

    /**
     * returns a rectangle (<code>Rect4d</code> instance) in the system GUI
     * coordination.
     * 
     * @param r
     *            a rectangle in the field coordination
     */
    public static Rect4d GlobalRect(Rect4d r)
    {
        return new Rect4d(GlobalX(r.x1), GlobalY(r.y1), GlobalX(r.x2),
                GlobalY(r.y2));
    }

    /**
     * returns the rounded result of the argument to the n digits after the
     * decimal point.
     * 
     * @param d
     *            the double number to be rounded
     * @param n
     *            the number of digits after the decimal point
     */
    public static double roundTo(double d, int n)
    {
        d *= Math.pow(10, n);
        d = Math.round(d);
        d /= Math.pow(10, n);
        return d;
    }

    /**
     * returns the rounded result of the argument to the precision specified in
     * the parameters of the application (See <code>Params.Precision</code>).
     * <p>
     * All the double values that are shown to the user or saved in a
     * human-readable file are rounded to a reasonable precision using this
     * function.
     * 
     * @param d
     *            the double value to be rounded
     */
    public static double roundDefault(double d)
    {
        return roundTo(d, Params.Precision);
    }

    /**
     * returns true if the string argument is empty or completely containing
     * white-space characters
     * 
     * @param str
     *            the string argument to be tested
     * @return true if the argument is completely containing white-space
     *         characters or is empty
     */
    public static boolean isWhiteSpace(String str)
    {
        int l = str.length();
        for (int i = 0; i < l; ++i)
        {
            if (!Character.isWhitespace(str.charAt(i)))
                return false;
        }
        return true;
        // thus empty strings are considered whitespace
    }

    /**
     * returns the name of a file leaving out its extension (if any).
     * 
     * @param name
     *            the string containing the name of a file
     * @return name of the file without its extension
     */
    public static String fileNameNoExt(String name)
    {
        int index = name.lastIndexOf('.');
        if (index < 0)
            return name;
        else
            return name.substring(0, index);
    }

    /**
     * This function writes a line of string and appends a new-line character to
     * form a UNIX like text file.
     * 
     * @param br
     *            The Buffered Reader to write to
     * @param str
     *            The string to be written
     * @throws IOException
     */
    public static void writeLine(BufferedWriter br, String str)
            throws IOException
    {
        br.write(str + "\n");
    }

    /**
     * (Maybe unfortunately) in java disabling/enabling a container GUI
     * component does not disable/enable the childs. This method does that job
     * for you.
     * 
     * @param comp
     *            the component to be disabled/enabled
     * @param enable
     *            true for enabling, false for disabling
     */
    static void enableTree(Component comp, boolean enable)
    {
        if (comp instanceof Container)
        {
            Container root = (Container) comp;
            Component children[] = root.getComponents();
            for (int i = 0; i < children.length; i++)
            {
                if (children[i] instanceof Container)
                {
                    enableTree(children[i], enable);
                }
                children[i].setEnabled(enable);
            }
        }
        comp.setEnabled(enable);
    }

    /**
     * This method automatically adds the contents of a data structured refered
     * to by an iterator to a <code>JComboBox</code> instance.
     * 
     * @param combo
     *            a <code>JComboBox</code> instance
     * @param it
     *            iterator to the contents to be added to the combo
     */
    public static void addToCombo(JComboBox combo, Iterator it)
    {
        while (it.hasNext())
        {
            combo.addItem(it.next());
        }
    }

    /**
     * This method makes it easy (and also neat) creating a <code>JLabel</code>
     * and adding it to its container class.
     * <p>
     * The <code>BorderLayout</code> of the container class must be set to
     * <code>null</code>.
     * <p>
     * This method makes the label autosized. This is done according to the
     * container's font.
     * 
     * @param c
     *            the container
     * @param caption
     *            the caption (text) of the label
     * @param x
     *            x of its upper left corner
     * @param y
     *            y of its upper left corner
     * @return a <code>JLabel</code> instance
     */
    public static JLabel createJLabel(Container c, String caption, int x, int y)
    {
        FontMetrics fm = c.getFontMetrics(c.getFont());
        JLabel lbl = new JLabel(caption);
        lbl.setBounds(x, y, fm.stringWidth(caption) + 20, 20);
        c.add(lbl);
        return lbl;
    }

    /**
     * This method makes it easy (and also neat) creating a
     * <code>JTextField</code> and adding it to its container class.
     * <p>
     * The <code>BorderLayout</code> of the container class must be set to
     * <code>null</code>.
     * 
     * @param c
     *            the container
     * @param text
     *            the text to be shown
     * @param x
     *            x of its upper left corner
     * @param y
     *            y of its upper left corner
     * @param width
     *            width of the component
     * @return a <code>JTextField</code> instance
     */
    public static JTextField createJTextField(Container c, String text, int x,
            int y, int width)
    {
        JTextField txt = new JTextField(text);
        txt.setBounds(x, y, width, 20);
        c.add(txt);
        return txt;
    }

    /**
     * This method makes it easy (and also neat) creating a <code>JButton</code>
     * and adding it to its container class.
     * <p>
     * The <code>BorderLayout</code> of the container class must be set to
     * <code>null</code>.
     * 
     * @param c
     *            the container
     * @param caption
     *            the caption (text) of the control
     * @param x
     *            x of its upper left corner
     * @param y
     *            y of its upper left corner
     * @param width
     *            width of the component
     * @param height
     *            height of the component
     * @param al
     *            the <code>ActionListener</code> of the button, set it
     *            <code>null</code> if you will do it manually later.
     * @return a <code>JButton</code> instance
     */
    public static JButton createJButton(Container c, String caption, int x,
            int y, int width, int height, ActionListener al)
    {
        JButton btn = new JButton(caption);
        btn.setBounds(x, y, width, height);
        if (al != null)
            btn.addActionListener(al);

        c.add(btn);
        return btn;
    }

    /**
     * This method makes it easy (and also neat) creating a
     * <code>JCheckBox</code> and adding it to its container class.
     * <p>
     * The <code>BorderLayout</code> of the container class must be set to
     * <code>null</code>.
     * <p>
     * This method makes the check-box autosized. This is done according to the
     * container's font.
     * 
     * @param c
     *            the container
     * @param caption
     *            the caption (text) of the control
     * @param x
     *            x of its upper left corner
     * @param y
     *            y of its upper left corner
     * @param compToDisable
     *            The component to be disabled and enabled automatically when
     *            the check-box is checked and unchecked. Pass <code>null</code>
     *            to skip it.
     * @return a <code>JCheckBox</code> instance
     */
    public static JCheckBox creteJCheckBox(Container c, String caption, int x,
            int y, final Component compToDisable)
    {
        JCheckBox cb = new JCheckBox(caption);
        FontMetrics fm = c.getFontMetrics(c.getFont());
        cb.setBounds(x, y, fm.stringWidth(caption) + 20, 20);
        c.add(cb);

        if (compToDisable != null)
        {
            cb.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Util.enableTree(compToDisable, ((JCheckBox) e.getSource())
                            .isSelected());
                }
            });

            compToDisable.setEnabled(cb.isSelected());
        }

        return cb;
    }
}
