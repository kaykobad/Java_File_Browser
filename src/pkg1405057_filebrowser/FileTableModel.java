package pkg1405057_filebrowser;

import java.io.File;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author MD_Kaykobad_Reza
 * This class models the table view
 * 
 */
public class FileTableModel extends AbstractTableModel{

    public FileTableModel() {
        this(new File[0]);
    }

    public FileTableModel(File[] files) {
        this.files = files;
    }
    
    

    @Override
    public int getRowCount() {
        int len = files.length;
        return len;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        int len2 = columns.length;
        return len2;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int row, int col) {
        File file = files[row];
        
        switch(col){
            case 0:
                return fsv.getSystemIcon(file);
            case 1:
                return fsv.getSystemDisplayName(file);
            case 2:
                return file.getPath();
            case 3:
                return file.length();
            case 4:
                return file.lastModified();
            case 5:
                return file.canRead();
            case 6:
                return file.canWrite();
            case 7:
                return file.canExecute();
            case 8:
                return file.isDirectory();
            case 9:
                return file.isFile();
            default:
                System.out.println("GetValueAt function call error");
        }
        return "";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     *
     * @param column
     * @return
     */
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 3:
                return Long.class;
            case 4:
                return Date.class;
            case 5:
                return Boolean.class;
            case 6:
                return Boolean.class;
            case 7:
                return Boolean.class;
            case 8:
                return Boolean.class;
            case 9:
                return Boolean.class;
            
        }
        return String.class;
    }
    
    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
    
    /**
     *
     * @param column
     * @return
     */
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }
    
    public File getFile(int row) {
        try{
            return files[row];
        }
        catch(Exception e){
            System.err.println(e);
        }
        return null;
    }
    
    private FileSystemView fsv = FileSystemView.getFileSystemView();
    private String[] columns = {
        "Icon", "File Name", "Path",
        "Size", "Last Modified", "R",
         "W", "E", "D", "F"
    };
    private File[] files;
    
}
