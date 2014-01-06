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

import java.awt.Color;

/**
 * This class defines all the parameters needed globally in the application, as
 * static final constants.
 * 
 * @author Sina
 */
public class Params
{
    /**
     * Extension of the applications' strategy files
     */
    public final static String  AppFilesExt      = "cas";
    /**
     * The name of a new file
     */
    public final static String  NewFileName      = "untitiled." + AppFilesExt;

    /*
     * The Literals for the three headers building a typical .cas file
     */
    public final static String  HeaderRegions    = "REGIONS";
    public final static String  HeaderPartitions = "PARTITIONS";
    public final static String  HeaderPlayers    = "PLAYER";

    /*
     * The colors used in the GUI
     */
    public final static Color   ColorPitch       = Color.GREEN;
    public final static Color   ColorOurPlayer   = Color.YELLOW;
    public final static Color   ColorOppPlayer   = Color.CYAN;
    public static final Color   BorderColor      = Color.BLACK;
    public static final Color   ColorLine        = Color.WHITE;

    public static final Color   ColorBallReg     = Color.BLUE;
    public static final Color   ColorPlayerReg   = Color.RED;

    /**
     * Show the X player?
     */
    public static final boolean ShowXPlayer      = true;

    public static final int     PlayerRadius     = 11;

    /**
     * A double value for an illegal value
     */
    public static final double  IllegalDouble    = -1000.0;

    /**
     * The number of digits after the decimal points used for all double values
     * that are going to be shown to the user, or saved in a human-readable
     * file.
     */
    public static final int     Precision        = 2;

    /**
     * Scale (i.e. zooming factor) of showing the field (pitch). e.g. if it
     * equals 7, then every 7 pixels represent one meter.
     */
    public static final double  dFieldScale      = 7.0;

    // Half of all the area, including the field and the area around it
    public static final double  dHalfWidth       = 58.0;
    public static final double  dHalfHeight      = 39.0;

    public static final double  dFieldWidth      = 52.5;
    public static final double  dFieldHeight     = 34.0;

    public static final double  dGoalRegX        = 47.0;
    public static final double  dGoalRegY        = 9.0;

    public static final double  dDangerX         = 36.0;
    public static final double  dDangerY         = 20.0;

    public static final double  dGoalWidth       = 7.0;
    public static final double  dMidRad          = 9.15;
}
