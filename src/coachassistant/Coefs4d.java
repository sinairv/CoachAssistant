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
 * This class is used to represent the coefficients for defining a player's
 * positionings.
 * <p>
 * The coefficients are named c1, c2, and offsets as o1, and o2.
 * <p>
 * Consider that the ball's position is shown by x, and y. Then the player's
 * position is calculated accordingly using following formulas:
 * <p>
 * player's x = x * c1 + o1
 * <p>
 * player's y = y * c2 + o2
 * 
 * @author Sina
 * 
 */
public class Coefs4d
{
    public double c1, c2, o1, o2;

    public Coefs4d(double c1, double c2, double o1, double o2)
    {
        this.c1 = c1;
        this.c2 = c2;
        this.o1 = o1;
        this.o2 = o2;
    }

    public String toString()
    {
        return "" + c1 + "  " + c2 + "  " + o1 + "  " + o2;
    }

    public String toCLang()
    {
        return "(((pt ball) * (pt  " + Util.roundTo(c1, 2) + "  "
                + Util.roundTo(c2, 2) + ")) + (pt  " + Util.roundTo(o1, 2)
                + "  " + Util.roundTo(o2, 2) + "))";
    }
}
