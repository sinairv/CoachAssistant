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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

/**
 * This class provides the data-structures needed to hold all the data used to
 * store a strategy.
 * <p>
 * Note also that all rects in all the data-structures are in global
 * coordination.
 * <p>
 * See also <code>IStrategyDataListener</code>.
 * <p>
 * Any change in the data stored here will cause a call of its proper method in
 * listeners that implemented <code>IStrategyDataListener</code>.
 * 
 * @author Sina
 * 
 */
public class StrategyData
{
    /**
     * A map from a region name to its rectangle
     */
    private HashMap<String, Rect4d>  mapName2Reg     = new HashMap<String, Rect4d>();
    /**
     * The set of partition names
     */
    private HashSet<String>          setPartitions   = new HashSet<String>();
    /**
     * Array of 11 maps used for all the 11 of <i>our</i> players.
     * <p>
     * Each element is a map from a partition name to a <code>Coefs4d</code>
     * instance. Knowing these 2 together, leads to defining the position of the
     * player when the ball is inside that partition.
     */
    private HashMap[]                playersCoefs    = new HashMap[11];

    /**
     * a list of listeners to the changes made to the data structures.
     * <p>
     * The listeners must have implemented the
     * <code>IStrategyDataListener</code> interface.
     */
    ArrayList<IStrategyDataListener> listOfListeners = new ArrayList<IStrategyDataListener>();

    /**
     * The Constructor. :D
     */
    public StrategyData()
    {
        // instantiate the 11 maps
        for (int i = 0; i < 11; ++i)
        {
            playersCoefs[i] = new HashMap();
        }
    }

    /**
     * Adds a listener that will listen for changes made in the strategy data
     * structures.
     * <p>
     * The listeners must have implemented the
     * <code>IStrategyDataListener</code> interface.
     * 
     * @param listener
     *            an implementation of the <code>IStrategyDataListener</code>
     *            interface
     */
    public void addListener(IStrategyDataListener listener)
    {
        listOfListeners.add(listener);
    }

    /**
     * Is called whenever any change in <i>any</i> of the data-structures are
     * made. It calls <code>IStrategyDataListener.OnStrategyChanged()</code>
     * of the listeners in return.
     * <p>
     * Every data-structure has its own special event. But all of them will call
     * this method additionaly.
     */
    private void raiseStrategyChanged()
    {
        Iterator<IStrategyDataListener> it = listOfListeners.iterator();
        while (it.hasNext())
        {
            it.next().OnStrategyChanged();
        }
    }

    /**
     * Is called whenever any change in the regions hashmap are made. It calls
     * <code>IStrategyDataListener.OnRegionsChanged()</code> of the listeners
     * in return.
     */
    private void raiseRegionsChanged()
    {
        Iterator<IStrategyDataListener> it = listOfListeners.iterator();
        while (it.hasNext())
        {
            it.next().OnRegionsChanged();
        }

        raiseStrategyChanged();
    }

    /**
     * Is called whenever any change in the partitions hash-set are made. It
     * calls <code>IStrategyDataListener.OnPartitionsChanged()</code> of the
     * listeners in return.
     */
    private void raisePartitionsChanged()
    {
        Iterator<IStrategyDataListener> it = listOfListeners.iterator();
        while (it.hasNext())
        {
            it.next().OnPartitionsChanged();
        }

        raiseStrategyChanged();
    }

    /**
     * Is called whenever any change in any of the players coefficients are
     * made. It calls <code>IStrategyDataListener.OnCoefsChanged()</code> of
     * the listeners in return.
     */
    private void raiseCoefsChanged()
    {
        Iterator<IStrategyDataListener> it = listOfListeners.iterator();
        while (it.hasNext())
        {
            it.next().OnCoefsChanged();
        }

        raiseStrategyChanged();
    }

    /**
     * Clears all the data-structures.
     */
    public void clear()
    {
        mapName2Reg.clear();
        setPartitions.clear();
        for (int i = 0; i < 11; ++i)
        {
            playersCoefs[i].clear();
        }
    }

    /**
     * Adds a new region
     * 
     * @param regName
     *            The name of the region
     * @param rc
     *            the rectangle specifying the region in the System-GUI
     *            coordination (global)
     */
    public void addRegion(String regName, Rect4d rc)
    {
        mapName2Reg.put(regName, rc);
        raiseRegionsChanged();
    }

    /**
     * deletes a region
     * 
     * @param regName
     *            the name of the region to be deleted
     */
    public void removeRegion(String regName)
    {
        mapName2Reg.remove(regName);
        raiseRegionsChanged();
        removeRegFromPartition(regName);
    }

