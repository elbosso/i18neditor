/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elbosso.tools.i18neditor;


import de.elbosso.ui.Utilities;
import de.netsysit.util.ResourceLoader;

import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author elbosso
 */
public class I18NEditorFrame extends javax.swing.JFrame
		implements java.beans.PropertyChangeListener
		,java.awt.event.WindowListener
{
	private final static java.util.ResourceBundle i18n=java.util.ResourceBundle.getBundle("de.netsysit.util.i18n",java.util.Locale.getDefault());
	private final static int inset = 50;
	private I18NEditor ed;

	public I18NEditorFrame(java.lang.String name) throws Exception
	{
		super(name);
		setIconImage((de.netsysit.util.ResourceLoader.getIcon("de/elbosso/tools/logoicon.png")).getImage());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		ed=new I18NEditor();
		setContentPane(ed);
		java.awt.Rectangle screenSize=this.getGraphicsConfiguration().getBounds();
		setBounds(inset, inset, screenSize.width - 3 * inset, screenSize.height - 4 * inset);
		setBounds(Utilities.loadDimensions(I18NEditorFrame.class));
		ed.addPropertyChangeListener("name", this);
//		pack();
		setVisible(true);
		addWindowListener(this);
	}

	public static void main (java.lang.String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new java.lang.Runnable()
		   {
			   @Override
			   public void run()
			   {
				   try
				   {
					   java.util.Properties iconFallbacks = new java.util.Properties();
					   java.io.InputStream is=de.netsysit.util.ResourceLoader.getResource("de/elbosso/ressources/data/icon_trans_material.properties").openStream();
					   iconFallbacks.load(is);
					   is.close();
					   de.netsysit.util.ResourceLoader.configure(iconFallbacks);
				   }
				   catch(java.io.IOException ioexp)
				   {
					   ioexp.printStackTrace();
				   }

				   de.netsysit.util.ResourceLoader.setSize(ResourceLoader.IconSize.small);
				   try
				   {
					   new I18NEditorFrame(i18n.getString("I18NEditor.app.title"));
				   } catch (Exception e)
				   {
				   	de.elbosso.util.Utilities.handleException(null,e);
				   }
			   }
		   }
		);
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		setTitle(getTitle()+" - "+evt.getNewValue());
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
		ed.saveConfig();
		ed.dispose();
		System.exit(0);
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}
}
