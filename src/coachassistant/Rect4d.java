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

import java.awt.Graphics;

/**
 * This class Represents a rectangle consisting of 4 doubles.
 * <p>
 * The doubles represent the 4 corners of the rectangle.
 * <p>
 * The rectangle is normalized right after instantiation. That is its (x1, y1)
 * will represent its upper left corner, and (x2, y2) will represent its lower
 * right corner.
 * 
 * @author Sina
 * 
 */
public class Rect4d
{
    public double x1, x2, y1, y2;

    public Rect4d(double x1, double y1, double x2, double y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        normalize();
    }

    public double getWidth()
    {
        return Math.abs(x2 - x1);
    }

    public double getHeight()
    {
        return Math.abs(y2 - y1);
    }

    public void normalize()
    {
        double mx = Math.min(x1, x2);
        double my = Math.min(y1, y2);
        double Mx = Math.max(x1, x2);
        double My = Math.max(y1, y2);

        x1 = mx;
        x2 = Mx;
        y1 = my;
        y2 = My;
    }

    public boolean contains(double x, double y)
    {
        if ((x1 <= x && x <= x2) || (x2 <= x && x <= x1))
            if ((y1 <= y && y <= y2) || (y2 <= y && y <= y1))
                return true;
        return false;
    }

    public void draw(Graphics g)
    {
        g.drawRect((int) Math.round(x1), (int) Math.round(y1), (int) Math
                .round(getWidth()), (int) Math.round(getHeight()));

    }

    public String toString()
    {
        return "" + x1 + "  " + y1 + "  " + x2 + "  " + y2;
    }

    public String toCLang()
    {
        return "(rec (pt " + Util.roundTo(x1, Params.Precision) + "  "
                + Util.roundTo(y1, Params.Precision) + ") (pt "
                + Util.roundTo(x2, Params.Precision) + "  "
                + Util.roundTo(y2, Params.Precision) + ") )";
    }

}
