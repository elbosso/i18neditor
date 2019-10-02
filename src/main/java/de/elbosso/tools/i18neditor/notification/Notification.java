/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elbosso.tools.i18neditor.notification;

import javax.swing.Action;

/**
 *
 * @author elbosso
 */
public class Notification extends de.netsysit.ui.notification.Notification
{
	public static de.netsysit.ui.notification.Notification getSharedInstance()
	{
		if(sharedInstance==null)
			sharedInstance=new Notification();
		return sharedInstance;
	}

	protected void ensureTrayIconConstruction()
	{
		if(trayIcon==null)
		{
			try
			{
				java.awt.Dimension dim=java.awt.SystemTray.getSystemTray().getTrayIconSize();
				trayIcon=new java.awt.TrayIcon((de.netsysit.util.ResourceLoader.getIcon("de/netsysit/tools/i18neditor/logo.gif")).getImage());//.getScaledInstance(dim.width, dim.height, java.awt.Image.SCALE_SMOOTH));
				trayIcon.setImageAutoSize(true);
				java.awt.SystemTray.getSystemTray().add(trayIcon);
			}
			catch (java.awt.AWTException ex)
			{
				ex.printStackTrace();
			}
			catch (java.lang.UnsupportedOperationException ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