    /**
     * returns true if there is such region
     * 
     * @param regName
     *            the name of the region
     */
    public boolean regionExists(String regName)
    {
        return mapName2Reg.containsKey(regName);
    }

    /**
     * returns the <code>Rect4d</code> instance of a region
     * 
     * @param regName
     *            the region name
     * @return rectangle bounds of the region in global coordination
     */
    public Rect4d getRectForRegion(String regName)
    {
        return mapName2Reg.get(regName);
    }

    /**
     * Adds (declares) an existing region as a partition
     * 
     * @param regName
     *            the name of the region
     */
    public void addRegToPartition(String regName)
    {
        setPartitions.add(regName);
        raisePartitionsChanged();
    }

    /**
     * Declares that an existing region is no longer a partition
     * 
     * @param regName
     *            the name of the region
     */
    public void removeRegFromPartition(String regName)
    {
        setPartitions.remove(regName);
        raisePartitionsChanged();
    }

    /**
     * returns true if there is such partition
     * 
     * @param regName
     *            the name of the region
     */
    public boolean isRegInPartition(String regName)
    {
        return setPartitions.contains(regName);
    }

    /**
     * returns true if there are no partitions
     */
    public boolean isPartitionsEmpty()
    {
        return setPartitions.isEmpty();
    }

    /**
     * 
     * @param index
     *            is the player's 0-based index that is its unum - 1
     * @param regName
     *            the name of the ball region
     * @param cs
     *            the coefficients according which the player must position
     */
    @SuppressWarnings("unchecked")
    public void setCoefForPlayer(int index, String regName, Coefs4d cs)
    {
        playersCoefs[index].put(regName, cs);
        raiseCoefsChanged();
    }

    /**
     * returns true if there is a positioning (i.e. coeffitient) defined for a
     * player for a partition.
     * 
     * @param index
     *            the 0-based index of the player
     * @param regName
     *            the name of the partition
     */
    public boolean partitionExistsForPlayer(int index, String regName)
    {
        return playersCoefs[index].containsKey(regName);
    }

    /**
     * returns the existing coefficients defined for a player for a partition
     * 
     * @param index
     *            the 0-based index of the player
     * @param regName
     *            the name of the partition
     */
    public Coefs4d getPlayerCoefs(int index, String regName)
    {
        return (Coefs4d) playersCoefs[index].get(regName);
    }

    /**
     * returns an iterator referring to the beginning of a set containing the
     * name of all the regions
     */
    public Iterator<String> getRegionNamesIterator()
    {
        return mapName2Reg.keySet().iterator();
    }

    /**
     * returns an iterator referring to the beginning of a set containing the
     * name of the partitions
     */
    public Iterator<String> getPartitionNamesIterator()
    {
        return setPartitions.iterator();
    }

    /**
     * Reads the whole strategy from a .cas file. It makes use of the
     * <code>CASParser</code> class.
     * 
     * @param fileName
     *            the name of the .cas file
     * @throws IOException
     */
    public void readFromFile(String fileName) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        clear();

        CASParser parser = new CASParser(this, br);
        parser.parse(); // :D

        br.close();
    }

    /**
     * Saves the contents of the data-structure into a .cas file
     * 
     * @param fileName
     *            the name of the file
     * @throws IOException
     */
    public void saveToFile(String fileName) throws IOException
    {
        BufferedWriter br = new BufferedWriter(new FileWriter(fileName));

        // first save regions
        Util.writeLine(br, "[" + Params.HeaderRegions + "]");
        Iterator<String> it = mapName2Reg.keySet().iterator();
        String key;
        while (it.hasNext())
        {
            key = it.next();
            Util.writeLine(br, key + "  "
                    + (Util.FieldRect(mapName2Reg.get(key))).toString());
        }

        // second save partitions
        Util.writeLine(br, "[" + Params.HeaderPartitions + "]");
        it = setPartitions.iterator();
        while (it.hasNext())
        {
            Util.writeLine(br, it.next());
        }
        // third save players
        char chUnum = '?';
        for (int i = 0; i < 11; ++i)
        {
            if (0 <= i && i <= 8)
                chUnum = ("" + (i + 1)).charAt(0);
            else if (i == 9)
                chUnum = 'A';
            else if (i == 10)
                chUnum = 'B';

            Util.writeLine(br, "[" + Params.HeaderPlayers + chUnum + "]");

            Iterator iter = playersCoefs[i].keySet().iterator();
            String regName;
            Coefs4d cs;
            while (iter.hasNext())
            {
                regName = (String) iter.next();
                cs = (Coefs4d) playersCoefs[i].get(regName);
                Util.writeLine(br, regName + "  " + cs.toString());
            }
        }

        br.close();
    }
}

