/**
 * *##% JRst
 * Copyright (C) 2004 - 2008 CodeLutin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##%*
 */
/**
 * numerotation des lignes
 * 
 */
package org.codelutin.jrst;

/*
 *
 * Created on 1 aout 2005, 17:45
 *
 *  Copyright (C) 2005 Yves Zoundi
 *  MOdified by Jason Davis
 *  
 *  This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2.1 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

/**
 * A class which adds line numbering to a editor
 * 
 * @author Yves Zoundi
 */
public final class GutterColor extends JLabel {
    /** serialVersionUID */
    private static final long serialVersionUID = -4419253280014127529L;

    private JTextComponent edit;
    private int rhWidth = 40;
    boolean[] bColors;

    /**
     * 
     * @param edit
     *            the editor which has to display line numbers
     */
    public GutterColor(JTextComponent edit, JScrollPane pane, boolean[] bColors) {
        this.edit = edit;
        this.bColors = bColors;
        // listen for font change
        edit.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("font")) {
                    revalidate();
                    repaint();
                }
            }
        });

        setBackground(Color.GRAY);
        setForeground(Color.GRAY.darker());

        pane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        revalidate();
                        repaint();
                    }
                });
    }

    /**
     * 
     * @return The preferred size of the line numbering column
     */
    public Dimension getPreferredSize() {
        FontMetrics fm = edit.getFontMetrics(edit.getFont());
        int h = fm.getHeight();
        int w = fm.stringWidth(String.valueOf(edit.getHeight() / h)) + 6;

        rhWidth = w;
        int hi = (int) edit.getPreferredSize().getHeight();
        return new Dimension(w, hi);
    }

    /**
     * 
     * @param g
     *            A graphic component
     */
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        g2d.setFont(edit.getFont());
        FontMetrics fm = g2d.getFontMetrics();

        Rectangle rec = edit.getVisibleRect();

        int h = fm.getHeight();
        int row = rec.y / h;
        int i = h - fm.getDescent();
        i += h * row;
        int max = row + (rec.height / h) + 2;

        boolean doWhite = false;
        while (row < max) {
            String s = Integer.toString(row + 1) + "  ";
            boolean white = false;
            if (bColors.length > row + 1) {
                if (bColors[row + 1]) {
                    g2d.setColor(Color.RED);
                    g2d.fill3DRect(1, i + 3, rhWidth - 4, 15, false);
                    white = true;

                } else
                    white = false;
            } else
                white = false;
            if (doWhite) {
                g2d.setColor(Color.WHITE);
                g2d.drawString(s, (rhWidth + 9) - fm.stringWidth(s), i);
            } else {
                g2d.setColor(Color.GRAY.darker());
                g2d.drawString(s, (rhWidth + 9) - fm.stringWidth(s), i);
            }
            doWhite = white;

            i += h;
            row++;
        }

        g2d.setColor(Color.GRAY.darker());
        g2d.drawLine(getWidth() - 3, rec.y, getWidth() - 3, rec.height + rec.y);
    }
}
