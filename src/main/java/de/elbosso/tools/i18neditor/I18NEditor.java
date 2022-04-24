package de.elbosso.tools.i18neditor;
//$Id$

import de.elbosso.util.Utilities;
import de.netsysit.model.tree.I18NModel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class I18NEditor extends javax.swing.JPanel implements
javax.swing.event.ListSelectionListener
{
	private final static org.slf4j.Logger CLASS_LOGGER =org.slf4j.LoggerFactory.getLogger(I18NEditor.class);
	private final static java.util.ResourceBundle i18n=java.util.ResourceBundle.getBundle("de.elbosso.tools.i18n",java.util.Locale.getDefault());
	private static final java.lang.String[] PROPERTIESSUFFIXES={"props","properties"};
	private static final java.lang.String[] JAVASUFFIXES={"bsh","java"};
	private de.netsysit.util.pattern.command.ChooseFileAction save;
	private de.netsysit.util.pattern.command.ChooseFileAction open;
	private de.netsysit.util.pattern.command.ChooseFileAction importJavaAction;
	private de.netsysit.util.pattern.command.ChooseFileAction refactor;
	private javax.swing.Action addColumn;
	private javax.swing.Action addRow;
	private javax.swing.Action newaction;
	private javax.swing.Action removeaction;
	private javax.swing.Action configAction;
	private javax.swing.Action nextblankAction;
	private javax.swing.Action prevblankAction;
	private javax.swing.JToolBar toolbar = new javax.swing.JToolBar();
	private de.netsysit.ui.components.JTreeTable treetable=new de.netsysit.ui.components.JTreeTable();
	private de.netsysit.model.tree.I18NModel model;
	private de.netsysit.util.pattern.command.CollapseSelectedTreeAction collapseSelectedTreeAction = new de.netsysit.util.pattern.command.CollapseSelectedTreeAction(treetable.getTree(),i18n.getString("I18NEditor.collapseSelectedTreeAction.text"),(de.netsysit.util.ResourceLoader.getIcon("de/netsysit/ressources/gfx/common/CollapseSelected24.gif")));
	private de.netsysit.util.pattern.command.ExpandSelectedTreeAction expandSelectedTreeAction = new de.netsysit.util.pattern.command.ExpandSelectedTreeAction(treetable.getTree(), i18n.getString("I18NEditor.expandSelectedTreeAction.text"),(de.netsysit.util.ResourceLoader.getIcon("de/netsysit/ressources/gfx/common/ExpandSelected24.gif")));
	private de.netsysit.util.pattern.command.CollapseAllTreeAction collapseTreeAction = new de.netsysit.util.pattern.command.CollapseAllTreeAction(treetable.getTree(),i18n.getString("I18NEditor.collapseTreeAction.text"),(de.netsysit.util.ResourceLoader.getIcon("de/netsysit/ressources/gfx/common/CollapseAll24.gif")));
	private de.netsysit.util.pattern.command.ExpandAllTreeAction expandTreeAction = new de.netsysit.util.pattern.command.ExpandAllTreeAction(treetable.getTree(), i18n.getString("I18NEditor.expandTreeAction.text"),(de.netsysit.util.ResourceLoader.getIcon("de/netsysit/ressources/gfx/common/ExpandAll24.gif")));
	private javax.swing.JScrollPane scroller=new javax.swing.JScrollPane(treetable);
	private Config config;
    private de.netsysit.util.beans.InterfaceFactory iFactory;

	private static java.io.File i18nConfigFile;

	public static java.io.File geti18nConfigDirectory()
	{
		if(i18nConfigFile==null)
		{
			i18nConfigFile=new java.io.File(new java.io.File(System.getProperty("user.home")),".i18n");
			if(i18nConfigFile.exists()==false)
				i18nConfigFile.mkdirs();
		}
		return i18nConfigFile;
	}
	public I18NEditor() throws Exception
	{
		super(new java.awt.BorderLayout());
		add(toolbar,java.awt.BorderLayout.NORTH);
		add(scroller);
		createActions();
		populateToolBar();
		expandTreeAction.setEnabled(false);
		collapseTreeAction.setEnabled(false);
		expandSelectedTreeAction.setEnabled(false);
		collapseSelectedTreeAction.setEnabled(false);
		expandSelectedTreeAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.expandSelectedTreeAction.tooltip"));
		collapseSelectedTreeAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.collapseSelectedTreeAction.tooltip"));
		expandTreeAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.expandTreeAction.tooltip"));
		collapseTreeAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.collapseTreeAction.tooltip"));
		try{
			java.io.InputStream is=new java.io.FileInputStream(new java.io.File(geti18nConfigDirectory(), "preferences.xml"));
			java.beans.XMLDecoder decoder=new java.beans.XMLDecoder(is);
			java.lang.Object o=decoder.readObject();
			decoder.close();
			is.close();
			if(o instanceof de.elbosso.tools.i18neditor.Config)
				config=(de.elbosso.tools.i18neditor.Config)o;
			else
				config=new de.elbosso.tools.i18neditor.Config();
		}
		catch (Throwable exp)
		{
//			EXCEPTION_LOGGER.error("Problem loading the configurations",exp);
			config=new de.elbosso.tools.i18neditor.Config();
			exp.printStackTrace();

		}
	}
	void saveConfig()
	{
		try{
			java.io.OutputStream os=new java.io.FileOutputStream(new java.io.File(geti18nConfigDirectory(), "preferences.xml"));
			java.beans.XMLEncoder encoder=new java.beans.XMLEncoder(os);
			encoder.writeObject(config);
			encoder.close();
			os.close();
		}
		catch (Throwable exp)
		{
//			EXCEPTION_LOGGER.error("Problem saving the configurations",exp);
//			config=new de.elbosso.tools.i18neditor.Config();
			exp.printStackTrace();

		}
	}
	private void createActions()
	{
		newaction=
			new javax.swing.AbstractAction(i18n.getString("I18NEditor.newaction.text"),(de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/New24.gif")))
			{;
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					newrb();
				}
			};
		newaction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.newaction.tooltip"));
		addRow=
			new javax.swing.AbstractAction(i18n.getString("I18NEditor.addRow.text"),(de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/table/RowInsertAfter24.gif")))
			{;
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					try{
					addKey();
					}catch(java.lang.Throwable t){t.printStackTrace();}
				}
			};
			addRow.setEnabled(false);
		addRow.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.addRow.tooltip"));
		addColumn=
			new javax.swing.AbstractAction(i18n.getString("I18NEditor.addColumn.text"),(de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/table/ColumnInsertAfter24.gif")))
			{;
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					addLanguage();
				}
			};
		addColumn.setEnabled(false);
		addColumn.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.addColumn.tooltip"));
		de.netsysit.util.pattern.command.FileProcessor savePropertiesClient=
			new de.netsysit.util.pattern.command.FileProcessor()
			{
				public boolean  process(java.io.File[] files)
				{
					return saveProperties(files[0]);
				}
			};
		save =new de.netsysit.util.pattern.command.ChooseFileAction(savePropertiesClient,i18n.getString("I18NEditor.save.text"), (de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Save24.gif")));
		de.netsysit.ui.filechooser.ShortcutsAccessory shortcuts = new de.netsysit.ui.filechooser.ShortcutsAccessory(save.getFilechooser(), geti18nConfigDirectory(),".I18Neditor");
		shortcuts.setPreferredSize(new java.awt.Dimension(200,250));
		de.netsysit.ui.filechooser.PopUpAccessory popup = new de.netsysit.ui.filechooser.PopUpAccessory(save.getFilechooser(),shortcuts.getToolTipText(),shortcuts);
		popup.setPopUpBorder(javax.swing.BorderFactory.createEtchedBorder());
		save.getFilechooser().setAccessory(popup);
		save.getFilechooser().setMultiSelectionEnabled(false);
		save.getFilechooser().setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		save.getFilechooser().setAcceptAllFileFilterUsed(false);
		save.setParent(this);
		save.setAllowedSuffixes(PROPERTIESSUFFIXES);
		save.setSaveDialog(true);
		save.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.save.tooltip"));
		save.setEnabled(false);
		de.netsysit.util.pattern.command.FileProcessor openPropertiesClient=
			new de.netsysit.util.pattern.command.FileProcessor()
			{
				public boolean process(java.io.File[] files)
				{
					return openProperties(files[0]);
				}
			};
		open = new de.netsysit.util.pattern.command.ChooseFileAction(openPropertiesClient,i18n.getString("I18NEditor.open.text"), (de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Open24.gif")));
		shortcuts = new de.netsysit.ui.filechooser.ShortcutsAccessory(open.getFilechooser(), geti18nConfigDirectory(),".I18Neditor");
		shortcuts.setPreferredSize(new java.awt.Dimension(200,250));
		popup = new de.netsysit.ui.filechooser.PopUpAccessory(open.getFilechooser(),shortcuts.getToolTipText(),shortcuts);
		popup.setPopUpBorder(javax.swing.BorderFactory.createEtchedBorder());
		open.getFilechooser().setAccessory(popup);
		open.getFilechooser().setMultiSelectionEnabled(false);
		open.getFilechooser().setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		open.getFilechooser().setAcceptAllFileFilterUsed(false);
		open.setParent(this);
		open.setAllowedSuffixes(PROPERTIESSUFFIXES);
		open.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.open.tooltip"));
		de.netsysit.util.pattern.command.FileProcessor importJavaClient=
			new de.netsysit.util.pattern.command.FileProcessor()
			{
				public boolean process(java.io.File[] files)
				{
					return importJava(files[0]);
				}
			};
		importJavaAction = new de.netsysit.util.pattern.command.ChooseFileAction(importJavaClient,i18n.getString("I18NEditor.importJavaAction.text"), (de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Import24.gif")));
		shortcuts = new de.netsysit.ui.filechooser.ShortcutsAccessory(importJavaAction.getFilechooser(), geti18nConfigDirectory(),".I18Neditor");
		shortcuts.setPreferredSize(new java.awt.Dimension(200,250));
		popup = new de.netsysit.ui.filechooser.PopUpAccessory(importJavaAction.getFilechooser(),shortcuts.getToolTipText(),shortcuts);
		popup.setPopUpBorder(javax.swing.BorderFactory.createEtchedBorder());
		importJavaAction.getFilechooser().setAccessory(popup);
		importJavaAction.getFilechooser().setMultiSelectionEnabled(false);
		importJavaAction.getFilechooser().setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		importJavaAction.getFilechooser().setAcceptAllFileFilterUsed(false);
		importJavaAction.setParent(this);
		importJavaAction.setAllowedSuffixes(JAVASUFFIXES);
		importJavaAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.importJavaAction.tooltip"));
		importJavaAction.setEnabled(false);
		removeaction=
			new javax.swing.AbstractAction(i18n.getString("I18NEditor.removeaction.text"),(de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Remove24.gif")))
			{;
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					java.lang.StringBuffer preset=new java.lang.StringBuffer();
					javax.swing.tree.TreePath tp=treetable.getTree().getSelectionPath();
					final int selrow=treetable.getSelectedRow();
					if(tp!=null)
					{
						java.lang.Object[] nodes=tp.getPath();
						for(int i=0;i<nodes.length;++i)
						{
							if(i>1)
								preset.append('.');
							preset.append(nodes[i]);
						}
					}
					model.removeKey(preset.toString());
				}
			};
		removeaction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.removeaction.tooltip"));
		removeaction.setEnabled(false);
		configAction=
			new javax.swing.AbstractAction(i18n.getString("I18NEditor.configAction.text"), (de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Preferences24.gif")))
			{;
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					if(iFactory==null)
						iFactory=new de.netsysit.util.beans.InterfaceFactory();
					try
					{
						javax.swing.JOptionPane.showMessageDialog(I18NEditor.this,iFactory.fetchInterfaceForBean(config, i18n.getString("I18NEditor.configAction.text")));
					}
					catch(java.lang.Exception exp)
					{
						de.elbosso.util.Utilities.handleException(null, I18NEditor.this, exp);
					}
				}
			};
		configAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.configAction.tooltip"));
		javax.swing.ImageIcon findprevicon=null;
		javax.swing.ImageIcon findnexticon=null;
		try
		{
			findnexticon=new javax.swing.ImageIcon(de.netsysit.ui.image.DecoratedImageProducer.produceImage(de.netsysit.util.ResourceLoader.getImgResource("toolbarButtonGraphics/general/Find24.gif"), de.netsysit.util.ResourceLoader.getResource("de/netsysit/ressources/gfx/common/Next16.gif")));
		}
		catch(java.io.IOException exp)
		{
			findnexticon=(de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/FindAgain24.gif"));
		}
		try
		{
			findprevicon=new javax.swing.ImageIcon(de.netsysit.ui.image.DecoratedImageProducer.produceImage(de.netsysit.util.ResourceLoader.getImgResource("toolbarButtonGraphics/general/Find24.gif"), de.netsysit.util.ResourceLoader.getResource("de/netsysit/ressources/gfx/common/Previous16.gif"),javax.swing.SwingConstants.SOUTH_WEST));
		}
		catch(java.io.IOException exp)
		{
			findprevicon=(de.netsysit.util.ResourceLoader.getIcon("de/netsysit/ressources/gfx/common/FindPrevious24.gif"));
		}
		nextblankAction=
			new javax.swing.AbstractAction(i18n.getString("I18NEditor.nextblankAction.text"),findnexticon)
			{;
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					selectNextItemYetToDo(true);
				}

			};
		nextblankAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.nextblankAction.tooltip"));
		nextblankAction.setEnabled(false);
		prevblankAction=
			new javax.swing.AbstractAction(i18n.getString("I18NEditor.prevblankAction.text"),findprevicon)
			{;
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					selectNextItemYetToDo(false);
				}

			};
		prevblankAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.prevblankAction.tooltip"));
		prevblankAction.setEnabled(false);
		de.netsysit.util.pattern.command.FileProcessor refactorPropertiesClient=
			new de.netsysit.util.pattern.command.FileProcessor()
			{
				public boolean process(java.io.File[] files)
				{
					return refactorProperties(files[0]);
				}
			};
		refactor = new de.netsysit.util.pattern.command.ChooseFileAction(refactorPropertiesClient,i18n.getString("I18NEditor.refactor.text"), (de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Export24.gif")));
		shortcuts = new de.netsysit.ui.filechooser.ShortcutsAccessory(refactor.getFilechooser(), geti18nConfigDirectory(),".I18Neditor");
		shortcuts.setPreferredSize(new java.awt.Dimension(200,250));
		popup = new de.netsysit.ui.filechooser.PopUpAccessory(refactor.getFilechooser(),shortcuts.getToolTipText(),shortcuts);
		popup.setPopUpBorder(javax.swing.BorderFactory.createEtchedBorder());
		refactor.getFilechooser().setAccessory(popup);
		refactor.setSaveDialog(true);
		refactor.getFilechooser().setMultiSelectionEnabled(false);
		refactor.getFilechooser().setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		refactor.getFilechooser().setAcceptAllFileFilterUsed(false);
		refactor.setParent(this);
		refactor.setAllowedSuffixes(PROPERTIESSUFFIXES);
		refactor.putValue(javax.swing.Action.SHORT_DESCRIPTION, i18n.getString("I18NEditor.refactor.tooltip"));
		refactor.setEnabled(false);
	}
	private boolean importJava(File file)
	{
		boolean rv=false;
		try{
			java.io.FileInputStream is=new java.io.FileInputStream(file);
	//		java.io.InputStreamReader isr=new java.io.InputStreamReader(is);
			java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
			de.elbosso.util.Utilities.copyBetweenStreams(is, os);
			os.close();
			is.close();
			java.lang.String filecontent=new java.lang.String(os.toString());

			java.util.regex.Pattern patt=java.util.regex.Pattern.compile(".*?(\\s+|\\.)ResourceBundle\\s+(\\w+)\\s*(=|;).*?");
			java.util.regex.Matcher matcher=patt.matcher(filecontent);
			java.util.List<java.lang.String> variables=new java.util.LinkedList();
			while(matcher.find())
			{


				variables.add(matcher.group(2));
			}

			java.lang.StringBuffer buf=new java.lang.StringBuffer();
			for (String string : variables)
			{
				if(buf.length()>0)
					buf.append("|");
				buf.append(string);
			}
			java.util.regex.Pattern patt_=java.util.regex.Pattern.compile(".*?("+buf.toString()+").getString\\s*\\(\\s*\"(.*?)\"\\s*\\).*");
			java.util.regex.Matcher matcher_=patt_.matcher(filecontent);
			java.util.List<java.lang.String> strings=new java.util.LinkedList();
			while(matcher_.find())
			{
				strings.add(matcher_.group(2));

			}
			patt_=java.util.regex.Pattern.compile(".*?netbeans.I18N.getResource\\(("+buf.toString()+")\\s*,\\s*\"(.*?)\"\\s*\\).*");
			matcher_=patt_.matcher(filecontent);
			while(matcher_.find())
			{
				strings.add(matcher_.group(2));

			}
			java.util.Collections.sort(strings);

			de.netsysit.ui.text.TextEditor editor=new de.netsysit.ui.text.TextEditor(new de.netsysit.ui.text.AugmentedJEditTextArea());
			buf=new java.lang.StringBuffer();
			for (String string : strings)
			{
				if(buf.length()>0)
					buf.append("\n");
				buf.append(string);
			}
			editor.setText(buf.toString());
			editor.getTextField().setCaretPosition(0);
			editor.getTextField().setPreferredSize(new java.awt.Dimension(640,480));
			if(javax.swing.JOptionPane.OK_OPTION==javax.swing.JOptionPane.showConfirmDialog(this, editor.getTextField(),"",javax.swing.JOptionPane.OK_CANCEL_OPTION))
			{
				strings.clear();
				for(int i=0;i<editor.getTextField().getLineCount();++i)
				{
					java.lang.String candidate=editor.getTextField().getText(editor.getTextField().getLineStartOffset(i),editor.getTextField().getLineEndOffset(i)-editor.getTextField().getLineStartOffset(i));

					if((candidate!=null)&&(candidate.trim().length()>0))
						strings.add(candidate.trim());
				}

				for (String string : strings)
				{
					model.addKey(string);
				}
			}
			Utilities.performAction(this,nextblankAction);
			rv=true;
		}
		catch(java.io.IOException exp)
		{
			de.elbosso.util.Utilities.handleException(null, this, exp);
		}
		return rv;
	}
	private void selectNextItemYetToDo(boolean forward)
	{

		javax.swing.tree.TreePath path=treetable.getTree().getPathForRow(treetable.getSelectedRow());

		if((path==null)||(path.getPathCount()<1))
		{
			path=new javax.swing.tree.TreePath(treetable.getTree().getModel().getRoot());
		}

		java.lang.StringBuffer buf=new java.lang.StringBuffer();
		for(int i=1;i<path.getPathCount();++i)
		{
			if(buf.length()>0)
				buf.append(".");
			buf.append(path.getPathComponent(i).toString());
		}

		java.lang.String next=buf.toString();
		treetable.getTree().clearSelection();
//		java.lang.String oldnext=next+"#"+next;
		while(next!=null)
//		while((next!=null)&&(oldnext.equals(next)==false))
		{
//			oldnext=next;
			next=model.getNextMatchingProperty(next, forward);

			if(next!=null)
			{
				javax.swing.tree.TreePath npath=model.addKey(next);
				boolean needsattention=false;
				for(int i=1;i<model.getColumnCount();++i)
				{
					java.lang.Object ref=model.getValueAt(npath.getLastPathComponent(), i);
					if((ref==null)||(ref.toString().trim().length()<1))
					{
						needsattention=true;
						break;
					}
				}
				if(needsattention)
				{

								treetable.getTree().setScrollsOnExpand(true);
					treetable.getTree().expandPath(npath.getParentPath());

					int r=treetable.getTree().getRowForPath(npath.getParentPath());

					treetable.scrollRectToVisible(new java.awt.Rectangle(0,(r-2)*treetable.getRowHeight()+scroller.getHeight(),1,1));

				//			if(treetable.editCellAt(r,1))
				//				treetable.getEditorComponent().requestFocus();
					treetable.getTree().setSelectionPath(npath);
					next=null;
					break;
				}
			}
		}
		if(treetable.getTree().getSelectionCount()<1)
			java.awt.Toolkit.getDefaultToolkit().beep();
	}
	private void addKey()
	{
		java.lang.StringBuffer preset=new java.lang.StringBuffer();
		javax.swing.tree.TreePath tp=treetable.getTree().getSelectionPath();
		final int selrow=treetable.getSelectedRow();
		java.awt.datatransfer.Transferable tr=null;
		if(java.awt.Toolkit.getDefaultToolkit().getSystemSelection()!=null)
			tr=java.awt.Toolkit.getDefaultToolkit().getSystemSelection().getContents(java.awt.datatransfer.DataFlavor.stringFlavor);
		if(tr!=null)
		{
			try
			{
				java.lang.Object ref = tr.getTransferData(DataFlavor.stringFlavor);
				if(ref!=null)
				{
					if(CLASS_LOGGER.isTraceEnabled())CLASS_LOGGER.trace(ref.getClass()+" "+ref);
					java.lang.String data=ref.toString();
					if((data!=null)&&(data.trim().length()>0))
					{
						preset.append(data.trim());
					}
				}
			}
			catch (UnsupportedFlavorException ex)
			{
				Logger.getLogger(I18NEditor.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch (IOException ex)
			{
				Logger.getLogger(I18NEditor.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		if((preset.length()<1)&&(tp!=null))
		{
			java.lang.Object[] nodes=tp.getPath();
			for(int i=0;i<nodes.length;++i)
			{
				if(i>1)
					preset.append('.');
				preset.append(nodes[i]);
			}
		}
		java.lang.String newkey=javax.swing.JOptionPane.showInputDialog(this,i18n.getString("I18NEditor.addRow.dialogtitle"),preset.toString());
		if((newkey!=null)&&((newkey.trim().length()>0)&&(newkey.trim().endsWith(".")==false)))
		{
			javax.swing.tree.TreePath newnode=model.addKey(newkey.trim());

			java.lang.Object[] nodes=newnode.getPath();

			for(int i=0;i<nodes.length;++i)
				if(CLASS_LOGGER.isTraceEnabled())CLASS_LOGGER.trace(nodes[i]+" "+nodes[i].getClass());
			treetable.getTree().expandPath(newnode.getParentPath());
//			treetable.getTree().expandPath(treetable.getTree().getClosestPathForLocation(150, 150));


			nodes=treetable.getTree().getClosestPathForLocation(150, 150).getPath();

			for(int i=0;i<nodes.length;++i)
				if(CLASS_LOGGER.isTraceEnabled())CLASS_LOGGER.trace(nodes[i]+" "+nodes[i].getClass());
			int r=treetable.getTree().getRowForPath(newnode);


			treetable.scrollRectToVisible(new java.awt.Rectangle(0,(r-2)*treetable.getRowHeight()+scroller.getHeight(),1,1));
			if(treetable.editCellAt(r,1))
				treetable.getEditorComponent().requestFocus();

			treetable.getTree().setSelectionPath(newnode);
		}
		else
			treetable.getTree().setSelectionPath(tp);
	}
	private void addLanguage()
	{
		java.lang.String[] options=java.util.Locale.getISOLanguages();
		javax.swing.ComboBoxModel cbmodel=new javax.swing.DefaultComboBoxModel(options);
		javax.swing.JComboBox cb=new javax.swing.JComboBox(cbmodel);
		int rv=javax.swing.JOptionPane.showConfirmDialog(this,cb,i18n.getString("I18NEditor.addColumn.dialogtitle"),javax.swing.JOptionPane.OK_CANCEL_OPTION);
		if(rv==javax.swing.JOptionPane.OK_OPTION)
		{
			if(cb.getSelectedItem()!=null)
			{
				try{
					model.addLanguage(cb.getSelectedItem().toString());
				}catch(java.lang.IllegalArgumentException exp){javax.swing.JOptionPane.showMessageDialog(this,exp.getMessage());}
			}
		}
	}
	private boolean openProperties(java.io.File file)
	{
		boolean rv=false;
		try
		{
			treetable.getSelectionModel().removeListSelectionListener(this);
			model=new Model(config,treetable,file.getCanonicalPath());
			treetable.setModel((de.netsysit.model.tree.TreeTableModel)model);
			addRow.setEnabled(model!=null);
			addColumn.setEnabled(model!=null);
			save.setEnabled(model!=null);
			save.getFilechooser().setSelectedFile(file);
			expandTreeAction.setEnabled(model!=null);
			collapseTreeAction.setEnabled(model!=null);
			expandSelectedTreeAction.setEnabled(((model!=null)&(treetable.getSelectionModel().isSelectionEmpty()==false)));
			collapseSelectedTreeAction.setEnabled(((model!=null)&(treetable.getSelectionModel().isSelectionEmpty()==false)));
			nextblankAction.setEnabled(model!=null);
			prevblankAction.setEnabled(model!=null);
			importJavaAction.setEnabled(model!=null);
			setName(file.getName());
			treetable.getSelectionModel().addListSelectionListener(this);
		}catch(Exception exp){}
		return rv;
	}
	private boolean refactorProperties(File file)
	{
		boolean rv=false;
		try
		{
			javax.swing.tree.TreePath tp=treetable.getTree().getSelectionPath();
			if(tp!=null)
			{
				java.lang.Object[] pathElements=tp.getPath();
				java.lang.StringBuffer buf=new java.lang.StringBuffer();
				for(int i=1;i<pathElements.length;++i)
				{
					if(i>1)
						buf.append('.');
					buf.append(pathElements[i]);
				}
				if(CLASS_LOGGER.isTraceEnabled())CLASS_LOGGER.trace(buf.toString());
				java.lang.String result=buf.toString();
				java.util.List<java.lang.String> l=model.getAllPropertyNamesStartingWith(result);
				for (String string : l)
				{
					if(CLASS_LOGGER.isTraceEnabled())CLASS_LOGGER.trace("+ "+string);
				}
				I18NModel filtered=model.filter(l);
				filtered.append(file.getCanonicalPath());
			}
		}catch(Exception exp){
			de.elbosso.util.Utilities.handleException(null, this, exp);}
		return rv;
	}
	private void newrb()
	{
		model=new Model(config,treetable);
		treetable.setModel((de.netsysit.model.tree.TreeTableModel)model);
		addRow.setEnabled(model!=null);
		addColumn.setEnabled(model!=null);
		save.setEnabled(model!=null);
		expandTreeAction.setEnabled(model!=null);
		collapseTreeAction.setEnabled(model!=null);
		expandSelectedTreeAction.setEnabled(((model!=null)&(treetable.getSelectionModel().isSelectionEmpty()==false)));
		collapseSelectedTreeAction.setEnabled(((model!=null)&(treetable.getSelectionModel().isSelectionEmpty()==false)));
		nextblankAction.setEnabled(model!=null);
		prevblankAction.setEnabled(model!=null);
		importJavaAction.setEnabled(model!=null);
	}
	private boolean saveProperties(java.io.File file)
	{
		boolean rv=false;
		try
		{
			java.lang.String name=file.getCanonicalPath();
			int dotindex=name.lastIndexOf(".properties");
			if(dotindex>-1)
				name=name.substring(0,dotindex);
			model.writeItOut(name);
			rv=true;
		}catch(Exception exp){}
		return rv;
	}
	private void populateToolBar()
	{
		toolbar.setFloatable(false);
		toolbar.add(newaction);
		toolbar.add(open);
		toolbar.add(save);
		toolbar.addSeparator();
		toolbar.add(importJavaAction);
		toolbar.add(refactor);
		toolbar.addSeparator();
		toolbar.add(addColumn);
		toolbar.add(addRow);
		toolbar.add(removeaction);
		toolbar.addSeparator();
		toolbar.add(prevblankAction);
		toolbar.add(nextblankAction);
		toolbar.addSeparator();
		toolbar.add(expandTreeAction);
		toolbar.add(collapseTreeAction);
		toolbar.add(expandSelectedTreeAction);
		toolbar.add(collapseSelectedTreeAction);
		toolbar.addSeparator();
		toolbar.add(configAction);
		de.elbosso.tools.i18neditor.notification.Notification.getSharedInstance().addAction(addRow);
	}
	void dispose()
	{
		de.elbosso.tools.i18neditor.notification.Notification.getSharedInstance().tearDown();
	}
	//Implementation of interface javax.swing.event.ListSelectionListener
	public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent0)
	{
		if(listSelectionEvent0.getValueIsAdjusting()==false)
		{
			removeaction.setEnabled(treetable.getSelectionModel().isSelectionEmpty()==false);
			refactor.setEnabled(treetable.getSelectionModel().isSelectionEmpty()==false);
			expandSelectedTreeAction.setEnabled(((model!=null)&(treetable.getSelectionModel().isSelectionEmpty()==false)));
			collapseSelectedTreeAction.setEnabled(((model!=null)&(treetable.getSelectionModel().isSelectionEmpty()==false)));
		}
	}
}