/**
 * This class parses the contents of a <code>BufferedReader</code> and stores
 * the result into a <code>StrategyData</code> instance.
 * <p>
 * It assumes that the file is in a .cas file format, otherwise the behaviour of
 * the parser is unknown.
 * 
 * @author Sina
 */
class CASParser
{
    BufferedReader br;
    StrategyData   strategyData;
    Headers        currentHeader = null;
    int            playerIndex   = -1;

    public CASParser(StrategyData sd, BufferedReader br)
    {
        strategyData = sd;
        this.br = br;
    }

    public void parse() throws IOException
    {
        String line = br.readLine();

        while (line != null)
        {
            if (!parseLine(line))
                JOptionPane.showMessageDialog(null, "Error in: " + line);
            line = br.readLine();
        }
    }

    private boolean parseLine(String str)
    {
        StringTokenizer st = new StringTokenizer(str, "[],# \t\r\n", true);

        String token;
        while (st.hasMoreTokens())
        {
            token = st.nextToken();
            if (Util.isWhiteSpace(token))
                continue;
            if (token.compareTo("#") == 0)
                return true;

            if (token.compareTo("[") == 0)
            {
                return parseSegmentHeader(st);
            }

            char ch = token.charAt(0);
            if (Character.isLetterOrDigit(ch) || ch == '_')
            {
                return parseSegmentContent(token, st);
            }
        }

        return true;
    }

    private boolean parseSegmentContent(String token, StringTokenizer st)
    {
        switch (currentHeader)
        {
            case Partitions:
                if (strategyData.regionExists(token))
                    strategyData.addRegToPartition(token);
                return true;
            case Regions:
            case Players:
            {
                ArrayList<Double> listParams = new ArrayList<Double>();

                String str;
                while (st.hasMoreTokens())
                {
                    str = st.nextToken();
                    if (Util.isWhiteSpace(str))
                        continue;
                    try
                    {
                        listParams.add(new Double(Double.parseDouble(str)));
                    }
                    catch (NumberFormatException ex)
                    {
                        return false;
                    }
                }

                if (listParams.size() != 4)
                    return false;

                if (currentHeader == Headers.Regions)
                {
                    strategyData.addRegion(token, Util.GlobalRect(new Rect4d(
                            listParams.get(0).doubleValue(), listParams.get(1)
                                    .doubleValue(), listParams.get(2)
                                    .doubleValue(), listParams.get(3)
                                    .doubleValue())));
                }
                else
                {
                    if (strategyData.isRegInPartition(token))
                    {
                        strategyData.setCoefForPlayer(playerIndex, token,
                                new Coefs4d(listParams.get(0).doubleValue(),
                                        listParams.get(1).doubleValue(),
                                        listParams.get(2).doubleValue(),
                                        listParams.get(3).doubleValue()));
                    }
                }
                return true;
            }
            default:
                return false;
        }
    }

    private boolean parseSegmentHeader(StringTokenizer st)
    {
        String token;
        if (st.hasMoreTokens())
        {
            token = st.nextToken();

            if (token.compareTo(Params.HeaderRegions) == 0)
            {
                currentHeader = Headers.Regions;
                return true;
            }
            else if (token.compareTo(Params.HeaderPartitions) == 0)
            {
                currentHeader = Headers.Partitions;
                return true;
            }
            else if (token.startsWith(Params.HeaderPlayers))
            {
                currentHeader = Headers.Players;

                // PlayerA
                // 0123456
                int ch = -1;
                if (token.length() >= 7)
                    ch = token.charAt(6);

                if (ch == 'A')
                {
                    playerIndex = 9;
                    return true;
                }
                else if (ch == 'B')
                {
                    playerIndex = 10;
                    return true;
                }

                int nch = Character.getNumericValue(ch)
                        - Character.getNumericValue('0');

                if (1 <= nch && nch <= 9)
                {
                    playerIndex = nch - 1;
                    return true;
                }
            }
        }
        currentHeader = null;
        return false;
    }

    /**
     * The 3 headers of a typical .cas file
     * 
     * @author Sina
     */
    private enum Headers
    {
        Regions, Partitions, Players
    }
}
