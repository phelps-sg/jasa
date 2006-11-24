/*
 * JASA Java Auction Simulator API Copyright (C) 2001-2005 Steve Phelps
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package uk.ac.liv.auction.ui;

import javax.swing.*;

import uk.ac.liv.util.ObjectConverter;
import uk.ac.liv.util.Parameterizable;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Defines a JFrame whose properties can be customized by parameters.
 * 
 * <p><b>Parameters</b><br>
 *
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.title</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top></td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.x</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the x location of the left-top corner of the frame)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.y</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the y location of the left-top corner of the frame)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.width</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the width of the frame)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.height</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the height of the frame)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.background</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the background color of the frame)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.foreground</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the foreground color of the frame)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.font</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the font of the frame)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.icon</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the file path of the icon for the frame)</td></tr>
 * 
 * </table>
 *
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class UserFrame extends JFrame implements Parameterizable {

  public static final String P_TITLE = "title";

  public static final String P_FONT = "font";

  public static final String P_BACKGROUND = "background";

  public static final String P_FOREGROUND = "foreground";

  public static final String P_X = "x";

  public static final String P_Y = "y";

  public static final String P_WIDTH = "width";

  public static final String P_HEIGHT = "height";

  public static final String P_ICON = "icon";

//  private static Logger logger = Logger.getLogger(UserFrame.class);

  public void setup(ParameterDatabase parameters, Parameter base) {

//    this.base = base;

    setTitle(parameters.getStringWithDefault(base.push(P_TITLE), ""));

    Font font = ObjectConverter
        .getFont(parameters.getString(base.push(P_FONT)));
    if (font != null)
      setFont(font);

    Color color = ObjectConverter.getColor(parameters.getString(base
        .push(P_BACKGROUND)));
    if (color != null)
      setBackground(color);

    color = ObjectConverter.getColor(parameters.getString(base
        .push(P_FOREGROUND)));
    if (color != null)
      setForeground(color);

    String s1 = getDimName();
    int i = parameters.getIntWithDefault(base.push(s1).push(P_X), base
        .push(P_X), 20);
    int j = parameters.getIntWithDefault(base.push(s1).push(P_Y), base
        .push(P_Y), 20);
    int k = parameters.getIntWithDefault(base.push(s1).push(P_WIDTH), base
        .push(P_WIDTH), 1024);
    int l = parameters.getIntWithDefault(base.push(s1).push(P_HEIGHT), base
        .push(P_HEIGHT), 800);
    setBounds(i, j, k, l);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exit();
      }
    });

    try {
      Image image = ObjectConverter.getImage(parameters.getString(base
          .push(P_ICON)));
      setIconImage(image);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private String getDimName() {
    Dimension dimension = getToolkit().getScreenSize();
    return dimension.width + "x" + dimension.height;
  }

  /**
   * called when the window is being closed.
   */  
  public void exit() {
    setVisible(false);
  }

  public void centerMe() {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension dimension1 = getSize();
    setLocation((dimension.width - dimension1.width) / 2,
        (dimension.height - dimension1.height) / 2);
  }

}