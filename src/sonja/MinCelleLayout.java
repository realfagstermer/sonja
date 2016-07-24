/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonja;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author knuthe
 */
public class MinCelleLayout extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int indeks,
            boolean iss,
            boolean fokus) {
        super.getListCellRendererComponent(list, value, indeks, iss, fokus);
        Term t = (Term) value;
        if (t.slettdato != null) {
            setForeground(Color.RED);
        } else {
            setForeground(Color.BLACK);
        }
        return this;
    }
}
