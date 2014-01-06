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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * This class serves as the CLang Generator. It makes use of the strategy-data
 * of the strategy, and generates CLang statements according to the user's
 * options.
 * 
 * @author Sina
 * 
 */
public class CLangGenerator
{
    StrategyData   strategyData;

    /**
     * User defined condition which will be anded to the existing conditions
     */
    public String  customCondition = "";

    /**
     * Should play-mode play-on condition be generated.
     */
    public boolean addPlayon       = false;

    /**
     * Defines a condition so that if the player is closer from some certain
     * distance to the ball then it is not needed to obey positioning rules
     * (maybe it is better to intercept it). That distance is referred
     * <i>Freedom Radius</i> in this application.
     */
    public double  freedomRadius   = -1.0;

    /**
     * adds a shooting rule and a shooting region to the generated CLang if
     * true.
     */
    public boolean enableShooting  = true;

    /**
     * the prefix which will be concated to the generated positioning rules'
     * names.
     */
    public String  ruleNamePrefix  = "";

    /**
     * Defines a circle around the destination positioning point, so that the
     * player is not told to position in an exact point. Rather he is told to
     * position to a point inside a radius around that point. That radius is
     * referred <i>Positioning Radius</i> in this application.
     * <p>
     * Defining a positioning radius is not needed when you're using Standard
     * Coachable Players (rcsscoachable).
     */
    public double  posRadius       = -1.0;

    /**
     * The name of the file that the CLang statements will be saved in.
     */
    private String fileName        = "";

    public CLangGenerator(StrategyData st, String fileName)
    {
        strategyData = st;
        this.fileName = fileName;
    }

    public void generateCLang() throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        defineRegions(bw);

        Util.writeLine(bw, "");
        Util
                .writeLine(bw,
                        "(say (define (definec \"cnd_nonplayons\" (not (playm play_on)) )))");
        Util.writeLine(bw, "");

        defineHomePositionings(bw);
        Util.writeLine(bw, "");

        generatePositionings(bw);

        Util.writeLine(bw, "# A Simple Passing");
        Util
                .writeLine(
                        bw,
                        "(say (define (definerule RULE_GENERAL_PASSING direc ((true) (do our {0} (pass {0})) ))))");
        Util.writeLine(bw, "");
        Util.writeLine(bw, "# Play when it is not play on");
        Util
                .writeLine(
                        bw,
                        "(say (define (definerule RULE_PASS_NONPLAYONS direc (\"cnd_nonplayons\" (do our {0} (pass {0}))) )))");
        Util.writeLine(bw, "");

        if (enableShooting)
        {
            Util.writeLine(bw, "# A Simple Shooting");
            Util
                    .writeLine(
                            bw,
                            "(say (define (definerule RULE_SHOOT direc ((and (bowner our {X}) (bpos \"CACR_ShootingReg\")) (do our {X} (shoot))) )))");
            Util.writeLine(bw, "");
        }

        Util.writeLine(bw, "");
        Util.writeLine(bw, "(say (rule (on all)))");

