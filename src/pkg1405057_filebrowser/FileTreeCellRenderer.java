package pkg1405057_filebrowser;

import java.awt.Component;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author MD_Kaykobad_Reza
 * This class controls the property of each tree cell
 * A TreeCellRenderer for a file
 * 
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    public FileTreeCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fsv = FileSystemView.getFileSystemView();
        
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object obj, boolean selected, boolean expanded, boolean isLeaf, int row, boolean hasFocus){
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
        File file = (File)node.getUserObject();
        
        if(selected){
            label.setForeground(textSelectionColor);
            label.setBackground(backgroundSelectionColor);
        }
        else{
            label.setForeground(textNonSelectionColor);
            label.setBackground(backgroundNonSelectionColor);
        }
        
        label.setIcon(fsv.getSystemIcon(file));
        label.setText(fsv.getSystemDisplayName(file));
        label.setToolTipText(file.getPath());
        
        return label;
    }
    
    private JLabel label;
    private FileSystemView fsv;
    
    
}
