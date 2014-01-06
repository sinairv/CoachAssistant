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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

/**
 * This class is the GUI representation of the field (pitch).
 * 
 * @author Sina
 * 
 */
public class Pitch extends JPanel implements MouseListener, MouseMotionListener
{
    private static final long serialVersionUID        = -4605320772696082604L;

    ArrayList<Player>         listOfPlayers           = new ArrayList<Player>();

    /**
     * a reference to the <code>CoachAssistant</code> instance.
     */
    CoachAssistant            coachAssistant          = null;

    /**
     * a reference to the unique <code>StrategyData</code> instance of the
     * CoachAssistant.
     */
    StrategyData              strategyData            = null;

    /**
     * 0-based index of the player which is right-clicked
     */
    public int                rightClickedPlayerIndex = -1;

    /**
     * is the user dragging the mouse for drawing the ball region
     */
    public boolean            isDrawingBall           = false;

    /**
     * is the user dragging the mouse for drawing the player region
     */
    public boolean            isDrawingPlayer         = false;

    /**
     * the current ball region specified by the user
     */
    public Rect4d             rectBall                = null;

    /**
     * the current player region specified by the user
     */
    public Rect4d             rectPlayer              = null;

    /**
     * is the user moving the ball, so that the players need to position
     * accordingly
     */
    private boolean           isMovingBall            = false;

    public Pitch(CoachAssistant ca)
    {
        coachAssistant = ca;
        strategyData = ca.strategyData;
        setPreferredSize(new Dimension(
                (int) (Params.dFieldScale * 2 * Params.dHalfWidth),
                (int) (Params.dFieldScale * 2 * Params.dHalfHeight)));

        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        addMouseListener(this);
        addMouseMotionListener(this);

        setBackground(Params.ColorPitch);

        createPlayers();
        lineupPlayers();
    }

    private void createPlayers()
    {
        listOfPlayers.clear();
        int i;
        for (i = 1; i <= 11; ++i)
            listOfPlayers.add(new Player(i, true, 1, 1));

        for (i = 1; i <= 11; ++i)
            listOfPlayers.add(new Player(i, false, 1, 1));

        if (Params.ShowXPlayer)
        {
            listOfPlayers.add(new Player(-1, true, 1, 1));
            listOfPlayers.add(new Player(-1, false, 1, 1));
        }
    }

    /**
     * resets everything to its initial state
     * 
     */
    public void clear()
    {
        isDrawingBall = false;
        isDrawingPlayer = false;
        rectBall = null;
        rectPlayer = null;
        isMovingBall = false;

        lineupPlayers();
    }

    /**
     * arranges the players on a line on top of the pitch
     */
    public void lineupPlayers()
    {
        int sour = -50, sopp = 6;
        Player ourp, oppp;
        for (int i = 0; i < 11; ++i)
        {
            ourp = listOfPlayers.get(i);
            oppp = listOfPlayers.get(11 + i);

            ourp.y = oppp.y = (int) Math.round(Util.GlobalY(-36));
            ourp.x = (int) Math.round(Util.GlobalX(sour + i * 4));
            oppp.x = (int) Math.round(Util.GlobalX(sopp + i * 4));
        }

        if (Params.ShowXPlayer)
        {
            ourp = listOfPlayers.get(22);
            oppp = listOfPlayers.get(23);
            ourp.y = oppp.y = (int) Math.round(Util.GlobalY(-36));
            ourp.x = (int) Math.round(Util.GlobalX(sour + 11 * 4));
            oppp.x = (int) Math.round(Util.GlobalX(sopp + 11 * 4));
        }

        repaint();
    }

    // / these variables are needed for double-buffering
    Dimension offscreensize = null;
    Image     offscreen     = null;
    Graphics  offgraphics   = null;
    Image     pitchImage    = null;

