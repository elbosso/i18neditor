/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elbosso.tools.i18neditor;


/**
 *
 * @author elbosso
 */
public class I18NEditorApplet extends javax.swing.JApplet
{
	private I18NEditor ed;
	@Override
	public void start()
	{
		try
		{
			super.start();
			ed = new I18NEditor();
			add(ed);
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public void stop()
	{
		ed.saveConfig();
		super.stop();

	}

public void setSize(int width, int height)
{
super.setSize(width,height);
validate();
}
	
}
/*<html><body onResize="resize()" onLoad="resize()" 
topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">
<SCRIPT LANGUAGE="JavaScript">
function resize() {
var w_newWidth,w_newHeight;
var w_maxWidth=1600, w_maxHeight=1200;
if (navigator.appName.indexOf("Microsoft") != -1)
{
w_newWidth=document.body.clientWidth;
w_newHeight=document.body.clientHeight;
}else{
var netscapeScrollWidth=15;
w_newWidth=window.innerWidth-netscapeScrollWidth;
w_newHeight=window.innerHeight-netscapeScrollWidth;
}
if (w_newWidth>w_maxWidth)
w_newWidth=w_maxWidth;
if (w_newHeight>w_maxHeight)
w_newHeight=w_maxHeight;
document.myApplet.setSize(w_newWidth,w_newHeight);
window.scroll(0,0);
}
window.onResize = resize;
window.onLoad = resize;
</SCRIPT>

<applet name="myApplet" code="de.elbosso.tools.i18neditorApplet" CODEBASE="http://mond/i18neditor/app" width="2600"
         height="1200">
	<param name="jnlp_href" value="launch_applet.jnlp">
	<PARAM name="draggable" value="true">

</applet>
<!--applet code="de.netsysit.ui.applets.FireApplet" CODEBASE="http://mond/~elbosso/demo" width="64" height="64"/-->
<!--applet code="de.netsysit.ui.applets.DitherLEDLineApplet" CODEBASE="http://elbosso.github.io/~jkey/demo" width="300" height="240"/-->
<!--applet code=de.netsysit.ui.applets.DitherLEDLineApplet width=300 height=300-->
</body>
</html>*/