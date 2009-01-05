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

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Tree for displaying SimModels to load into repast. The tree has three nodes.
 * One for the demos, one for the anything in repast/models and one for
 * any thing loaded via the add button.
 *
 * @version $Revision$ $Date$
 */
public class LoadModelTree extends JTree {

  private DefaultTreeModel model;
  private ModelTreeNode demoNode;
  private ModelTreeNode otherModels;
  private ModelTreeNode modelsNode;

  /**
   * Create a LoadModelTree.
   */
  public LoadModelTree() {
    setRootVisible(false);
    setShowsRootHandles(true);
    getSelectionModel().setSelectionMode(TreeSelectionModel.
                                         SINGLE_TREE_SELECTION);
    ModelTreeNode root = new ModelTreeNode("root");
    demoNode = new ModelTreeNode("Demo Models");
    modelsNode = new ModelTreeNode("Models");
    otherModels = new ModelTreeNode("Other Models");
    root.add(demoNode);
    root.add(modelsNode);
    root.add(otherModels);
    model = new DefaultTreeModel(root);
    setModel(model);
    this.setCellRenderer(new NodeRenderer());
  }

  /**
   * Adds the list of SimModels in the specified ArrayList to the demos node.
   * @param demos the list of SimModels to add
   */
  public void addDemos(ArrayList demos) {
    for (int i = 0, n = demos.size(); i < n; i++) {
      demoNode.add(new ModelTreeNode((SimModel) demos.get(i)));
    }

    if (demos.size() > 1) {
      setSelectionPath(new TreePath(new Object[]{model.getRoot(), demoNode,
                                                 demoNode.getFirstChild()}));
    }
  }

  /**
   * Adds the list of SimModels in the specified ArrayList to the models node.
   * @param models the list of SimModels to add
   */
  public void addModels(ArrayList models) {
    for (int i = 0, n = models.size(); i < n; i++) {
     modelsNode.add(new ModelTreeNode((SimModel) models.get(i)));
    }
  }

  /**
   * Adds the specified SimModel to the other models nodes.
   *
   * @param simModel the list of SimModels to add
   */
  public void addOtherModel(SimModel simModel) {
    ModelTreeNode node = new ModelTreeNode(simModel);
    otherModels.add(node);

    TreePath path = new TreePath(new Object[]{model.getRoot(), otherModels,
                                                   node});
    setSelectionPath(path);
    this.scrollPathToVisible(path);
  }

  private static class NodeRenderer extends DefaultTreeCellRenderer {

    Icon icon;

    public NodeRenderer() {
      icon = new ImageIcon(LoadModelTree.class.
                           getResource("/uchicago/src/sim/images/Object.gif"));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus) {
      JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, sel,
                                                             expanded, leaf,
                                                             row, hasFocus);
      if (((ModelTreeNode) value).isModelNode()) {
        l.setIcon(icon);
      }

      return l;
    }
  }

}
