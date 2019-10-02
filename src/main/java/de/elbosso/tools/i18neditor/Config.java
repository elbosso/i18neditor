/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elbosso.tools.i18neditor;

/**
 *
 * @author elbosso
 */
public class Config 
{

	private boolean doAutomaticTranslation;

	public boolean isDoAutomaticTranslation()
	{
		return doAutomaticTranslation;
	}

	public void setDoAutomaticTranslation(boolean doAutomaticTranslation)
	{
		this.doAutomaticTranslation = doAutomaticTranslation;
	}
	private String automaitcTranslationToolWithFullPath;

	public String getAutomaitcTranslationToolWithFullPath()
	{
		return automaitcTranslationToolWithFullPath;
	}

	public void setAutomaitcTranslationToolWithFullPath(String automaitcTranslationToolWithFullPath)
	{
		this.automaitcTranslationToolWithFullPath = automaitcTranslationToolWithFullPath;
	}

}
