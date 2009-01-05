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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import layout.TableLayout;
import uchicago.src.guiUtils.GuiUtilities;
import uchicago.src.sim.engine.gui.ParameterWizard;
import uchicago.src.sim.engine.gui.components.ParameterData;
import uchicago.src.sim.util.Disassembler;
import uchicago.src.sim.util.SimUtilities;


/**
 * Dialog for loading a model. Called when the user clicks on the
 * folder icon in the gui controller. This will load all the appropriate
 * jars and SimModels in repast/demo as well as any jars or classes in
 * repast/models. Other classes or jars can be loaded via the add button.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class LoadModelDialog implements ParameterFileListener {
	private JDialog dialog = null;
	private LoadModelTree tree = new LoadModelTree();
	private JLabel lblName = new JLabel();
	private JLabel lblPath = new JLabel();
	private JTextField txtParamFile = new JTextField();
	private JLabel lblModelClass = new JLabel();
	private JButton btnParamBrowse = new JButton("...");
	private JButton btnAdd = new JButton("Add");
	private JButton load = new JButton("Load");
	private JButton cancel = new JButton("Cancel");
	private JButton parameterWizardButton = new JButton("Parameter Wizard");
	private JPanel mainPanel;
	private String modelsDir;
	private String demoDir;
	private String demoJar;
	private SimModel selectedModel = null;
	private HashSet demoExclude = new HashSet();
	private Comparator modelComp = new ModelComparator();
	private CLoader loader;

	/**
	 * Creates a LoadModelDialog.
	 */
	public LoadModelDialog() {
		loader = new CLoader(new URL[] {  }, this.getClass().getClassLoader());

		demoExclude.add("uchicago.src.repastdemos.heatBugs.HBNoGui");
		demoExclude.add("uchicago.src.repastdemos.enn.EnnBase");
		demoExclude.add("uchicago.src.repastdemos.enn.EnnBatchModel");
		demoExclude.add("uchicago.src.repastdemos.schellingGis.SchellingGis");

		guiInit();
		addListeners();
		findPaths();

		try {
			if (demoDir != null) {
				loadDemos();
			}

			if (modelsDir != null) {
				loadModels();
			}
		} catch (IOException ex) {
			SimUtilities.showError("Error creating LoadModelDialog", ex);
		}
	}

	private void guiInit() {
		//		  Listen for when the selection changes.
		tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					if (tree.isSelectionEmpty()) {
						parameterWizardButton.setEnabled(false);
					} else {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

						if (node == null) {
							parameterWizardButton.setEnabled(false);

							return;
						}

						Object nodeInfo = node.getUserObject();

						if (nodeInfo instanceof uchicago.src.sim.engine.SimModel) {
							parameterWizardButton.setEnabled(true);
						} else {
							parameterWizardButton.setEnabled(false);
						}
					}
				}
			});

		JPanel bottom = new JPanel();
		bottom.add(load);
		bottom.add(cancel);
		mainPanel = new JPanel(new BorderLayout());

		int bHeight = btnAdd.getPreferredSize().height;
		double[][] sizes = {
							   { 4, .4, 6, .6, 4 },
							   { 4, TableLayout.FILL, 2, bHeight, 4 }
						   };
		JPanel topPanel = new JPanel(new TableLayout(sizes));
		topPanel.add(btnAdd, "1, 3");
		topPanel.add(new JScrollPane(tree), "1, 1");
		topPanel.setBorder(BorderFactory.createEtchedBorder());

		int textHeight = txtParamFile.getPreferredSize().height;
		double[][] s = {
						   { .4, 1, .5, 4, .1 },
						   {
							   textHeight, 6, textHeight, 6, textHeight, 6,
							   textHeight, 2, textHeight, 2, textHeight, 2,
							   textHeight
						   }
					   };
		JPanel right = new JPanel(new TableLayout(s));
		topPanel.add(right, "3, 1, 3, 3");
		right.add(new JLabel("Name:"), "0, 0");

		right.add(lblName, "2, 0, l, f");
		right.add(new JLabel("Model Class:"), "0, 2");
		right.add(lblModelClass, "2, 2, 4, 2");
		right.add(new JLabel("Class Path:"), "0, 4");
		right.add(lblPath, "2, 4, 4, 4");
		right.add(new JLabel("Parameter File:"), "0, 6");
		right.add(txtParamFile, "0, 8, 2, 8");
		right.add(btnParamBrowse, "4, 8");

		right.add(new JLabel("or use:"), "0, 10");
		right.add(parameterWizardButton, "2, 10");
		right.add(new JLabel("To activate the wizard, you need to select a simulation."),
				  "0, 12, 4, 12");
		parameterWizardButton.setEnabled(false);
		mainPanel.add(bottom, BorderLayout.SOUTH);
		mainPanel.add(topPanel, BorderLayout.CENTER);

		btnAdd.setToolTipText("Add a class or jar file");
	}

	private void addListeners() {
		cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					selectedModel = null;
					exit();
				}
			});

		load.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					exit();
				}
			});

		tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					TreePath path = e.getPath();
					ModelTreeNode node = (ModelTreeNode) path.getLastPathComponent();

					if (node.isModelNode()) {
						selectedModel = (SimModel) node.getUserObject();
						lblName.setText(selectedModel.getName());

						String cname = selectedModel.getClass().getName();
						int index = cname.lastIndexOf(".");

						if (index != -1) {
							cname = cname.substring(index + 1, cname.length());
						}

						lblModelClass.setText(cname);
						txtParamFile.setText("");
					}
				}
			});

		btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
							public boolean accept(File pathname) {
								String name = pathname.getName();

								return pathname.isDirectory() ||
									   name.endsWith(".jar") ||
									   name.endsWith(".class");
							}

							public String getDescription() {
								return "Jar and Class Files (*.jar, *.class)";
							}
						});
					chooser.setDialogType(JFileChooser.OPEN_DIALOG);

					int retVal = chooser.showOpenDialog(dialog);

					if (retVal == JFileChooser.APPROVE_OPTION) {
						File f = chooser.getSelectedFile();

						if (f.getName().endsWith(".jar")) {
							try {
								loader.addPath(f.getCanonicalPath());

								ArrayList modelList = addSimModelFromJar(new JarFile(f));

								for (int i = 0; i < modelList.size(); i++) {
									SimModel model = (SimModel) modelList.get(i);
									tree.addOtherModel(model);
								}
							} catch (IOException ex) {
								SimUtilities.showError("Error loading selected file\n" +
													   ex.getMessage(), ex);

								return;
							}
						} else if (f.getName().endsWith(".class")) {
							SimModel model = loadClass(f, true);

							if (model != null) {
								tree.addOtherModel(model);
							}
						}
					}
				}
			});

		btnParamBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					JFileChooser chooser = new JFileChooser();
					String path = txtParamFile.getText();

					if (path.length() > 0) {
						chooser.setCurrentDirectory(new File(path));
					}

					chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
							public boolean accept(File pathname) {
								String name = pathname.getName();

								return pathname.isDirectory() ||
									   name.endsWith(".pf");
							}

							public String getDescription() {
								return "Parameter files (*.pf)";
							}
						});

					chooser.setDialogType(JFileChooser.OPEN_DIALOG);

					int retVal = chooser.showOpenDialog(dialog);

					if (retVal == JFileChooser.APPROVE_OPTION) {
						File f = chooser.getSelectedFile();
						txtParamFile.setText(f.getAbsolutePath());
					}
				}
			});

		parameterWizardButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					displayParameterWizard();
				}
			});
	}

	private String findRepastHome(String classPath) {
		String path = null;

		if (classPath == null) {
			return path;
		}

		StringTokenizer tok = new StringTokenizer(classPath, File.pathSeparator);

		while (tok.hasMoreTokens()) {
			path = tok.nextToken();

			if (path.endsWith(".jar")) {
				// look for a Class-Path attribute in that jar
				File jarPath = new File(path);

				if (jarPath.exists()) {
					try {
						JarFile jf = new JarFile(jarPath);
						Manifest m = jf.getManifest();

						if (m != null) {
							String jfClassPath = m.getMainAttributes().getValue("Class-Path");

							if (jfClassPath != null) {
								StringTokenizer pathTok = new StringTokenizer(jfClassPath,
																			  " ");

								while (pathTok.hasMoreTokens()) {
									path = pathTok.nextToken();

									if (path.indexOf("repastj.jar") != -1) {
										if (new File(path).exists()) {
											return path;
										}
									} else {
										path = null;
									}
								}
							}
						}
					} catch (IOException ex) {
					}
				}
			}
		}

		return path;
	}

	/*
	 * Finds the model (repast/models) and demo directories (repast/demo)
	 * by finding the repast.jar in the classpath.
	 */
	private void findPaths() {
		String classPath = System.getProperty("java.class.path");
		String path = null;
		StringTokenizer tok = new StringTokenizer(classPath, File.pathSeparator);

		while (tok.hasMoreTokens()) {
			path = tok.nextToken();

			if (path.indexOf("repastj.jar") != -1) {
				//path = "./" + path;
				break;
			} else {
				path = null;
			}
		}

		if (path == null) {
			path = findRepastHome(classPath);

			if (path == null) {
				SimUtilities.showMessage("Cannot find repast home directory");

				return;
			}
		}
		
		File dir = new File(path);
		try {
			dir = dir.getCanonicalFile();
		} catch (IOException ex) {
			SimUtilities.showMessage("Cannot find repast home directory");

			return;
		}

		path = dir.getParent();
		File f = new File(path + File.separator + "models");

		if (!f.exists()) {
			//SimUtilities.showMessage("Cannot find models directory (repast/models");
		} else {
			modelsDir = f.getAbsolutePath();
		}

		f = new File(path + File.separator + "demo");
		if (f.exists()) {
			demoDir = f.getAbsolutePath();
		}
		f = new File(path + File.separator + "repast-demos.jar");
		demoJar = f.getAbsolutePath();
	}

	/*
	 * Loads the jars in the various demo sub directories into the
	 * demo node on the load model tree.
	 */
	private void loadDemos() throws IOException {
		File dir = new File(demoDir);
		File[] subDirs = dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});

		ArrayList demos = new ArrayList();
		for (int i = 0; i < subDirs.length; i++) {
			demos.addAll(getSimModels(subDirs[i]));
		}
		File demoFile = new File(demoJar);
		if(demoFile.exists()){
			loader.addPath(demoJar);
			JarFile jar = new JarFile(demoJar);
			demos.addAll(addSimModelFromJar(jar));
		}
		Collections.sort(demos, modelComp);
		tree.addDemos(demos);
	}

	/*
	 * Loads any jars and classes in the models directory into the
	 * models node in the load model tree.
	 */
	private void loadModels() throws IOException {
		File dir = new File(modelsDir);
		ArrayList models = new ArrayList();
		models.addAll(getSimModels(dir));
		findClasses(new File(modelsDir).listFiles(), models);
		Collections.sort(models, modelComp);
		tree.addModels(models);
	}

	/**
	 * Finds any classes that implement SimModel in the specified File[]. If
	 * there are any directories in this array  those directories
	 * are searched as well and so on with their sub-directories as well until
	 * the directory trees have been searchred. The SimModels are loaded into
	 * the specified ArrayList.
	 *
	 * @param children the array of files and / or directories to search
	 * @param models   the ArrayList into which any SimModels are put
	 */
	private void findClasses(File[] children, ArrayList models) {
		for (int i = 0; i < children.length; i++) {
			File f = children[i];

			if (f.isDirectory()) {
				findClasses(f.listFiles(), models);
			} else {
				if (f.getName().endsWith(".class")) {
					SimModel m = loadClass(f, false);

					if (m != null) {
						models.add(m);
					}
				}
			}
		}
	}

	/**
	 * Returns an ArrayList of loaded and instantiated SimModels. These are
	 * loaded from any jar files in the specified directory.
	 */
	private ArrayList getSimModels(File dir) throws IOException {
		File[] files = dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".jar");
				}
			});

		ArrayList list = new ArrayList();

		for (int i = 0; i < files.length; i++) {
			loader.addPath(files[i].getAbsolutePath());

			JarFile jar = new JarFile(files[i]);
			list.addAll(addSimModelFromJar(jar));
		}

		return list;
	}

	/**
	 * Loads and instantiates the specified class file as a SimModel. If
	 * showMessage is true this will show message if the specified class file
	 * is not a SimModel.
	 */
	private SimModel loadClass(File classfile, boolean showMessage) {
		try {
			Disassembler dis = new Disassembler(classfile);
			String name = dis.getFQClassName().replace('/', '.');
			String pathName = name.replace('.', File.separatorChar);
			String path = classfile.getAbsolutePath();
			int index = path.lastIndexOf(pathName);

			if (index != -1) {
				path = path.substring(0, index);
				loader.addPath(path);

				Class c = loader.loadClass(name);

				if (isSimModel(c)) {
					return (SimModel) c.newInstance();
				} else if (showMessage) {
					SimUtilities.showMessage(name + " is not a SimModel");
				}
			}
		} catch (IOException ex) {
			SimUtilities.showError("Error loading selected file\n" +
								   ex.getMessage(), ex);
		} catch (ClassNotFoundException ex) {
			SimUtilities.showError("Error loading selected file\n" +
								   ex.getMessage(), ex);
		} catch (InstantiationException ex) {
			SimUtilities.showError("Error loading selected file\n" +
								   ex.getMessage(), ex);
		} catch (IllegalAccessException ex) {
			SimUtilities.showError("Error loading selected file\n" +
								   ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * Returns a list of any SimModels found in the specified jar file.
	 */
	private ArrayList addSimModelFromJar(JarFile jar) throws IOException {
		ArrayList list = new ArrayList();

		Manifest manifest = jar.getManifest();

		if (manifest != null) {
			Attributes attrb = manifest.getMainAttributes();
			String classPath = attrb.getValue("Class-Path");

			if (classPath != null) {
				StringTokenizer tok = new StringTokenizer(classPath, " ");

				while (tok.hasMoreTokens()) {
					File f = new File(tok.nextToken());
					loader.addPath(f.getCanonicalPath());
				}
			}
		}

		Enumeration e = jar.entries();

		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();

			if (entry.getName().endsWith(".class")) {
				String name = entry.getName().substring(0,
														entry.getName().length() -
														6);
				name = name.replace('/', '.');

				if (!demoExclude.contains(name)) {
					try {
						Class c = loader.loadClass(name);

						if (isSimModel(c)) {
							SimModel sm = (SimModel) c.newInstance();
							list.add(sm);
						}
					} catch (ClassNotFoundException ex) {
						SimUtilities.showError("Error loading jar " +
											   jar.getName() + "\n" +
											   ex.getMessage(), ex);
					} catch (InstantiationException ex) {
						SimUtilities.showError("Error loading jar " +
											   jar.getName() + "\n" +
											   ex.getMessage(), ex);
					} catch (IllegalAccessException ex) {
						SimUtilities.showError("Error loading jar " +
											   jar.getName() + "\n" +
											   ex.getMessage(), ex);
					}
				}
			}
		}

		return list;
	}

	/**
	 * Checks if the specified Class or any of its parent classes implement SimModel.
	 */
	private boolean isSimModel(Class c) {
		while (c != null) {
			if (simModelCheck(c)) {
				return true;
			}

			c = c.getSuperclass();
		}

		return false;
	}

	/**
	 * Checks if the specified Class implements SimModel
	 */
	private boolean simModelCheck(Class c) {
		Class[] interfaces = c.getInterfaces();

		for (int i = 0; i < interfaces.length; i++) {
			if (interfaces[i].getName().equals("uchicago.src.sim.engine.SimModel")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Closes this LoadModelDialog
	 */
	private void exit() {
		dialog.dispose();

		//Dimension d = dialog.getSize();
		//System.out.println(d);
		//System.out.println(d);
	}

	/**
	 * Displays this LoadModelDialog.
	 *
	 * @param f the parent JFrame for this LoadModelDialog
	 */
	public void display(JFrame f) {
		dialog = new JDialog(f, "Load Model", true);
		dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
					//Dimension d = dialog.getSize();
					//System.out.println(d.width + ", " + d.height);
					selectedModel = null;
					exit();
				}
			});

		Container c = dialog.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(mainPanel, BorderLayout.CENTER);

		dialog.setSize(500, 308);
		GuiUtilities.centerComponentOnScreen(dialog);
		dialog.setVisible(true);
	}

	/**
	 * Returns the selected SimModel if any, otherwise null.
	 */
	public SimModel getModel() {
		Class clazz = null;

		if (selectedModel != null) {
			try {
				clazz = selectedModel.getClass();
				selectedModel = (SimModel) clazz.newInstance();
			} catch (InstantiationException ex) {
				SimUtilities.showError("Error loading class " +
									   clazz.getName() + "\n" +
									   ex.getMessage(), ex);
			} catch (IllegalAccessException ex) {
				SimUtilities.showError("Error loading class " +
									   clazz.getName() + "\n" +
									   ex.getMessage(), ex);
			}
		}

		return selectedModel;
	}

	/**
	 * Returns the parameter file name as a String.
	 */
	public String getParameterFile() {
		return txtParamFile.getText();
	}

	/**
	 * Returns the parameter file name as a String.
	 */
	public void setParameterFile(String fileName) {
		txtParamFile.setText(fileName);
	}

	/**
	 * @return Returns the dialog.
	 */
	public JDialog getDialog() {
		return dialog;
	}

	/**
	 *
	 */
	private void displayParameterWizard() {
		ParameterWizard aParameterWizard = new ParameterWizard(this);
		boolean loadFromTheModel = false;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node != null) {
			ParameterData aParameterData = null;

			if ((txtParamFile.getText() != null) &&
					!txtParamFile.getText().equals("")) {
				try {
					aParameterData = new ParameterData(txtParamFile.getText());
				} catch (Exception ex) {
					SimUtilities.showError("Error loading parameters from " +
										   txtParamFile.getText() + "\n" +
										   ex.getMessage() +
										   "\n\nWill initialize using model information.",
										   ex);
					aParameterData = new ParameterData();
					loadFromTheModel = true;
				}
			} else {
				aParameterData = new ParameterData();
				loadFromTheModel = true;
			}

			if (loadFromTheModel) {
				Object nodeInfo = node.getUserObject();

				aParameterData = new ParameterData((SimModel) nodeInfo);
			}
    	    
	    	aParameterData.reloadInputOutput();
	    	aParameterWizard.setAParameterData(aParameterData);
	    	aParameterWizard.display();
		}
	}

	/**
	 * Compares two SimModels using the results of SimModel.getName()
	 */
	class ModelComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			SimModel m1 = (SimModel) o1;
			SimModel m2 = (SimModel) o2;

			return m1.getName().compareTo(m2.getName());
		}
	}

	/**
	 * Class loader used to the load the jars and class files of the SimModel
	 * loaded in this dialog.
	 */
	class CLoader extends URLClassLoader {
		public CLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}

		public Class createClass(byte[] data, int off, int len) {
			return super.defineClass(null, data, off, len);
		}

		public void addPath(String path) throws MalformedURLException {
			//path.replace(File.separatorChar, '/');
			File f = new File(path);
			addURL(f.toURL());
		}

		/*
		public URL findResource(final String name) {
		  System.out.println("finding resource");
		  URL url = super.findResource(name);
		  return url;
		}
		*/
	}
}
