/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elbosso.tools.i18neditor;

import de.elbosso.util.Utilities;

import java.io.IOException;

/**
 *
 * @author elbosso
 */
class Model extends de.netsysit.model.tree.I18NModel
{
	private final static org.slf4j.Logger CLASS_LOGGER =org.slf4j.LoggerFactory.getLogger(Model.class);
	private boolean recurse=true;
	private de.netsysit.ui.components.JTreeTable treetable;
	private Config config;

	Model(Config config,de.netsysit.ui.components.JTreeTable treetable,String canonicalPath) throws IOException
	{
		super(canonicalPath);
		this.treetable=treetable;
		this.config=config;
	}

	Model(Config config,de.netsysit.ui.components.JTreeTable treetable)
	{
		super();
		this.treetable=treetable;
		this.config=config;
	}

	@Override
	public void setValueAt(Object aValue, Object node, int column)
	{
		super.setValueAt(aValue, node, column);
		if((config.isDoAutomaticTranslation())&&((config.getAutomaitcTranslationToolWithFullPath()!=null)&&(config.getAutomaitcTranslationToolWithFullPath().trim().length()>0)))
		{
			if((recurse)&&((aValue!=null)&&(aValue.toString().trim().length()>0)))
			{
				recurse=false;
				if(column>0)
				{
					java.lang.String from="";
					java.lang.String to="";
					if (column==1)
						from="en";
					else
						from=getColumnName(column);
					if(from.startsWith("_"))
						from=from.substring(1);
					for(int i=1;i<getColumnCount();++i)
					{
						if(i!=column)
						{
							java.lang.Object ref=getValueAt(node, i);
							if((ref==null)||(ref.toString().trim().length()<1))
							{
								if (i==1)
									to="en";
								else
									to=getColumnName(i);
								if(to.startsWith("_"))
									to=to.substring(1);
								if(CLASS_LOGGER.isTraceEnabled())CLASS_LOGGER.trace("translating \""+aValue+"\" from "+from+" to "+to);
								java.lang.String command=config.getAutomaitcTranslationToolWithFullPath().trim();
								if(CLASS_LOGGER.isTraceEnabled())CLASS_LOGGER.trace(command);
								try
								{
									Process proc=Runtime.getRuntime().exec(new java.lang.String[]{command,aValue.toString(),from,to});
									java.io.InputStream is=proc.getInputStream();
									java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
									Utilities.copyBetweenStreams(is, baos);
									baos.close();
									is.close();
									setValueAt(baos.toString(), node, i);
								}
								catch(java.io.IOException exp)
								{
									exp.printStackTrace();
								}
							}
						}
					}
				}
				recurse=true;
				treetable.repaint();
			}
		}
	}

}