    public synchronized void paint(Graphics g)
    {
        Dimension d = getSize();
        if ((offscreen == null) || (d.width != offscreensize.width)
                || (d.height != offscreensize.height))
        {
            offscreen = createImage(d.width, d.height);
            offscreensize = d;
            if (offgraphics != null)
            {
                offgraphics.dispose();
            }
            offgraphics = offscreen.getGraphics();
            offgraphics.setFont(getFont());

            pitchImage = null;
        }

        drawPitch(offgraphics);

        if (rectBall != null)
        {
            offgraphics.setColor(Params.ColorBallReg);
            rectBall.draw(offgraphics);
        }

        if (rectPlayer != null)
        {
            offgraphics.setColor(Params.ColorPlayerReg);
            rectPlayer.draw(offgraphics);
        }

        if (isDrawingPlayer || isDrawingBall)
        {
            offgraphics.setColor(isDrawingBall ? Params.ColorBallReg
                    : Params.ColorPlayerReg);
            drawSRect(offgraphics, drSX, drSY, drEX, drEY);
        }

        FontMetrics fm = offgraphics.getFontMetrics();
        Iterator<Player> it = listOfPlayers.iterator();
        while (it.hasNext())
        {
            drawPlayer(it.next(), offgraphics, fm);
        }

        g.drawImage(offscreen, 0, 0, null);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    private void drawPlayer(Player p, Graphics g, FontMetrics fm)
    {
        int r = Params.PlayerRadius;

        g.setColor(Params.BorderColor);
        g.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);

        g.setColor(p.isOur ? Params.ColorOurPlayer : Params.ColorOppPlayer);
        g.fillOval(p.x - r + 1, p.y - r + 1, 2 * r - 2, 2 * r - 2);

        String playerCaption = "" + p.unum;
        if (p.unum == 10)
        {
            playerCaption = "A";
        }
        else if (p.unum == 11)
        {
            playerCaption = "B";
        }
        else if (p.unum > 11 || p.unum < 0)
        {
            playerCaption = "X";
        }

        int w = fm.stringWidth(playerCaption);
        int h = fm.getHeight();
        g.setColor(Params.BorderColor);
        g.drawString(playerCaption, p.x - w / 2, p.y - h / 2 + fm.getAscent());
    }

