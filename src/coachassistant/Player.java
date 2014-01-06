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

/**
 * This class is for logical representation of a player
 * 
 * @author Sina
 * 
 */
public class Player
{
    /**
     * The player's uniform number
     */
    public int     unum  = -1;

    /**
     * Which team does it belong to? our or opp?
     */
    public boolean isOur = true;

    public int     x     = 10;
    public int     y     = 10;

    Player(int n, boolean our, int xx, int yy)
    {
        unum = n;
        isOur = our;
        x = xx;
        y = yy;
    }
}
