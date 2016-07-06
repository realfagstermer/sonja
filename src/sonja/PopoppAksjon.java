/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonja;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author knuthe
 */
public class PopoppAksjon implements ActionListener {
    String kildefelt = null;
    
    PopoppAksjon(String felt) {
            kildefelt = felt;
    }
    
    public void actionPerformed(ActionEvent act){
        Sonja.popoppmenyaksjon(kildefelt, act.getActionCommand());
    }
    
}
