/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1405057_filebrowser;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author MD_Kaykobad_Reza
 */
public class TileCellRenderer extends JLabel implements ListCellRenderer<Object>{

    public TileCellRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list,           // the list
       Object value,            // value to display
       int index,               // cell index
       boolean isSelected,      // is the cell selected
       boolean cellHasFocus )   // does the cell have focus) 
    {
        File f = (File)value;
        String s = fsv.getSystemDisplayName(f);
        setText(s);
        Icon icon = fsv.getSystemIcon(f);
        setIcon(icon);

        if (isSelected) {
             setBackground(list.getSelectionBackground());
             setForeground(list.getSelectionForeground());
         } else {
             setBackground(list.getBackground());
             setForeground(list.getForeground());
         }
         setEnabled(list.isEnabled());
         setFont(list.getFont());
         setOpaque(true);
         
         return this;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private FileSystemView fsv = FileSystemView.getFileSystemView();
    private MouseMotionListener mouseMotionListener;
    private JLabel label;
}
