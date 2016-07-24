/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sonja;

import java.awt.Desktop;
import javax.swing.JOptionPane;

/**
 *
 * @author knuthe
 */

    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 1.5 (December 10, 2005)                    //
//  By Dem Pilafian                                    //
//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//  Example Usage:                                     //
//     String url = "http://www.centerkey.com/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////
import java.awt.Desktop;
import javax.swing.JOptionPane;

public class BrowserLaunch {

    private static final String errMsg = "Feil ved starting av nettleser";

    public static void openURL(String url) {
        if (Desktop.isDesktopSupported()) {
            // System.out.println("støtter desktop");
            Desktop skrivebord = Desktop.getDesktop();
            if (skrivebord.isSupported(Desktop.Action.BROWSE)) {
                // System.out.println("støtter browse");

                try {
                    skrivebord.browse(new java.net.URI(url));
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(
                            null, errMsg + ":\n"
                            + exp.getLocalizedMessage());
                }
            } else {
                //System.out.println("støtter ikke browse");
            }
        } else {
            //System.out.println("støtter ikke desktop");
            String osName = System.getProperty("os.name");
            //System.out.println("OS= " + osName);
            try {
                if (osName.startsWith("Mac OS")) {
                   // com.apple.eio.FileManager.openURL(url);
                    /*
                    Class fileMgr = Class.forName("com.apple.eio.FileManager");
                    Method openURL = fileMgr.getDeclaredMethod("openURL",
                    new Class[]{String.class});
                    openURL.invoke(null, new Object[]{url});
                     * */
                } else if (osName.startsWith("Windows")) {
                    //Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler <a href=\"" + url + "\" target=\"bibsysASK\">" + url + "</a>");
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url );
                    //Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url + " target=bibsys" );
                    //String [] commandline = {"firefox.exe", url, "-new_tab"};
                    //Runtime.getRuntime().exec(commandline);
                } else { //assume Unix or Linux
                    String[] browsers = {
                        "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                    String browser = null;
                    for (int count = 0; count < browsers.length && browser == null; count++) {
                        if (Runtime.getRuntime().exec(
                                new String[]{"which", browsers[count]}).waitFor() == 0) {
                            browser = browsers[count];
                        }
                    }
                    if (browser == null) {
                        throw new Exception("Could not find web browser");
                    } else {
                        //System.out.println(browser + " " + url);
                        Runtime.getRuntime().exec(new String[]{browser, url});
                        //Runtime.getRuntime().exec("firefox <a href=\"" + url + "\" target=\"ASK\">" + url + "</a>");
                    }
                }
            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, errMsg + ":\n" + e.getLocalizedMessage());
            }
        }
    }
}