    private void drawPitch(Graphics gr)
    {
        if (pitchImage == null)
        {
            Dimension d = getSize();
            pitchImage = createImage(d.width, d.height);

            Graphics g = pitchImage.getGraphics();
            g.setFont(getFont());

            g.setColor(Params.ColorPitch);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Params.ColorLine);
            drawSRect(g, Util.GlobalX(-Params.dFieldWidth), Util
                    .GlobalY(-Params.dFieldHeight), Util
                    .GlobalX(Params.dFieldWidth), Util
                    .GlobalY(Params.dFieldHeight));

            drawSRect(g, Util.GlobalX(-Params.dFieldWidth), Util
                    .GlobalY(-Params.dDangerY), Util.GlobalX(-Params.dDangerX),
                    Util.GlobalY(Params.dDangerY));

            drawSRect(g, Util.GlobalX(Params.dFieldWidth), Util
                    .GlobalY(-Params.dDangerY), Util.GlobalX(Params.dDangerX),
                    Util.GlobalY(Params.dDangerY));

            drawSRect(g, Util.GlobalX(Params.dFieldWidth), Util
                    .GlobalY(-Params.dGoalRegY),
                    Util.GlobalX(Params.dGoalRegX), Util
                            .GlobalY(Params.dGoalRegY));

            drawSRect(g, Util.GlobalX(-Params.dFieldWidth), Util
                    .GlobalY(-Params.dGoalRegY), Util
                    .GlobalX(-Params.dGoalRegX), Util.GlobalY(Params.dGoalRegY));

            drawSEllipse(g, Util.GlobalX(-Params.dMidRad), Util
                    .GlobalY(-Params.dMidRad), Util.GlobalX(Params.dMidRad),
                    Util.GlobalY(Params.dMidRad));

            fillSEllipse(g, Util.GlobalX(-0.5), Util.GlobalY(-0.5), Util
                    .GlobalX(0.5), Util.GlobalY(0.5));

            g.drawLine((int) Math.round(Util.GlobalX(0)), (int) Math.round(Util
                    .GlobalY(-Params.dFieldHeight)), (int) Math.round(Util
                    .GlobalX(0)), (int) Math.round(Util
                    .GlobalY(Params.dFieldHeight)));

            g.setColor(Params.BorderColor);

            fillSRect(g, Util.GlobalX(-Params.dFieldWidth), Util
                    .GlobalY(-Params.dGoalWidth), Util
                    .GlobalX(-Params.dFieldWidth - 1), Util
                    .GlobalY(Params.dGoalWidth));

            fillSRect(g, Util.GlobalX(Params.dFieldWidth), Util
                    .GlobalY(-Params.dGoalWidth), Util
                    .GlobalX(Params.dFieldWidth + 1), Util
                    .GlobalY(Params.dGoalWidth));
        }
        gr.drawImage(pitchImage, 0, 0, null);
    }

    /**
     * fills a rect using the coordination of its four corners (not the upper
     * lefr corner and the width and height of it).
     */
    private void fillSRect(Graphics g, double x1, double y1, double x2,
            double y2)
    {
        int _x1 = (int) Math.round(Math.min(x1, x2));
        int _x2 = (int) Math.round(Math.max(x1, x2));
        int _y1 = (int) Math.round(Math.min(y1, y2));
        int _y2 = (int) Math.round(Math.max(y1, y2));
        g.fillRect(_x1, _y1, _x2 - _x1, _y2 - _y1);
    }

    private void fillSEllipse(Graphics g, double x1, double y1, double x2,
            double y2)
    {
        int _x1 = (int) Math.round(Math.min(x1, x2));
        int _x2 = (int) Math.round(Math.max(x1, x2));
        int _y1 = (int) Math.round(Math.min(y1, y2));
        int _y2 = (int) Math.round(Math.max(y1, y2));
        g.fillOval(_x1, _y1, _x2 - _x1, _y2 - _y1);
    }

    private void drawSEllipse(Graphics g, double x1, double y1, double x2,
            double y2)
    {
        int _x1 = (int) Math.round(Math.min(x1, x2));
        int _x2 = (int) Math.round(Math.max(x1, x2));
        int _y1 = (int) Math.round(Math.min(y1, y2));
        int _y2 = (int) Math.round(Math.max(y1, y2));
        g.drawOval(_x1, _y1, _x2 - _x1, _y2 - _y1);
    }

    private void drawSRect(Graphics g, double x1, double y1, double x2,
            double y2)
    {
        int _x1 = (int) Math.round(Math.min(x1, x2));
        int _x2 = (int) Math.round(Math.max(x1, x2));
        int _y1 = (int) Math.round(Math.min(y1, y2));
        int _y2 = (int) Math.round(Math.max(y1, y2));
        g.drawRect(_x1, _y1, _x2 - _x1, _y2 - _y1);
    }

    /**
     * If the user is drawing a region resets needed variables and calls
     * repaint.
     * <p>
     * Else if the user is dragging a player changes its position and calls
     * repaint
     */
    public void mouseDragged(MouseEvent e)
    {
        if (isDrawingPlayer || isDrawingBall)
        {
            drEX = e.getX();
            drEY = e.getY();
            repaint();
        }
        else
        {
            if (selectedPlayer != null)
            {
                int newx = e.getX() - offx;
                int newy = e.getY() - offy;

                if (newx < 0)
                    newx = 0;
                if (newy < 0)
                    newy = 0;

                if (newx > getWidth())
                    newx = getWidth();
                if (newy > getHeight())
                    newy = getHeight();

                selectedPlayer.x = newx;
                selectedPlayer.y = newy;
                repaint();
            }
        }
        coachAssistant.setCoord(e.getX(), e.getY());
    }

    /**
     * Calls CoachAssistant to show the coordinations. If the user is moving the
     * ball, changes the position of the players accordingly, and calls repaint.
     */
    public synchronized void mouseMoved(MouseEvent e)
    {
        coachAssistant.setCoord(e.getX(), e.getY());

        if (isMovingBall)
        {
            for (int i = 0; i < 11; ++i)
            {
                Point2d pt = getMappedPointForPlayer(i, e.getX(), e.getY());
                if (pt != null)
                {
                    listOfPlayers.get(i).x = (int) Math.round(pt.x);
                    listOfPlayers.get(i).y = (int) Math.round(pt.y);
                }
            }
            repaint();
        }
    }

    /**
     * returns the position of a player, having the current position of the
     * ball.
     * <p>
     * returns null if no positioning is assigned to the player for the current
     * ball's position.
     * 
     * @param i
     *            0-based index of the player
     * @param x
     *            x of the ball in global coordination
     * @param y
     *            y of the ball in global coordination
     */
    private Point2d getMappedPointForPlayer(int i, int x, int y)
    {
        Iterator<String> it = strategyData.getPartitionNamesIterator();
        String regName = "";
        Coefs4d cs = null;

        while (it.hasNext())
        {
            regName = it.next();
            if (strategyData.getRectForRegion(regName).contains(x, y))
            {
                if (strategyData.partitionExistsForPlayer(i, regName))
                {
                    cs = strategyData.getPlayerCoefs(i, regName);
                    break;
                }
            }
        }

        if (cs != null)
        {
            double rx = Util.GlobalX(Util.FieldX(x) * cs.c1 + cs.o1);
            double ry = Util.GlobalY(Util.FieldY(y) * cs.c2 + cs.o2);
            return new Point2d(rx, ry);
        }

        return null;
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    Player selectedPlayer = null;
    int    offx           = 0, offy = 0;
    int    drSX           = 0, drSY = 0, drEX = 0, drEY = 0;

    /**
     * Starts drawings and Ends moving the ball. Selects a player if clicked on.
     */
    public void mousePressed(MouseEvent e)
    {
        int mx = e.getX();
        int my = e.getY();

        if (isMovingBall)
        {
            coachAssistant.finishedMovingBall();
        }
        else if (isDrawingBall || isDrawingPlayer)
        {
            drSX = drEX = mx;
            drSY = drEY = my;
        }
        else
        {
            Player p = getPlayerIn(mx, my);

            if (p != null)
            {
                selectedPlayer = p;
                offx = mx - p.x;
                offy = my - p.y;
            }
        }

        e.consume();
    }

    /**
     * Returns the player whose circle includes the given point. Returns null if
     * there is no such player.
     */
    public Player getPlayerIn(int x, int y)
    {
        Player p = null;
        for (int i = listOfPlayers.size() - 1; i >= 0; --i)
        {
            p = listOfPlayers.get(i);

            if ((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y) <= Params.PlayerRadius
                    * Params.PlayerRadius)
            {
                return p;
            }
        }

        return null;
    }

    /**
     * Ends drawings, and pops up players' pop-up menus if clicked on.
     */
    public void mouseReleased(MouseEvent e)
    {
        selectedPlayer = null;

        if (isDrawingBall || isDrawingPlayer)
        {
            Rect4d r = new Rect4d(drSX, drSY, drEX, drEY);

            if (isDrawingBall)
                setBallRect(r);
            else
                setPlayerRect(r);

            repaint();

            coachAssistant.finishedDrawing();
        }

        if (e.isPopupTrigger())
        {
            Player p = getPlayerIn(e.getX(), e.getY());
            if (p != null && p.isOur && p.unum >= 1 && p.unum <= 11)
            {
                rightClickedPlayerIndex = p.unum - 1;
                coachAssistant.popupPlayersMenu(p.unum, e.getX(), e.getY());
            }
        }

        isDrawingBall = isDrawingPlayer = false;
    }

    /**
     * Sets the player's rectangle in global coordination, and requests a show
     * of coefficients in the status bar.
     */
    public void setPlayerRect(Rect4d r)
    {
        rectPlayer = r;
        coachAssistant.showCoefs();
        repaint();
    }

    /**
     * Sets the player's rectangle, using the ball's rectangle (in the field
     * coordination) and the player's coefficients.
     * 
     * @param rcBall
     *            Ball's rectangle in the field coordination
     */
    public void setPlayerRect(Rect4d rcBall, Coefs4d coefs)
    {
        double x1 = rcBall.x1 * coefs.c1 + coefs.o1;
        double x2 = rcBall.x2 * coefs.c1 + coefs.o1;
        double y1 = rcBall.y1 * coefs.c2 + coefs.o2;
        double y2 = rcBall.y2 * coefs.c2 + coefs.o2;

        setPlayerRect(Util.GlobalRect(new Rect4d(x1, y1, x2, y2)));
    }

    /**
     * Sets the ball's rectangle in global coordination, and requests a show of
     * coefficients in the status bar.
     */
    public void setBallRect(Rect4d r)
    {
        rectBall = r;
        coachAssistant.showCoefs();
        repaint();
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * call this, to make this class aware that the user wants to move the ball
     */
    public void setBallMoveMode()
    {
        isMovingBall = true;
    }

    /**
     * call this, to make this class aware that the user finished moving the
     * ball
     */
    public void unsetBallMoveMode()
    {
        isMovingBall = false;
    }
}

/**
 * A helper class to represent a point consisting of 2 doubles
 * 
 * @author Sina
 */
class Point2d
{
    public double x = 0.0, y = 0.0;

    Point2d(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
}
