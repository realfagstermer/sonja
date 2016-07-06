/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonja;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author knuthe
 */
public class Feltvalgmeny extends JPopupMenu {
    Feltvalgmeny (String[]valg, PopoppAksjon act){
        for (int i = 0; i < valg.length; i++){
            JMenuItem jmi = new JMenuItem(valg[i]);
            jmi.addActionListener(act);
            add(jmi);
        }
    }
}
