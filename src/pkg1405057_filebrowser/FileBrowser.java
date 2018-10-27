package pkg1405057_filebrowser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author MD_Kaykobad_Reza
 */
public class FileBrowser {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException tried){
                    System.out.println("Tried but Failed");
                }
                
                JFrame frame = new JFrame("File Browser");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                FileBrowser fileBrowser = new FileBrowser();
                frame.setContentPane(fileBrowser.getGui());
                
                
                frame.pack();
                frame.setMinimumSize(frame.getSize());
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
                
                fileBrowser.showRootFile();
            }
        });
    }
    
    private Container getGui() {
        if(gui==null){
            gui = new JPanel(new BorderLayout(3,3));
            gui.setBorder(new EmptyBorder(5, 5, 5, 5));
            
            //icon and editing access
            fileSystemView = FileSystemView.getFileSystemView();
            desktop = Desktop.getDesktop();
            
            detailView = new JPanel(new BorderLayout(3,3));
            
            // details for a File
            JPanel fileMainDetails = new JPanel(new BorderLayout(4,2));
            fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

            JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

            JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

            JPanel flags = bottomRightDetails(fileDetailsLabels, fileDetailsValues);
            
             //file tree building up
            JScrollPane treeScroll  = buildFileTree();

             //table details building up
            buildUpListView(detailView);
            buildUpTileView(detailView);
            
            //set up bottom right toolbar
            JToolBar toolBar = bottomRightToolbar(flags);
            
            int count = fileDetailsLabels.getComponentCount();
            for (int ii=0; ii<count; ii++) {
                fileDetailsLabels.getComponent(ii).setEnabled(false);
            }

            count = flags.getComponentCount();
            for (int ii=0; ii<count; ii++) {
                flags.getComponent(ii).setEnabled(false);
            }

            JPanel fileView = new JPanel(new BorderLayout(3,3));

            fileView.add(toolBar,BorderLayout.NORTH);
            fileView.add(fileMainDetails,BorderLayout.CENTER);

            detailView.add(fileView, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                treeScroll,
                detailView);
            gui.add(splitPane, BorderLayout.CENTER);

            JPanel simpleOutput = new JPanel(new BorderLayout(3,3));
            progressBar = new JProgressBar();
            simpleOutput.add(progressBar, BorderLayout.EAST);
            progressBar.setVisible(false);

            gui.add(simpleOutput, BorderLayout.SOUTH);
        }
        
        return gui;
    }
    
    public Component viewFactory(){
        if(flag==1){
            flag++;
            return listTiles;
        }else{
            flag--;
            return table;
        }
    }

    public void buildUpListView(JPanel detailView) {
        if(currentFile==null)
            currentFile = new File(".");
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(true);
        
        listSelectionListener = (ListSelectionEvent lse) -> {
            int row = table.getSelectionModel().getLeadSelectionIndex();
            setFileDetails(((FileTableModel)table.getModel()).getFile(row));
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        };
        
        mouseListener2 = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me){
                if(me.getClickCount()==2){
                    int row = table.getSelectionModel().getLeadSelectionIndex();
                    //int row2 = table.getSelectedRow();
                    currentFile = ((FileTableModel)table.getModel()).getFile(row);
                    setFileDetails(currentFile);
                    setTileDetails(currentFile);
                    File[] fls = currentFile.listFiles();
                    setTileData(fls);
                    setTableData(fls);
                    fileTileModel.setFiles(fls);
                    fileTableModel.setFiles(fls);
                }
            }
        };
        
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        table.addMouseListener(mouseListener2);
        
        tableScroll = new JScrollPane(table);
        Dimension d = tableScroll.getPreferredSize();
        tableScroll.setPreferredSize(new Dimension((int)d.getWidth(), (int)d.getHeight()/2));
        detailView.add(tableScroll, BorderLayout.CENTER);
    }
    
    public void buildUpTileView(JPanel detailView){
        if(currentFile==null)
            currentFile = new File(".");
        File[] files = currentFile.listFiles();
        listTiles = new JList(files);
        listTiles.setFixedCellHeight(40);
        listTiles.setFixedCellWidth(150);
        listTiles.setVisibleRowCount(5);
        listTiles.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        
        listTiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTiles.setCellRenderer(new TileCellRenderer());
        
        listSelectionListener2 = (ListSelectionEvent lse)->{
            int row = listTiles.getSelectionModel().getLeadSelectionIndex();
            setFileDetails(((FileTileModel)listTiles.getModel()).getFile(row));
            setTileDetails(((FileTileModel)listTiles.getModel()).getFile(row));
            
        };
        
        mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me){
                if(me.getClickCount()==2){
                    int row = listTiles.getSelectionModel().getLeadSelectionIndex();
                    currentFile = ((FileTileModel)listTiles.getModel()).getFile(row);
                    setFileDetails(currentFile);
                    setTileDetails(currentFile);
                    File[] fls = currentFile.listFiles();
                    setTableData(fls);
                    setTileData(fls);
                    fileTileModel.setFiles(fls);
                    fileTableModel.setFiles(fls);
                }
            }
        };
        
        listTiles.addMouseListener(mouseListener);
        listTiles.getSelectionModel().addListSelectionListener(listSelectionListener2);
        
    }

    public JPanel bottomRightDetails(JPanel fileDetailsLabels, JPanel fileDetailsValues) {
        //bottom right
        fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
        fileName = new JLabel();
        fileDetailsValues.add(fileName);
        fileDetailsLabels.add(new JLabel("Path", JLabel.TRAILING));
        path = new JTextField(5);
        path.setEditable(false);
        fileDetailsValues.add(path);
        fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
        date = new JLabel();
        fileDetailsValues.add(date);
        fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
        size = new JLabel();
        fileDetailsValues.add(size);
        fileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));
        JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING,4,0));
        isDirectory = new JRadioButton("Directory");
        flags.add(isDirectory);
        isFile = new JRadioButton("File");
        flags.add(isFile);
        fileDetailsValues.add(flags);
        return flags;
    }

    public JToolBar bottomRightToolbar(JPanel flags) {
        JToolBar toolBar = new JToolBar();
        // mnemonics stop working in a floated toolbar
        toolBar.setFloatable(false);
        JButton locateFile = new JButton("Locate");
        locateFile.setMnemonic('l');
        locateFile.addActionListener((ActionEvent ae) -> {
            try {
                System.out.println("Locate: " + currentFile.getParentFile());
                desktop.open(currentFile.getParentFile());
            } catch(Throwable t) {
                showThrowable(t);
            }
            gui.repaint();
        });
        toolBar.add(locateFile);
        openFile = new JButton("Open");
        openFile.setMnemonic('o');
        openFile.addActionListener((ActionEvent ae) -> {
            try {
                System.out.println("Open: " + currentFile);
                desktop.open(currentFile);
            } catch(Throwable t) {
                showThrowable(t);
            }
            gui.repaint();
        });
        toolBar.add(openFile);
        toggleView = new JButton("Toggle View");
        toggleView.setMnemonic('t');
        toggleView.addActionListener((ActionEvent ae)-> {
            try{
                System.out.println("Toggle button clicked!");
                tableScroll.setViewportView(viewFactory());
                gui.repaint();
            }catch(Exception e){
                System.out.println(e);
            }
            gui.repaint();
        });
        toolBar.add(toggleView);
        editFile = new JButton("Edit");
        editFile.setMnemonic('e');
        editFile.addActionListener((ActionEvent ae) -> {
            try {
                desktop.edit(currentFile);
            } catch(Throwable t) {
                showThrowable(t);
            }
        });
        toolBar.add(editFile);
        printFile = new JButton("Print");
        printFile.setMnemonic('p');
        printFile.addActionListener((ActionEvent ae) -> {
            try {
                desktop.print(currentFile);
            } catch(Throwable t) {
                showThrowable(t);
            }
        });
        toolBar.add(printFile);
        // Check the actions are supported on this platform!
        openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
        editFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
        printFile.setEnabled(desktop.isSupported(Desktop.Action.PRINT));
        flags.add(new JLabel("::  Flags"));
        readable = new JCheckBox("Read  ");
        readable.setMnemonic('a');
        flags.add(readable);
        writable = new JCheckBox("Write  ");
        writable.setMnemonic('w');
        flags.add(writable);
        executable = new JCheckBox("Execute");
        executable.setMnemonic('x');
        flags.add(executable);
        return toolBar;
    }

    public JScrollPane buildFileTree() {
        //building up the tree
        DefaultMutableTreeNode root  = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);
        TreeSelectionListener treeSelectionListener = (TreeSelectionEvent tse) -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
            //currentFile = (File)node.getUserObject();
            showChildren(node);
            setFileDetails((File)node.getUserObject());
            setTileDetails((File)node.getUserObject());
        };
        //get file system roots
        File[] roots = fileSystemView.getRoots();
        for (File fileSystemRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
            root.add( node );
            File[] files = fileSystemView.getFiles(fileSystemRoot, true);
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(new DefaultMutableTreeNode(file));
                }
            }
            //
        }
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(treeSelectionListener);
        tree.setCellRenderer(new FileTreeCellRenderer());
        tree.expandRow(0);
        JScrollPane treeScroll = new JScrollPane(tree);
        // as per trashgod tip
        tree.setVisibleRowCount(15);
        Dimension preferredSize = treeScroll.getPreferredSize();
        Dimension widePreferred = new Dimension(200,(int)preferredSize.getHeight());
        treeScroll.setPreferredSize( widePreferred );
        return treeScroll;
    }
    
    private void showRootFile() {
        // ensure the main files are displayed
        tree.setSelectionInterval(0,0);
    }
    
    private TreePath findTreePath(File find) {
        for (int ii=0; ii<tree.getRowCount(); ii++) {
            TreePath treePath = tree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)object;
            File nodeFile = (File)node.getUserObject();

            if (nodeFile==find) {
                return treePath;
            }
        }
        // not found!
        return null;
    }
    
    private void setTileDetails(File file){
        currentFile = file;
    }
    
    private void setFileDetails(File file) {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        path.setText(file.getPath());
        date.setText(new Date(file.lastModified()).toString());
        size.setText(file.length() + " bytes");
        readable.setSelected(file.canRead());
        writable.setSelected(file.canWrite());
        executable.setSelected(file.canExecute());
        isDirectory.setSelected(file.isDirectory());

        isFile.setSelected(file.isFile());

        JFrame f = (JFrame)gui.getTopLevelAncestor();
        if (f!=null) {
            f.setTitle("File Browser" + " :: " + fileSystemView.getSystemDisplayName(file) );
        }

        gui.repaint();
    }
    
    private void showChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker;
        worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                    setTileData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                chunks.stream().forEach((child) -> {
                    node.add(new DefaultMutableTreeNode(child));
                });
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }
    
    private void setTileData(final File[] files){
        SwingUtilities.invokeLater(()->{
            if(fileTileModel==null){
                fileTileModel = new FileTileModel();
                if(listTiles==null){
                    listTiles = new JList(files);
                    listTiles.setFixedCellHeight(44);
                    listTiles.setFixedCellWidth(150);
                    listTiles.setVisibleRowCount(5);
                   
                    listTiles.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                    listTiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    listTiles.setCellRenderer(new TileCellRenderer());
                }
                listTiles.setModel(fileTileModel);
            }
            
            //listTiles.getSelectionMode().removeListSelectionListener(listSelectionListener);
            listTiles.getSelectionModel().removeListSelectionListener(listSelectionListener2);
            listTiles.removeMouseListener(mouseListener);
            fileTileModel.setFiles(files);
            listTiles.getSelectionModel().addListSelectionListener(listSelectionListener2);
            listTiles.addMouseListener(mouseListener);
            
            cellSizesSet = true;
        });
    }
    
    private void setTableData(final File[] files) {
        SwingUtilities.invokeLater(() -> {
            if (fileTableModel==null) {
                fileTableModel = new FileTableModel();
                if(table==null){
                    table = new JTable();
                    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    table.setShowVerticalLines(false);
                    table.setAutoCreateRowSorter(true);
                }
                table.setModel(fileTableModel);
            }
            table.getSelectionModel().removeListSelectionListener(listSelectionListener);
            table.removeMouseListener(mouseListener2);
            fileTableModel.setFiles(files);
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
            table.addMouseListener(mouseListener2);
            
            if (!cellSizesSet) {
                Icon icon = fileSystemView.getSystemIcon(files[0]);
                
                // size adjustment to better account for icons
                table.setRowHeight( icon.getIconHeight()+rowIconPadding );
                
                setColumnWidth(0,-1);
                setColumnWidth(3,60);
                table.getColumnModel().getColumn(3).setMaxWidth(120);
                setColumnWidth(4,-1);
                setColumnWidth(5,-1);
                setColumnWidth(6,-1);
                setColumnWidth(7,-1);
                setColumnWidth(8,-1);
                setColumnWidth(9,-1);
                
                cellSizesSet = true;
            }
        });
    }
    
    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width<0) {
            // use the preferred width of the header..
            JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int)preferred.getWidth()+14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }
    
    private void showErrorMessage(String errorMessage, String errorTitle) {
        JOptionPane.showMessageDialog(
            gui,
            errorMessage,
            errorTitle,
            JOptionPane.ERROR_MESSAGE
            );
        gui.repaint();
    }

    private void showThrowable(Throwable t) {
        JOptionPane.showMessageDialog(
            gui,
            t.toString(),
            t.getMessage(),
            JOptionPane.ERROR_MESSAGE
            );
        gui.repaint();
    }

    
    
    //variable section
    /* File details. */
    private JLabel fileName;
    private JTextField path;
    private JLabel date;
    private JLabel size;
    private JCheckBox readable;
    private JCheckBox writable;
    private JCheckBox executable;
    private JRadioButton isDirectory;
    private JRadioButton isFile;
    
    /* File controls. */
    private JButton openFile;
    private JButton printFile;
    private JButton editFile;
    private JButton toggleView;
    
    /** Used to open/edit/print files. */
    private Desktop desktop;
    /** Provides nice icons and names for files. */
    private FileSystemView fileSystemView;

    /** currently selected File. */
    private File currentFile ;

    /** Main GUI container */
    private JPanel gui;
    private GridLayout gl;
    private JPanel buttonPanel;

    /** File-system tree*/
    private JTree tree;
    private DefaultTreeModel treeModel;

    /** Directory listing */
    private JTable table;
    private JProgressBar progressBar;
    
    /** Table model for File[]. */
    private FileTableModel fileTableModel;
    private FileTileModel fileTileModel;
    private ListSelectionListener listSelectionListener;
    private ListSelectionListener listSelectionListener2;
    private boolean cellSizesSet = false;
    private final int rowIconPadding = 6;
    
    //tile view component
    private JPanel detailView;
    private JScrollPane tableScroll;
    private JScrollPane tileScroll;
    private JList listTiles;
    
    //mouse click detector
    private MouseListener mouseListener;
    private MouseListener mouseListener2;
    
    int flag = 1;

}