        bw.close();
    }

    private void generatePositionings(BufferedWriter bw) throws IOException
    {
        boolean bFRadius = freedomRadius > 0.0;
        boolean bCustom = customCondition.length() > 0;
        boolean isCndEmpty = !addPlayon && !bFRadius && !bCustom;
        boolean bPrefix = ruleNamePrefix.length() > 0;
        boolean bPosRadius = posRadius > 0.0;

        Util.writeLine(bw, "# Positionings");

        Iterator<String> it = strategyData.getPartitionNamesIterator();
        String partName = "";
        Coefs4d cs = null;
        String strLine = "";
        while (it.hasNext())
        {
            partName = it.next();
            Util.writeLine(bw, "# " + partName);
            for (int i = 0; i < 11; ++i)
            {
                if (strategyData.partitionExistsForPlayer(i, partName))
                {
                    cs = strategyData.getPlayerCoefs(i, partName);
                    strLine = "(say (define (definerule RULE_";

                    if (bPrefix)
                        strLine += ruleNamePrefix;

                    strLine += partName;
                    if ((i + 1) < 10)
                        strLine += "0";
                    strLine += i + 1;
                    strLine += " direc (";

                    if (isCndEmpty)
                    {
                        strLine += "(bpos \"" + partName + "\")";
                    }
                    else
                    {
                        strLine += "(and ";
                        if (addPlayon)
                            strLine += "(playm play_on)";

                        strLine += "(bpos \"" + partName + "\")";

                        if (bFRadius)
                            strLine += "(not (bpos (arc (pt our " + (i + 1)
                                    + ") 0 " + Util.roundDefault(freedomRadius)
                                    + " 0 360 )))";

                        if (bCustom)
                            strLine += customCondition;

                        strLine += ") ";
                    }

                    if (bPosRadius)
                    {
                        strLine += "(do our {" + (i + 1) + "} (pos (arc "
                                + cs.toCLang();
                        strLine += " 0 " + posRadius + " 0 360 ))) ))))";
                    }
                    else
                    {
                        strLine += "(do our {" + (i + 1) + "} (pos "
                                + cs.toCLang();
                        strLine += " )) ))))";
                    }
                    Util.writeLine(bw, strLine);
                }
            }
            Util.writeLine(bw, "");
        }
    }

    /**
     * Generates home positionings for the players.
     * <p>
     * The home position of the players are derived from the players' positions
     * when the ball is in the center of the field. Then the results are maped
     * to points with x ranging from -5 to -30.
     * <p>
     * If the player has no positionings assigned to, when the ball is in the
     * center of the field, then there's no home positionings generated for that
     * player.
     * 
     * @param bw
     *            The buffered Writer to write to
     * @throws IOException
     */
    private void defineHomePositionings(BufferedWriter bw) throws IOException
    {
        // First see which partition contains center
        double x = 0.0, y = 0.0;
        double[] homeXs = new double[11], homeYs = new double[11];

        boolean found = false;
        String strRegName = "";
        Iterator<String> it = strategyData.getPartitionNamesIterator();
        while (it.hasNext())
        {
            strRegName = it.next();
            if (Util.FieldRect(strategyData.getRectForRegion(strRegName))
                    .contains(x, y))
            {
                found = true;
                break;
            }
        }

        if (!found)
            return;

        for (int i = 0; i < 11; ++i)
        {
            if (strategyData.partitionExistsForPlayer(i, strRegName))
            {
                Coefs4d cs = strategyData.getPlayerCoefs(i, strRegName);
                homeXs[i] = x * cs.c1 + cs.o1;
                homeYs[i] = y * cs.c2 + cs.o2;
            }
        }

        // find min and max x
        double minx = 20000, maxx = -20000;

        for (int i = 1; i < 11; ++i)
        {
            if (homeXs[i] < minx)
                minx = homeXs[i];
            if (homeXs[i] > maxx)
                maxx = homeXs[i];
        }

        // map them from -30 to -5
        double dist = maxx - minx;
        for (int i = 1; i < 11; ++i)
        {
            homeXs[i] -= maxx + 5;
            homeXs[i] *= 25.0f / dist;
        }

        // Now generate the rule
        Util.writeLine(bw, "# Home Positionings");
        String strLine = "(say (define (definerule RULE_HOMES direc ((true) ";

        for (int i = 1; i < 11; ++i)
        {
            strLine += "(do our {" + (i + 1) + "} (home (pt "
                    + Util.roundDefault(homeXs[i]) + " "
                    + Util.roundDefault(homeYs[i]) + "))) ";
        }

        strLine += " ) ) ) )";
        Util.writeLine(bw, strLine);
        Util.writeLine(bw, "");
    }

    private void defineRegions(BufferedWriter bw) throws IOException
    {
        Util.writeLine(bw, "# Regions Definitions");

        Iterator<String> it = strategyData.getRegionNamesIterator();
        String regName;
        while (it.hasNext())
        {
            regName = it.next();
            String strLine = "";
            strLine += "(say (define (definer \""
                    + regName
                    + "\" "
                    + Util.FieldRect(strategyData.getRectForRegion(regName))
                            .toCLang() + " )))";

            Util.writeLine(bw, strLine);
        }

        if (enableShooting)
        {
            Util.writeLine(bw, "");
            Util.writeLine(bw, "# CACR: Coach Assistant Created Region");
            Util
                    .writeLine(bw,
                            "(say (define (definer \"CACR_ShootingReg\" (rec (pt 40 -18)(pt 52.5 18)) )))");
        }
    }
}
