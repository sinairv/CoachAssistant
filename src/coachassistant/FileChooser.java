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

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * This class is a wrapper around JFileChooser, with customized filters.
 * 
 * @author Sina
 * 
 */
public class FileChooser
{

    private JFileChooser fc     = new JFileChooser("");

    /**
     * The filter to be used by the JFileChooser
     */
    Filter               filter = new Filter();

    public FileChooser()
    {
        fc.addChoosableFileFilter(filter);
    }

    /**
     * Shows an open-file dialog
     * 
     * @return the selected file name in canonical form, or an empty string if
     *         the operation has some errors or has been canceled.
     */
    public String openFileDialog()
    {
        if (fc.getSelectedFile() != null)
        {
            fc.setSelectedFile(new File(Util.fileNameNoExt(fc.getSelectedFile()
                    .getName())));
        }

        int result = fc.showOpenDialog(null);
        switch (result)
        {
            case JFileChooser.APPROVE_OPTION:
                try
                {
                    return fc.getSelectedFile().getCanonicalPath();
                }
                catch (IOException ex)
                {
                    return "";
                }
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                return "";
        }
    }

    /**
     * Shows a save-file dialog
     * 
     * @return the selected file name in canonical form, or an empty string if
     *         the operation has some errors or has been canceled.
     */
    public String saveFileDialog()
    {
        if (fc.getSelectedFile() != null)
        {
            fc.setSelectedFile(new File(Util.fileNameNoExt(fc.getSelectedFile()
                    .getName())));
        }

        int result = fc.showSaveDialog(null);
        switch (result)
        {
            case JFileChooser.APPROVE_OPTION:
                try
                {
                    String name = fc.getSelectedFile().getCanonicalPath();
                    if (!name.endsWith(filter.ext))
                        name += filter.ext;

                    if (new File(name).exists())
                    {
                        int r = JOptionPane.showConfirmDialog(null, "\"" + name
                                + "\"\nalready exists. Replace?", "Replace?",
                                JOptionPane.YES_NO_OPTION);
                        if (r == JOptionPane.YES_OPTION)
                            return name;
                        else
                            return "";
                    }
                    return name;
                }
                catch (IOException ex)
                {
                    return "";
                }
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                return "";
        }
    }

    /**
     * Generally sets a filter for the file-chooser.
     * 
     * @param ext
     *            file extension
     * @param des
     *            description for that extension
     */
    public void setFilter(String ext, String des)
    {
        filter.setFilter(ext, des);
    }

    /**
     * Sets the .clang filter
     */
    public void setCLangFilter()
    {
        setFilter(".clang", "CLang File (*.clang)");
    }

    /**
     * Sets the .cas filter
     */
    public void setCASFilter()
    {
        setFilter("." + Params.AppFilesExt, "Coach Assistant Strategy (*.cas)");
    }
}

/**
 * This class is served as a filter for the FileChooser class.
 * 
 * @author Sina
 * 
 */
class Filter extends FileFilter
{
    public String ext = ".txt";
    public String des = "Text files (*.txt)";

    public Filter()
    {
    }

    public Filter(String ext, String des)
    {
        this.ext = ext;
        this.des = des;
    }

    public void setFilter(String ext, String des)
    {
        this.ext = ext;
        this.des = des;
    }

    /**
     * Causes to accept only directories and files ending with the specified
     * extension
     */
    @Override
    public boolean accept(File f)
    {
        if (f.isDirectory())
            return true;
        return f.getName().toLowerCase().endsWith(ext.toLowerCase());
    }

    @Override
    public String getDescription()
    {
        return des;
    }
}
