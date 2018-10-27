/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1405057_filebrowser;

import java.io.File;
import javax.swing.AbstractListModel;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author MD_Kaykobad_Reza
 */
public class FileTileModel extends AbstractListModel{

    public FileTileModel() {
    }

    public FileTileModel(File[] files) {
        this.files = files;
    }
    
    public void setFiles(File[] files){
        this.files = files;
    }

    @Override
    public int getSize() {
        return files.length;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getElementAt(int i) {
        return files[i];
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private FileSystemView fsv = FileSystemView.getFileSystemView();
    private File[] files;

    File getFile(int row) {
        return files[row];
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
