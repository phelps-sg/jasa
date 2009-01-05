/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

/**
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class HelpPane implements HyperlinkListener {

  private JEditorPane ep;// = new JEditorPane();
  private JFrame frame;

  private Vector urls = new Vector(3);
  private int urlPointer = 0;
  private URL currentURL = null;

  private JButton right = new JButton(new ImageIcon("d:/home/nick/src/uchicago/src/sim/images/Right.gif"));
  private JButton left = new JButton(new ImageIcon("d:/home/nick/src/uchicago/src/sim/images/Left.gif"));
  private JButton exit = new JButton(new ImageIcon("d:/home/nick/src/uchicago/src/sim/images/Delete.gif"));

  public HelpPane() {
    try {
      currentURL = new URL("file:///d:/home/nick/repast/docs/how_to/how_to.html");
      urls.add(currentURL);
      ep = new JEditorPane(currentURL);
      ep.setEditable(false);
      ep.addHyperlinkListener(this);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    JScrollPane sp = new JScrollPane(ep);

    frame = new JFrame("Repast Help");
    Container c = frame.getContentPane();
    c.setLayout(new BorderLayout());
    c.add(sp, BorderLayout.CENTER);

    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        exit();
      }
    });

    JToolBar bar = new JToolBar();
    bar.setFloatable(false);
    bar.add(left);
    bar.add(right);
    bar.add(exit);
    left.setEnabled(false);
    right.setEnabled(false);

    c.add(bar, BorderLayout.NORTH);
    addListeners();
  }

  public void display() {
    if (!frame.isVisible()) {
      frame.setSize(500, 400);
      frame.setVisible(true);
    } else if (frame.getState() == JFrame.ICONIFIED) {
      frame.setState(JFrame.NORMAL);
    } else {
      frame.requestFocus();
    }
  }

  private void addListeners() {
    left.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        urlPointer--;
        gotoLink((URL)urls.get(urlPointer));
        if (urlPointer == 0) {
          left.setEnabled(false);
        }
        right.setEnabled(true);
      }
    });

    right.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        urlPointer++;
        gotoLink((URL)urls.get(urlPointer));
        if (urlPointer == urls.size() - 1) {
          right.setEnabled(false);
        }

        left.setEnabled(true);
      }
    });

    exit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        exit();
      }
    });
  }

  private void exit() {
    frame.dispose();
    System.exit(0);
  }

  // HyperlinkListener interface
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
	    Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	    ep.setCursor(handCursor);
    } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
      ep.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      ep.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      urls.add(e.getURL());
      gotoLink(e.getURL());
      ep.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      urlPointer++;
      for (int i = urlPointer + 1; i < urls.size(); i++) {
        urls.removeElementAt(i);
      }
      right.setEnabled(false);

    }
	}

  private void gotoLink(URL url) {
    if (url != null) {
      Document doc = ep.getDocument();
      try {
        ep.setPage(url);
        currentURL = url;
        if (urls.size() > 1) {
          left.setEnabled(true);
        }
      } catch (Exception ex) {
        ep.setDocument(doc);
      }
    }
  }

  public static void main(String[] args) {
    HelpPane p = new HelpPane();
    p.display();
  }
}
