package jScribe_final;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;

/** 
 * @author Auden Childress
 * Version: final
 * Started: March, 2018
 * Copyright 2019
 * GeistfulAutomaton@gmail.com
 */
@SuppressWarnings("serial")
public class jScribe extends JFrame implements ActionListener, ChangeListener , WindowListener, WindowStateListener, MouseListener
{
	private static byte maxOpenDocs = 5;
	private static byte maxRecentDocs = 5;
	private static byte maxUndos = 25;
    private static boolean autoFormat = false;
    private static short lineLen = -1;
    private static Dimension screen;
	private static LinkedList <Document> openCheck = new LinkedList <Document> ();
	private static JMenu recents;
	private static RecentDoc recentDocs[] = new RecentDoc[maxRecentDocs];
	private static Dimension winDims = new Dimension(1000,700);
	private static SettingsBar sb;
    public static Words wrds = new Words();
    private static JMenuBar menu = null;
    private static JMenu other[] = new JMenu[2];
    private static final int pgLimit = 200;
    
    private JTabbedPane docTab;
	private int currDoc = 0;
	private ArrayList <Document> openDocs = new ArrayList <Document> ();
    
	public static void main (String... cheese) 
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) 
		{ }
		String s = System.getProperty("java.version");
		int decimalIndex = s.indexOf('.');
		if (decimalIndex == -1)
			decimalIndex = s.length();
		int version = Integer.parseInt(s.substring(0, decimalIndex));
		if (version < 10.0)
		{
			s = "Please update to JDK or JRE 10 or later";
	    	Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	 	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) 
	 	    {
	 	        try 
	 	        {
	 	        	URL pg = new URL ("https://www.oracle.com/technetwork/java/javase/downloads/index.html");
	 	        	URL downloadLink = null;
		 	       	try 
		 	   		{
		 	       		URLConnection con = pg.openConnection();
		 	   	        con.setConnectTimeout(5000);
		 	   	        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		 	   	        String line = null;
		 	   	        while ((line = br.readLine()) != null) 
		 	   	        	if (downloadLink == null && line.matches(".*/technetwork/java/javase/downloads/jdk[0-9][0-9].*[.]html.*"))
		 	   	        	{
		 	   	        		downloadLink = new URL ("https://www.oracle.com/technetwork/java/javase/" + line.substring(line.indexOf("downloads/jdk"), line.indexOf(".html") + 5));
		 	   	        		int index = downloadLink.getPath().indexOf("jdk") + 3;
		 	   	        		s = "Java SE " + downloadLink.getPath().substring(index, index + 2) + " is the latest version.\nWould you like to update now?";
		 	   	        	}
		 	   	        br.close();
		 	   		}
		 	   		catch (Exception e)
		 	   		{ }
	 	        	int i = JOptionPane.showConfirmDialog(null, s, "Notice", JOptionPane.YES_NO_OPTION);
	 	        	if (i == JOptionPane.YES_OPTION)
	 	        		desktop.browse(downloadLink.toURI());
	 	        } 
	 	        catch (Exception e) 
	 	        { }
	 	    }
	 	    else
				JOptionPane.showMessageDialog(null, s + ".", "Notice", JOptionPane.OK_OPTION);
		}
		else
		{
			int windows = getWindows().length;
			if (windows == 0)
			{
				
				ShowLogo sl = new ShowLogo();
				 try
			    {
			    	Thread.sleep(1400);
			    }
			    catch (Exception e)
			    { }
			    sl.dispose();
			}
			else if (windows == 2)
			{
				JOptionPane.showMessageDialog(null, "The maximum number of windows \nthat can be opened is two.", "Notice", JOptionPane.NO_OPTION);
				return;
			}
			System.gc();
			SwingUtilities.invokeLater(new Runnable() 
	    	{
				@Override
				public void run() 
				{
					new jScribe(cheese);
				}
	    	});
		}
	}
	
	public jScribe (String... files) 
	{
		// call delegate constructor method
		
		setVisible(false);
		setWindowProperties();
		setComponentProperties();
		
		// Load any files given from the command line.
		
		for (String f : files)
			loadDoc(new File(f));
		setVisible(true);
	}
	
	private void setWindowProperties () 
	{
		this.addWindowListener(this);
		setLayout(new BorderLayout());
		setSize(winDims);
		setMinimumSize(new Dimension(400,300));
		this.addWindowStateListener(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		ImageIcon icon = new ImageIcon(getClass().getResource("/Icon.png"));
		setIconImage(icon.getImage());
	}
	
	private void setComponentProperties () 
	{
		docTab = new JTabbedPane();
		docTab.setForeground(new Color(85, 85, 85, 255));
		docTab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        docTab.addChangeListener(this);
		menu = new JMenuBar();
		String menuBits[][] = {
				{"File", "New", "Open", "Open Recent", "Save", "Save As", "Save All", "Print", "Close Document", "Exit"},
				{"Edit","Undo", "Redo","Copy", "Cut", "Paste", "Delete", "Select All"},
				{"Format", "Change Case"},
				{"Tools", "Word Count", "Find & Replace", "Spell Check", "Dictionary"},
				{"Window", "New Window", "Settings"},
				{"Help", "Help", "About"}};
		for (byte menuHead = 0; menuHead < menuBits.length; menuHead++) 
		{
			JMenu headToAdd = new JMenu(menuBits[menuHead][0]);
			for (byte menuItem = 1; menuItem < menuBits[menuHead].length; menuItem++) 
			{
				String s = menuBits[menuHead][menuItem];
				if (s.equals("Open Recent")) 
				{
					JMenu itemToAdd = recents = new JMenu(s);
					other[0] = itemToAdd;
					itemToAdd.add(new JMenuItem("none"));
					headToAdd.add(itemToAdd);
				} 
				else if (s.equals("Change Case")) 
				{
					JMenu itemToAdd = new JMenu(s);
					try
					{
						ImageIcon ii = new ImageIcon(getClass().getResource("/reverseCASE.png"));
						itemToAdd.setIcon(ii);
					}
					catch (Exception e)
					{ }
					itemToAdd.add(getItem("lowercase"));
					itemToAdd.add(getItem("UPPERCASE"));
					itemToAdd.add(getItem("reverseCASE"));
					itemToAdd.add(getItem("Cap Every Word"));
					other[1] = itemToAdd;
					headToAdd.add(itemToAdd);
				}
				else 
				{
					if (addSep(s))
						headToAdd.addSeparator();
					headToAdd.add(getItem(s));
				}
			}
			menu.add(headToAdd);
			JMenu jm = new JMenu("");
			jm.setVisible(false);
			menu.add(jm);
		}
		try
		{
			ImageIcon ii = new ImageIcon(getClass().getResource("/OpenRecent.png"));
			recents.setIcon(ii);
		}
		catch (Exception e)
		{ }
		pullSettings();
		Dimension d = getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle comp = ge.getMaximumWindowBounds();
        if (comp.height - 50 < d.height && comp.width - 50 < d.width)
        {
        	setSize(1000, 600);
        	this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
        else
        {
        	Point p = ge.getCenterPoint();
            int x = p.x - d.width / 2;
            int y = p.y - d.height / 2;    
            setLocation(x, y);
        }
		sb = new SettingsBar();
		add(sb, BorderLayout.NORTH);
		setJMenuBar(menu);
        add(docTab, BorderLayout.CENTER);
        newDoc();
    }
	
	public static byte getMU () 
	{
		return maxUndos;
	}

	public static void setMU (byte mu) 
	{
		maxUndos = mu;
	}
	
	public static void setAF (boolean af)
	{
		autoFormat = af;
	}
	
	public static boolean getAF ()
	{
		return autoFormat;
	}
	
	private JMenuItem getItem(String name)
	{
		JMenuItem a = new JMenuItem(name);
		try
		{
			ImageIcon ii = new ImageIcon(getClass().getResource("/" + name + ".png"));
			a.setIcon(ii);
		}
		catch (Exception e)
		{ }
		a.addActionListener(this);
		return a;
	}
	
	private static boolean addSep(String s)
	{
		String a[] = {"Print", "Exit", "Close Document", "Copy", "Select All", "Print"};
		for (String l : a)
			if (s.equals(l))
				return true;
		return false;
	}

	public static void setMOD (byte mod) 
	{
		maxOpenDocs = mod;
	}

	public static byte getMRD () 
	{
		return maxRecentDocs;
	}

	public static void setMRD (byte mrd) 
	{
		maxRecentDocs = mrd;
	}
	
	private void changeTitle () 
	{
		setTitle("jScribe - " + openDocs.get(currDoc).getDocTitle());
	}
	
	private void newDoc () 
	{
		if (docTab.getTabCount() >= maxOpenDocs)
		{
			JOptionPane.showMessageDialog(this, "Document bar full. Open a new window or \nclose a document before opening another.", "Notice", JOptionPane.NO_OPTION);
			return;
		}
		Document doc = new Document();
		openDocs.add(doc);
		docTab.addTab(doc.getDocTitle(), doc);
		changeTitle();
	}
	
	private void loadDoc (File toOpen) 
	{
		if (docTab.getTabCount() >= maxOpenDocs)
		{
			JOptionPane.showMessageDialog( this, "Document bar full. Open a new window or \nclose a document before opening another.", "Notice", JOptionPane.NO_OPTION);
			return;
		}
		Document opened = new Document (toOpen, this);
		for (Document doc : openCheck)	
			if (doc.equals(opened)) 
			{
				JOptionPane.showMessageDialog(this, toOpen.getName()+"  already open", "Notice", JOptionPane.NO_OPTION);
				return;
			}
		openCheck.add(opened);
		if (docTab.getTabCount() < maxOpenDocs) 
		{
			Document d = openDocs.get(currDoc);
			if (d.getPad().getText().length() == 0 && d.getDocPath() == null) 
			{
				d.storeDoc(toOpen);
				docTab.setTitleAt(currDoc, d.getDocTitle());
			} 
			else 
			{
				docTab.addTab(opened.getDocTitle(), opened);
				openDocs.add(opened);
			}
			cycleRecents(new RecentDoc(toOpen));
			changeTitle();
			recents.repaint();
			pushSettings();
		} 
		else
			JOptionPane.showMessageDialog(this, "Document bar full. Open a new window or \nclose a document before opening another.", "Notice", JOptionPane.NO_OPTION);
	}
	
	private void cycleRecents (RecentDoc justOpened) 
	{
		recents.removeAll();
		byte num = 0;
		for (byte pos = 0; pos < recentDocs.length; pos++) 
		{
			if (recentDocs[pos] == null)
				break;
			if (recentDocs[pos].equals(justOpened)) 
			{
				num = pos;
				break;
			} 
			else
				num = (byte) (maxRecentDocs-1);
		}
		for (byte pos = num; pos >= 0; pos--) 
		{
			if (pos == 0) 
				recentDocs[0] = justOpened;
			else 
				recentDocs[pos] = recentDocs[pos-1];
		}
		for (byte pos = 0; pos < maxRecentDocs; pos++)
			if (recentDocs[pos] != null) 
			{
				recents.add(recentDocs[pos]);
				if (recentDocs[pos].getActionListeners().length == 0) 
					recentDocs[pos].addActionListener(this);
			}
	}
	
	private void setDefaults ()
	{
		maxOpenDocs = 10;
		maxRecentDocs = 5;
		recentDocs = new RecentDoc[maxRecentDocs];
		setMU((byte) 25);
		winDims.setSize(1000,700);
		setAF(false);
	}
	
	private static void addFilters (JFileChooser it) 
	{
        it.setFileFilter(new FileNameExtensionFilter("Text File", "txt"));
        it.addChoosableFileFilter(new FileNameExtensionFilter("jScribe fastload Text File", "opus"));
        it.addChoosableFileFilter(new FileNameExtensionFilter("Windows Batch File", "bat"));
        it.addChoosableFileFilter(new FileNameExtensionFilter("Java File", "java"));
        it.addChoosableFileFilter(new FileNameExtensionFilter("Python Module File", "py"));
        it.addChoosableFileFilter(new FileNameExtensionFilter("Wavefront Object File", "obj"));
	}
	
	private static void fixRecents()
	{
		RecentDoc temp[] = recentDocs;
		recentDocs = new RecentDoc[maxRecentDocs];
		for (byte b = 0; b < temp.length && b < maxRecentDocs; b++)
			recentDocs[b] = temp[b];
	}
	
	public static void pushSettings()
	{
		if (recentDocs.length != maxRecentDocs)
			fixRecents();
		try 
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(".js_sett"), false);
			pw.print(maxOpenDocs+"\t" + maxRecentDocs+"\t" + getMU() + "\t" + winDims.width + "\t" + winDims.height + "\t" + getAF());
			for (byte b = (byte) (maxRecentDocs > recentDocs.length ? recentDocs.length : maxRecentDocs); b >= 0; b--)
			{
				try 
				{
					String s = recentDocs[b].getDocPath();
					pw.print("\t"+s);
				}
				catch (Exception e)
				{ }
			}
			pw.println();
			pw.close();
		}
		catch (Exception e) 
		{ }
		wrds.save();
	}
	
	private void pullSettings () 
	{
		File F = new File(".js_sett");
		if (!F.exists())
		{
			pushSettings();
			return;
		}
		try {
			Scanner s = new Scanner(F);
			String p[] = s.nextLine().split("\t");
			s.close();
			maxOpenDocs = (byte) Integer.parseInt(p[0]);
			maxRecentDocs = (byte) Integer.parseInt(p[1]);
			recentDocs = new RecentDoc[maxRecentDocs];
			setMU((byte) Integer.parseInt(p[2]));
			winDims.setSize(Integer.parseInt(p[3]), Integer.parseInt(p[4]));
			setSize(winDims);
			if (p[5].equals("true"))
				setAF(true);
			else
				setAF(false);
			for (byte b = 6; b < p.length; b++)
			{
				File f = new File(p[b]);
				if (f.exists())
					cycleRecents(new RecentDoc(new File(p[b])));
			}
		}
		catch (Exception e)
		{ 
			setDefaults();
			pushSettings();
		}
	}
	
	private void saveAll ()
	{
		int len = openDocs.size();
		int temp = currDoc;
		for (int i = 0; i < len; i++)
		{
			Document toSave = openDocs.get(i);
			if (toSave.isOnDisc() && toSave.wasMod())
				toSave.saveDoc();
			else if (toSave.wasMod())
			{
				currDoc = i;
				docTab.setSelectedIndex(i);
				toSave.saveDoc();
			}
		}
		currDoc = temp;
		docTab.setSelectedIndex(currDoc);
	}
	
	private void closeDoc (boolean cancelOpt)
	{
		Document d = openDocs.get(currDoc);
		if (d.pages.size() > pgLimit)
		{
			JOptionPane.showMessageDialog( this, "Document sizes have been limited to 200 pages for now", "Notice", JOptionPane.PLAIN_MESSAGE);
		}
		else
		{
			int opt = cancelOpt ? JOptionPane.YES_NO_CANCEL_OPTION : JOptionPane.YES_NO_OPTION;
			int i = JOptionPane.NO_OPTION;
			if (d.wasMod())
				i = JOptionPane.showConfirmDialog(this, "This document has been modified.\nWould you like to save changes?", "Notice", opt);
			if (i == JOptionPane.CANCEL_OPTION)
				return;
			else if (i == JOptionPane.YES_OPTION)
				d.saveDoc();
		}
		if (docTab.getTabCount() == 1 && cancelOpt) 
			this.dispose();
		else 
		{
			for (int pos = 0; pos < openCheck.size(); pos++)
				if (openCheck.get(pos).equals(d))
				{
					openCheck.remove(pos);
					break;
				}
			docTab.remove(docTab.getSelectedIndex());
			openDocs.remove(d);
		}
	}
	
	@Override
	public void actionPerformed (ActionEvent evt) 
	{
		String btnPress = evt.getActionCommand();
		Document d = openDocs.get(currDoc);
		if (evt.getSource().getClass().equals(RecentDoc.class))
			loadDoc(((RecentDoc) evt.getSource()).getFile());
		switch (btnPress) 
		{
		case "Dictionary":
			String toSearch = d.getPad().getSelectedText();
			if (toSearch == null)
				d.openDict();
			else
				d.openDict(toSearch);
			break;
		case "Word Count":
			d.wordCount();
			break;
		case "Print":
			d.print();
			break;
		case "Spell Check":
			d.openSpellCheck();
			break;
		case "Help":
			JOptionPane.showMessageDialog( this, "This feature is not implemented yet\n\n Contact me at GeistfulAutomaton@gmail.com", "Temp Help", JOptionPane.PLAIN_MESSAGE);
			
			System.out.println("--------------------------------------------------\n\n"
					+ "Runtime analysis of memory usage for fine tuning and dev purposes\n");
			String s[] = {"", "kilo", "mega", "giga"};
			long tm = Runtime.getRuntime().totalMemory();
			long mm = Runtime.getRuntime().maxMemory();
			int div = 1;
			for (String p : s)
			{
				String a = tm/div + " of ";
				String b = mm/div+ " " + p + "bytes";
				String c = "     leaving " + (mm - tm)/div + " " + p + "bytes";
				System.out.println(a + b + c + "\n");
				div*=1000;
			}
			System.out.println("--------------------------------------------------\n");
			break;
		case "Settings":
			if (sb.isShowing())
				break;
			d.closeDict();
			d.closeFindBar();
			d.closeSpellCheck();
			sb.setVisible(true);
			break;
		case "Save":
			d.saveDoc();
			docTab.setTitleAt(currDoc, d.getDocTitle());
			changeTitle();
			break;
		case "Save As":
			d.saveDoc(true);
			docTab.setTitleAt(currDoc, d.getDocTitle());
			changeTitle();
			break;
		case "Save All":
			saveAll();
			break;
		case "About":
			JOptionPane.showMessageDialog( this, "Written and Tested by Auden Childress.\nConcept modeled after Apache OpenOffice Writer and Notepad.\n\n Contact me at GeistfulAutomaton@gmail.com", "About", JOptionPane.PLAIN_MESSAGE);
			break;
		case "Open":
			JFileChooser opener = new JFileChooser();
			addFilters(opener);
			opener.setMultiSelectionEnabled(true);
			opener.setDialogTitle("Open Document");
			int choice = opener.showOpenDialog(null);
			File files[] = opener.getSelectedFiles();
			if (choice == JFileChooser.APPROVE_OPTION)
				for (File toOpen : files)
					loadDoc(toOpen);
			break;
		case "Undo":
			d.undo();
			break;
		case "Redo":
			d.redo();
			break;
		case "Close Document":
			closeDoc(true);
			break;
		case "Exit":
			this.dispose();
			break;
		case "New Window":
			main();
			break;
		case "New":
			newDoc();
			break;
		case "Copy":
			d.copy();
			break;
		case "Cut":
			d.cut();
			break;
		case "Delete":
			d.delete();
			break;
		case "Paste":
			d.paste();
			break;
		case "Select All":
			d.selectAll();
			break;
		case "lowercase":
			d.toLowerCase();
			break;
		case "UPPERCASE":
			d.toUpperCase();
			break;
		case "reverseCASE":
			d.reverseCase();
			break;
		case "Cap Every Word":
			d.capEvery();
			break;
		case "Find & Replace":
			d.search();
			break;
		}
	}

	@Override
	public void stateChanged (ChangeEvent e) 
	{
		currDoc = docTab.getSelectedIndex();
		if (currDoc == -1)
			return;
		if (this != null)
			changeTitle();
		for (int i = 0; i < docTab.getTabCount(); i++)
			docTab.setForegroundAt(i, Color.gray);
		docTab.setForegroundAt(currDoc, Color.black);
	}
	
	@Override
	public void windowClosing(WindowEvent e) 
	{
		for (Document d : openDocs)
			d.threadRun = false;
		currDoc = 0;
		docTab.setSelectedIndex(0);
		while (docTab.getTabCount() > 0)
			closeDoc(false);
		setVisible(false);
		winDims.setSize(this.getSize());
		pushSettings();
		System.gc();
	}
	
	@Override
	public void windowStateChanged(WindowEvent e) 
	{
		if (e.getNewState() == 0 && e.getOldState() == 6)
		{
			int w = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int h = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			if (this.getHeight() > 6*h/7 && this.getWidth() > 6*w/7)
				this.setSize(3*w/4,3*h/4);
		}
	}
	
	private static void deSelMenu ()
	{
		Component[] comps = menu.getComponents();
		for (Component c : comps)
		{
			JMenu jm = (JMenu) c;
			jm.setPopupMenuVisible(false);
			jm.setSelected(false);
		}
		for (JMenu jm : other)
		{
			jm.setPopupMenuVisible(false);
			jm.setSelected(false);
		}
	}
	
	// unused
	@Override public void windowActivated   (WindowEvent e) { }
	@Override public void windowClosed      (WindowEvent e) { }
	@Override public void windowDeactivated (WindowEvent e) { }
	@Override public void windowDeiconified (WindowEvent e) { }
	@Override public void windowIconified   (WindowEvent e) { }
	@Override public void windowOpened      (WindowEvent e) { }
	@Override public void mouseEntered      (MouseEvent e) 	{ }
	@Override public void mouseClicked		(MouseEvent e) 	{ }
	@Override public void mousePressed		(MouseEvent e)	{ }
	@Override public void mouseReleased		(MouseEvent e) 	{ }
	@Override public void mouseExited		(MouseEvent e) 	{ }
	
	
	
	
	


	// undo and redo could be reworked as there are better
	// ways to go about this.
	// the best other way i could think of at the time involves having a
	// string buffer that stores the keys you type until you hit a 'flagged'
	// key such at '.' or '\n'  or a mouse click at which point it stores the
	// current index minus the length and current index to the undo slot as well
	// as the string (in case of redo). deletions and paste would be handeled similarly
	//
	// people have asked that i add a shift click feature. to make this simple
	// just deselect everything and select from the last page to the clicked one

	private class Document extends JPanel implements KeyListener, MouseListener, ActionListener, MouseMotionListener
	{
		private SmallStack writeStates = new SmallStack(maxUndos);
		private JLabel docInfo;
		private File docFile;
		private FindBar fb;
	    private SpellCheckBar scb;
	    private Dictionary dict = new Dictionary();
	    private boolean modified = false;
	    private Box pager;
	    private ArrayList <JTextArea> pages = new ArrayList <JTextArea> ();
	    private ArrayList <Component> seps = new ArrayList <Component> ();
	    private int currPage = 0;
	    private JScrollPane scroller;
	    private boolean interPage = false;
	    private boolean pressing = false;
	    private int lastPage = 0;
	    private Color hc = new Color(0, 175, 50, 128);
	    private long lastClick = 0;
	    private int lastPos = 0;
	    private byte bitFlip = 0;
	    private byte selLevel;
	    public boolean threadRun = true;
	    
		public Document () 
		{
			super ();
			construct();
	        storeDoc(null, "");
		}
		
		public Document (File toOpen, jScribe p) 
		{
			super ();
			construct();
			storeDoc(toOpen);
		}
		
		private boolean wasMod ()
		{
			return modified;
		}
		
		private boolean isOnDisc ()
		{
			return docFile != null;
		}
		
		private void saveDoc () 
		{
			saveDoc (false);
		}
		
		private void saveDoc (boolean forcedSaveAs) 
		{
			boolean b = false;
			File file = docFile;
			if (file != null && file.exists() && !forcedSaveAs) 
			{
				int choice = JOptionPane.showConfirmDialog ( this, "The file " + getDocTitle() + " already exists. Would you like to save over it?","Notice", JOptionPane.YES_NO_CANCEL_OPTION);
				if (choice == JOptionPane.NO_OPTION) 
					forcedSaveAs = true;
				else if (choice != JOptionPane.YES_OPTION)
					return;
			}
			if (file == null || forcedSaveAs) 
			{
				JFileChooser saver = new JFileChooser();
				addFilters(saver);
				saver.setDialogTitle("Save Document As...");
				int choice = saver.showSaveDialog(null);
				file = saver.getSelectedFile();
				if (choice != 0)
					return;
				String ext = saver.getFileFilter().toString().substring(69);
				for (byte pos = 0; pos < ext.length(); pos++) 
				{
					// TODO use regex
					//i below i get the file extension. i could not think of a way to do this without creating a a whole separate data set for it.
					try 
					{
						if (ext.substring(pos, pos + 2).equals("=["))
							ext = "." + ext.substring(pos + 2, ext.length() - 2);
					} 
					catch (Exception e) 
					{ 
						ext = "";
					}
				}
				String path = file.getPath();
				file = new File (path.substring(0, path.indexOf(file.getName())) + path.substring(path.indexOf(file.getName())).replaceAll(ext, "") + ext);
				b = true;
			}
			try 
			{
				String processedText = "";
				for (JTextArea jta : pages)
				{
					String pieces[] = jta.getText().split("\n");
					for (String toAdd : pieces)
						processedText += toAdd + "\r\n";
				}
				processedText = processedText.substring(0, processedText.length() - 2);
				PrintWriter toFile = new PrintWriter(new FileOutputStream(file.getPath()), false);
				toFile.println(processedText);
				toFile.close();
				modified = false;
			} 
			catch (Exception e) 
			{ 
				JOptionPane.showMessageDialog(this, "Could not save Document", "Error", JOptionPane.ERROR_MESSAGE);
			}
			docFile = file;
			if (b)
				cycleRecents(new RecentDoc(docFile));
		}
		
		private void construct () 
		{
			if (screen == null)
				screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			setLayout(new BorderLayout());
			Box b = Box.createHorizontalBox();
			pager = Box.createVerticalBox();
			b.add(Box.createHorizontalGlue());
			b.add(Box.createHorizontalStrut(25));
			b.add(pager);
			b.add(Box.createHorizontalStrut(25));
			b.add(Box.createHorizontalGlue());
			pager.add(Box.createVerticalStrut(50));
			scroller = new JScrollPane(pager);
			scroller.getVerticalScrollBar().setUnitIncrement(3);
			newPg(" ");
			Box box = Box.createVerticalBox();
			box.add(Box.createVerticalStrut(1));
			scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        box.add(scroller);
	        box.add(Box.createVerticalStrut(2));
	        add(box, BorderLayout.CENTER);
	        docInfo = new JLabel(" Page  1 / " + (pages.size() + 1));
	        add(docInfo, BorderLayout.PAGE_END);
	        add(dict, BorderLayout.EAST);
	        fb = new FindBar();
	        scb = new SpellCheckBar();
	        SwingUtilities.invokeLater(new Runnable() 
	    	{
				@Override
				public void run() 
				{
					if (lineLen == -1) 
						config();
				}
	    	});
		}
		
		private void config ()
		{
			JTextArea jta = pages.get(0);
			jta.requestFocusInWindow();
			jta.setEditable(false);
			try 
			{
				for (int i = 0; i < jta.getText().length() + 1; i++)
				{
					jta.setText(jta.getText() + ' ');
					Rectangle2D R = jta.modelToView2D(0);
					Rectangle2D r = jta.modelToView2D(i);
					if (R.getY() == r.getY())
						continue;
					else if (R.getX() == r.getX())
					{
						lineLen = (short) (i - 1);
						break;
					}
				}
			} 
			catch (Exception e) 
			{ }
			jta.setText("");
			jta.setEditable(true);
		}
		
		private void gotoPg (double pg)
		{
			int i = (int) (((double) (pages.get(0).getHeight() + 25)) * (pg));
			scroller.getVerticalScrollBar().setValue(i);
		}
		
		private void gotoPgLn (double pg, double ln)
		{
			double h = pages.get(0).getHeight() + 25;
			ln /= 47.0;
			pg += ln - 0.25;
			int i = (int) (h * pg);
			scroller.getVerticalScrollBar().setValue(i);
		}
		
		private JTextArea newPg ()
		{
			return newPg("");
		}
		
		
		private JTextArea newPg (String s)
		{
			int flag = currPage == pages.size() - 1 ? 1 : 0;
			JTextArea a = new JTextArea();
			a.setText(s);
			Component c = Box.createVerticalStrut(25);
			a.setSelectedTextColor(Color.black);
			a.setSelectionColor(hc);
			a.addKeyListener(this);
			a.addMouseListener(this);
			a.addMouseMotionListener(this);
			a.setLineWrap(true);
			a.setWrapStyleWord(true);
			a.setPreferredSize(new Dimension(screen.width/2, (int) (screen.height*1.25)));
			a.setMaximumSize(new Dimension(screen.width/2, (int) (screen.height*1.25)));
			Border border = BorderFactory.createMatteBorder(1, 1, 2, 2, Color.gray);
			a.setBorder(BorderFactory.createCompoundBorder(border, 
			            BorderFactory.createEmptyBorder(60, 60, 40,60)));
			pager.add(a);
			pager.add(c);
			pages.add(a);
			seps.add(c);
			try 
			{
				updateInfo();
			} 
			catch (Exception e) 
			{ }
			if (flag == 1)
				SwingUtilities.invokeLater(new Runnable() 
		    	{
					@Override
					public void run() 
					{
						gotoPg(pages.size() - 1);
					}
		    	});
			return a;
		}
		
		private void remPg (int p)
		{
			pager.remove(pages.get(p));
			pager.remove(seps.get(p));
			pages.remove(p);
			seps.remove(p);
		}
		
		private void storeDoc (File toOpen) 
		{
			try 
			{
				JTextArea jta = getPad();
				Scanner fromFile = new Scanner(toOpen);
				String text = "";
				String s;
				double len = (double) lineLen;
				int lines = 0;
				while (fromFile.hasNextLine())
				{
					s = fromFile.nextLine();
					lines += ((s.length() / len) + 0.99);
					text += s + "\n";
					if (lines >= 47)
					{
						jta.setText(text);
						jta = newPg();
						text = "";
						lines = 0;
					}
				}
				jta.setText(text);			
				fromFile.close();
		        storeDoc(toOpen, text);
			} 
			catch (Exception e) 
			{ 
				JOptionPane.showMessageDialog(this, "Could not open  " + toOpen.getName(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		private void storeDoc (File file, String text) 
		{
			if (pages.size() > pgLimit)
				closeDoc(false);
			else
			{
				docFile = file;
				getPad().setCaretPosition(0);
				getPad().requestFocusInWindow();
				autoCorrPg (0);
			}
		}
		
		private JTextArea getPad () 
		{
			return pages.get(currPage);
		}
		
		private String getDocTitle () 
		{
			return docFile == null ? "New Document" : docFile.getName();
		}
		
		private String getDocPath () 
		{
			return docFile == null ? null : docFile.getPath();
		}
		
		private void scan()
		{
			scb.rescan();
			fb.queueScan();
		}
		
		private void print()
		{
			try 
			{
				int len = lineLen - 4;
				JTextArea b = new JTextArea();
				b.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
				String text = "";
				for (JTextArea jta : pages)
					text += jta.getText() + " ";
				String s[] = text.split(" ");
				String out = "";
				for (int i = 0, c = 0; i < s.length; i++)
				{
					c += s[i].length() + 1;
					if (c > len)
					{
						if (s[i].length() > len)
						{
							out += s[i].substring(0, len) + "\n" + s[i].substring(len) + " ";
							c = s[i].substring(len).length() + 1;
						}
						else
						{
							out += "\n" + s[i] + " ";
							c = s[i].length() + 1;
						}
					}
					else
						out += s[i] + " ";
				}
				b.setText(out);
				b.print();
			} 
			catch (Exception e) 
			{ 
				JOptionPane.showMessageDialog(this, "Could not print  " + docInfo.getText(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		private int deSel ()
		{
			int pg = 0;
			JTextArea pad = getPad();
			if (pad.getSelectionStart() == 0)
			{
				int flag = 0;
				for (int i = currPage-1; i >= 0; i--)
				{
					JTextArea jta = pages.get(i);
					if (jta.getSelectionEnd() == jta.getText().length() && jta.getSelectionStart() == 0 && flag == 0)
						continue;
					else if (jta.getSelectionEnd() == jta.getText().length() && flag == 0)
					{
						pg = i;
						flag = 1;
					}
					else
					{
						jta.getHighlighter().removeAllHighlights();
						jta.select(0,0);
					}
				}			
			}
			else
			{
				pg = currPage;
				for (int i = currPage-1; i >= 0; i--)
				{
					JTextArea jta = pages.get(i);
					jta.getHighlighter().removeAllHighlights();
					jta.select(0, 0);
				}
			}
			if (pad.getSelectionEnd() == pad.getText().length())
			{
				int flag = 0;
				int len = pages.size();
				for (int i = currPage+1; i < len; i++)
				{
					JTextArea jta = pages.get(i);
					if (jta.getSelectionEnd() == jta.getText().length() && jta.getSelectionStart() == 0)
						continue;
					else if (jta.getSelectionStart() == 0 && flag == 0)
						flag = 1;
					else
					{
						jta.getHighlighter().removeAllHighlights();
						jta.select(0,0);
					}
				}			
			}
			else
				for (int i = currPage+1; i < pages.size(); i++)
				{
					JTextArea jta = pages.get(i);
					jta.getHighlighter().removeAllHighlights();
					jta.select(0, 0);
				}
			return pg;
		}
		
		private int copy () 
		{
			int pg = deSel();
			String s = "";
			for (JTextArea jta : pages)
				if (jta.getSelectedText() != null)
					s += jta.getSelectedText() + " ";
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
			return pg;
		}
		
		private void cut () 
		{
			int pg = copy ();
			delete_b (pg);
		}
		
		private void delete_b (int pg)
		{
			int yval = scroller.getVerticalScrollBar().getValue();
			threadRun = false;
			JTextArea p = pages.get(pg);
			newState();
			for (int i = pg; i < pages.size(); i++)
			{
				JTextArea page = pages.get(i);
				if (page.getSelectionStart() == 0 && page.getSelectionEnd() == page.getText().length() && pages.size() > 1)
				{
					pages.remove(i);
					i--;
				}
				else
					page.replaceSelection("");
			}
			String s = p.getText();
			int c = p.getCaretPosition() >= s.length() ? s.length() - 1: p.getCaretPosition();
			if (c > 0)
				if (s.charAt(c) == '\n')
				{
					if (c == s.length() - 1)
						p.setText(s.substring(0, c));
					else
						p.setText(s.substring(0, c) + s.substring(c + 1));
				}
			threadRun = true;
			autoCorrPg(pg, yval);
			updateInfo();
		}
		
		private void delete () 
		{
			int pg = deSel();
			delete_b(pg);
		}
		
		private void paste () 
		{
			threadRun = false;
			int pg = deSel();
			String toPaste;
			JTextArea pad = pages.get(pg);
			try 
			{
				int i = pad.getCaretPosition();
				String s = pad.getText();
				toPaste = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				delete_b(pg);
				pad.setText(s.substring(0, i) + toPaste + s.substring(i));
				modified = true;
				pad.setCaretPosition(i + toPaste.length());
			} 
			catch (Exception e) 
			{ }
			threadRun = true;
			autoCorrPg(pg);
			updateInfo();
		}
		
		private void selectAll () 
		{
			for (JTextArea jta : pages)
			{
				jta.selectAll();
				try 
				{
					jta.getHighlighter().addHighlight(0, jta.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(hc));
				} 
				catch (Exception e) 
				{ }
			}
		}
		
		private void toLowerCase () 
		{
			deSel();
			newState();
			for (JTextArea jta : pages)
				if (jta.getSelectedText() != null)
					jta.replaceSelection(jta.getSelectedText().toLowerCase());
			scan();
		}
		
		private void toUpperCase () 
		{
			deSel();
			newState();
			for (JTextArea jta : pages)
				if (jta.getSelectedText() != null)
					jta.replaceSelection(jta.getSelectedText().toUpperCase());
			scan();
		}
		
		private void reverseCase () 
		{
			deSel();
			newState();
			for (JTextArea jta : pages)
				if (jta.getSelectedText() != null)
					jta.replaceSelection(Words.reverseCase(jta.getSelectedText()));
			scan();
		}
		
		private void capEvery () 
		{
			deSel();
			newState();
			for (JTextArea jta : pages)
				if (jta.getSelectedText() != null)
					jta.replaceSelection(Words.capEvery(jta.getSelectedText()));
			scan();
		}
		
		private String updateInfo () 
		{
			docInfo.setText(" Page  " + (currPage + 1) + " / " + pages.size());
	        return getDocTitle();
		}
		
		private void undo () 
		{
			int pg = writeStates.getPg();
			int i = pg;
			String arr[] = writeStates.pop();
			while (i < pages.size())
			{
				pages.get(i).setText(arr[i - pg]);
				i++;			
			}
			while (i < arr.length)
			{
				newPg(arr[i - pg]);
				i++;			
			}
			gotoPg(currPage);
		}
		
		private void redo ()
		{
			int pg = writeStates.getPg();
			int i = pg;
			String arr[] = writeStates.redo();
			while (i < pages.size())
			{
				pages.get(i).setText(arr[i - pg]);
				i++;			
			}
			while (i < arr.length)
			{
				newPg(arr[i - pg]);
				i++;			
			}
			gotoPg(currPage);
		}
		
		private boolean canUndo ()
		{
			return writeStates.isEmpty();
		}
		
		private boolean canRedo ()
		{
			return writeStates.canRedo();
		}
		
		private void newState () 
		{
			int n = pages.size() - currPage;
			String arr[] = new String[n];
			for (int i = 0; i < n; i++)
				arr[i] = pages.get(i + currPage).getText();
			writeStates.push(arr, currPage);
		}
		
		private boolean equals (Document it) 
		{
			if (it.docFile == null || docFile == null) 
				return false;
			return it.getDocPath().equals(getDocPath()) && it.getDocTitle().equals(getDocTitle());
		}
		
		private void format ()
		{
			SwingUtilities.invokeLater(new Runnable() 
	    	{
				@Override
				public void run() 
				{
					JTextArea pad = getPad();
					String t = pad.getText();
					int loc = pad.getCaretPosition();
					int len = t.length();
					t = Words.autoCap(t, loc);
					for (int i = 0; i < len - 3; i++)
					{
						String s0 = t.substring(i, i + 3);
						String s1 = s0.substring(0, 2);
						if (s1.charAt(0) == '\n' && Character.isLetter(s1.charAt(1)))
							t = t.substring(0, i) + s1.toUpperCase()+t.substring(i + 2);
						else if (s1.equals(". ") && Character.isLetter(s0.charAt(2)))
							t = t.substring(0, i) + s0.toUpperCase() + t.substring(i + 3);
					}
					for (int i = 0; i < len; i++)
					{
						char c = t.charAt(i);
						if (Words.isWS(c) == 1)
							continue;
						if (Character.isLetter(c))
						{
							t = t.substring(0,i) + Character.toUpperCase(c) + t.substring(i + 1);
							break;
						}
						else
							break;
					}
					pad.setText(t);
					pad.setCaretPosition(loc);
					System.gc();
				}
	    	});
		}
		
		private void search()
		{
			if (scb.isShowing())
			{
				scb.preClose();
				remove(scb);
			}
			add(fb, BorderLayout.EAST);
			remove (dict);
			add (dict, BorderLayout.WEST);
		}
		
		private void closeFindBar ()
		{
			remove (fb);
			remove (dict);
			add (dict, BorderLayout.EAST);
		}
		
		private void wordCount ()
		{
			Document d = this;
			SwingUtilities.invokeLater(new Runnable() 
	    	{
				@Override
				public void run() 
				{
					String s = "";
					for (JTextArea jta : pages)
						s += jta.getText() + " ";
					int lines = lineLen == -1 ? 1 : Words.getLineCount(s, getPad().getTabSize(), lineLen);
					char arr[] = s.toCharArray();
					int chars = -1;
					for (char c : arr)
						if ((c > 31 && c < 127) || c == '\t')
							chars++;
					String reps[] = {"\n", "\t"};
					for (String r : reps)
						while (s.indexOf(r) >= 0)
							s = s.replaceAll(r, " ");
					while (s.indexOf("  ") >= 0)
						s = s.replaceAll("  ", " ");
					String parts[] = s.split(" ");
					int words = 0;
					if (parts.length > 0 && arr.length != 0)
						for (String p : parts)
						{
							int len = p.length();
							if (len > 1)
								words++;
							else if (len != 0 && Character.isLetterOrDigit(p.charAt(0)))
								words++;
						}
					System.gc();
					JOptionPane.showMessageDialog(d, "Pages : " + pages.size() + "\nLines : " + lines + "\nWords : " + words + "\nCharacters : " + chars, "Word Count", JOptionPane.INFORMATION_MESSAGE);
				}
	    	});
		}
		
		private void autoCorrPg (int pg, int yval)
		{
			SwingUtilities.invokeLater(new Runnable() 
	    	{
				@Override
				public void run() 
				{
					corrPg(pg, yval);
					System.gc();
				}
	    	});
		}
		
		private void autoCorrPg (int pg)
		{
			int yval = scroller.getVerticalScrollBar().getValue();
			autoCorrPg(pg, yval);
		}
		
		private void corrPg (int pg, int yval)
		{
			if (pg >= pages.size())
				return;
			scroller.getVerticalScrollBar().setValue(yval);
			JTextArea jta = pages.get(pg);
			JTextArea curr = pages.get(currPage);
			int c = curr.getCaretPosition();
			String o = jta.getText();
			int lines = Words.getLineCount(o, jta.getTabSize(), lineLen);
			if (lines > 47)
			{
				int i;
				for (i = o.length() - 1; i >= 0; i--)
					if (Words.isAWS(o.charAt(i)) == 1 && i != o.length() - 1)
						break;
				if (i == 0 && Words.isAWS(o.charAt(i)) == 0)
					newPg();
				else
				{
					String s = o.substring(0, i);
					if (o.charAt(i) == '\n' || o.charAt(i) == ' ')
					{
						s += o.charAt(i);
						i++;
					}
					jta.setText(s);
					s = o.substring(i);
					if (pg + 1 >= pages.size())
					{
						newPg(s);
					}
					else
					{
						JTextArea nxt = pages.get(pg + 1);
						nxt.setText(s + nxt.getText());
					}
					if (c >= jta.getText().length() && currPage == pg)
					{
						JTextArea nxt = pages.get(pg + 1);
						nxt.requestFocusInWindow();
						nxt.setCaretPosition(s.length() - 1);
					}
					else
						curr.setCaretPosition(c);
					autoCorrPg(pg);
				}
			}
			else if (pg < pages.size() - 1)
			{
				JTextArea nxt = pages.get(pg + 1);
				String s = nxt.getText();
				int i = 0;
				if (i == s.length() && pg + 1 == pages.size() - 1)
					remPg(pg + 1);
				else
				{
					for (; i < s.length() - 1; i++)
						if (Words.isAWS(s.charAt(i)) == 1 && (i != 0 || s.charAt(i) == '\n'))
							break;
					String S = o + s.substring(0, i + 1);
					if (Words.getLineCount(S, jta.getTabSize(), lineLen) <= 47)
					{
						jta.setText(S);
						nxt.setText(s.substring(i + 1));
						if (nxt.getText().length() == 0 && pg + 1 == pages.size() - 1)
						{
							remPg(pg + 1);
							scan();
						}
						else
							autoCorrPg(pg);
						if (pg == currPage)
						{
							curr.setCaretPosition(c);
							scan();
						}
					}
					else
						autoCorrPg(pg + 1);
				}
			}
			else
			{
				updateInfo();
				scan();
			}
		}
		
		private void openSpellCheck ()
		{
			if (fb.isShowing())
				remove (fb);
			remove (dict);
			add (dict, BorderLayout.WEST);
			add (scb, BorderLayout.EAST);
			scb.prepare();
		}
		
		private void closeSpellCheck ()
		{
			remove(scb);
			remove (dict);
			add (dict, BorderLayout.EAST);
			System.gc();
		}
		
		private void openDict () 
		{
			dict.setVisible(true);
		}
		
		private void openDict (String toSearch) 
		{
			dict.setVisible(true);
			dict.queueSearch(toSearch);
		}
		
		private void closeDict () 
		{
			dict.setVisible(false);
		}
		
		private int getPage (MouseEvent e)
		{
			if (!e.getSource().getClass().equals(JTextArea.class))
				return -1;
			JTextArea jta = (JTextArea) e.getSource();
			return  pages.indexOf(jta);
		}
		
		private JMenuItem getItem(String name)
		{
			JMenuItem a = new JMenuItem(name);
			try
			{
				ImageIcon ii = new ImageIcon(getClass().getResource("/" + name + ".png"));
				a.setIcon(ii);
			}
			catch (Exception e)
			{ }
			a.addActionListener(this);
			return a;
		}
		
		private JMenu getCase ()
		{
			JMenu m = new JMenu("Change Case");
			try
			{
				ImageIcon ii = new ImageIcon(getClass().getResource("/reverseCASE.png"));
				m.setIcon(ii);
			}
			catch (Exception e)
			{ }
			m.add(getItem("lowercase"));
			m.add(getItem("UPPERCASE"));
			m.add(getItem("reverseCASE"));
			m.add(getItem("Cap Every Word"));
			return m;
		}
		
		private void selectWord ()
		{
			JTextArea jta = getPad();
			char arr[] = jta.getText().toCharArray();
			int a = 0, b = 0;
			for (a = lastPos - 1; a > 0; a--)
			{
				if (Words.isAWS(arr[a-1]) == 1)
					break;
			}
			for (b = lastPos; b < arr.length; b++)
			{
				if (Words.isAWS(arr[b]) == 1)
					break;
			}
			jta.select(a, b);
		}
		
		private void selectOther (char c)
		{
			JTextArea jta = getPad();
			char arr[] = jta.getText().toCharArray();
			int a = 0, b = 0;
			for (a = lastPos - 1; a > 0; a--)
			{
				if (arr[a-1] == c || arr[a-1] == '\n')
					break;
			}
			for (b = lastPos; b < arr.length; b++)
			{
				if (arr[b] == c || arr[b] == '\n')
					break;
			}
			if (c == '.')
				jta.select(a, b);
			else
				jta.select(a, b + 1);
		}
		
		@Override
		public void keyPressed (KeyEvent k) 
		{
			int id = k.getKeyCode();
			if (pages.size() > pgLimit && (id != KeyEvent.VK_BACK_SPACE || id != KeyEvent.VK_DELETE))
			{
				JOptionPane.showMessageDialog( this, "Document sizes have been limited to 200 pages for now", "Notice", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			JTextArea pad = getPad();
			char thisKey = k.getKeyChar();
			if (thisKey == '\b' && currPage != 0 && pad.getCaretPosition() == 0)
			{
				k.consume();
				SwingUtilities.invokeLater(new Runnable() 
		    	{
					@Override
					public void run() 
					{
						currPage--;
						updateInfo();
						JTextArea jta = getPad();
						jta.requestFocusInWindow();
						jta.setCaretPosition(jta.getText().length());
					}
		    	});
				return;
			}
			else if (thisKey == KeyEvent.VK_DELETE && currPage < pages.size() - 1 && pad.getSelectionStart() == pad.getSelectionEnd() && pad.getCaretPosition() == pad.getText().length() )
			{
				k.consume();
				JTextArea jta = pages.get(currPage + 1);
				jta.setText(jta.getText().substring(1));
				return;
			}
			boolean ctrl = k.isControlDown();
			modified = true;
			threadRun = false;
			if (ctrl)
			{
				if (id == KeyEvent.VK_C)
				{
					k.consume();
					copy();
					return;
				}
				else if (id == KeyEvent.VK_Z)
				{
					k.consume();
					undo();
					return;
				}
				else if (id == KeyEvent.VK_V)
				{
					k.consume();
					newState();
					return;
				}
				else if (id == KeyEvent.VK_S)
				{
					k.consume();
					saveDoc();
					return;
				}
				else if (id == KeyEvent.VK_P)
				{
					k.consume();
					print();
					return;
				}
				else if (id == KeyEvent.VK_F)
				{
					search();
					k.consume();
					return;
				}
			}
			else if (!(id > 36 && id < 41))
			{
				boolean isDel = id == KeyEvent.VK_DELETE;
				if (pad.getSelectedText() != null)
				{
					if (thisKey > 31 && thisKey < 127)
						delete();
					else if (isDel ||  thisKey == '\b')
					{

						int i = pad.getSelectionStart();
						newState();
						delete();
						k.consume();
						SwingUtilities.invokeLater(new Runnable() 
				    	{
							@Override
							public void run() 
							{
								int len = pad.getText().length();
								pad.setCaretPosition(i >= len ? len - 1 : i);
							}
				    	});
					}
				}
				if (autoFormat && Words.isAWS(thisKey) == 1)
					format();
			}
			if (thisKey != ' ' && Words.isGrammer(thisKey) == 1)
				newState();
			if (thisKey == '\n' && pad.getCaretPosition() == pad.getText().length() && Words.getLineCount(pad.getText(), pad.getTabSize(), lineLen) == 47)
			{
				JTextArea nxt;
				if (currPage == pages.size() - 1)
					nxt = newPg();
				else
				{
					nxt = pages.get(currPage + 1);
					nxt.setText('\n' + nxt.getText());
				}
				nxt.requestFocusInWindow();
				nxt.setCaretPosition(0);
				k.consume();
				currPage++;
			}
			threadRun = true;
			autoCorrPg(currPage);
		}
		
		@Override
		public void mousePressed(MouseEvent e) 
		{
			if (!SwingUtilities.isRightMouseButton(e))
			{
				pressing = true;
				JTextArea jta = (JTextArea) e.getSource();
				jta.getHighlighter().removeAllHighlights();
				int pg = pages.indexOf(jta);
				if (pg != currPage)
				{
					for (JTextArea page : pages)
						page.getHighlighter().removeAllHighlights();
					currPage = pg;
				}
			}
		}
		
		@Override 
		public void mouseEntered  (MouseEvent e) 
		{
			deSelMenu();
			interPage = false;
			int i = getPage(e);
			lastPage = pressing ? (i >= pages.size() ? lastPage : i) : lastPage;
		}
		
		@Override 
		public void mouseExited   (MouseEvent e) 
		{
			if (pressing)
				for (int p = 0; p < pages.size(); p++)
					if ((currPage < lastPage && (p < currPage || p >= lastPage) || (currPage > lastPage && (p > currPage || p <= lastPage))))
						pages.get(p).getHighlighter().removeAllHighlights();
			interPage = true;
			lastPage = currPage >= pages.size() ? lastPage : currPage;
		}
		
		@Override 
		public void mouseReleased (MouseEvent e) 
		{
			pressing = false;
			if (SwingUtilities.isRightMouseButton(e) || interPage || lastPage == currPage)
				return;
			JTextArea jta = pages.get(lastPage);
			Point p = jta.getMousePosition();
			try
			{
				jta.setCaretPosition(jta.viewToModel2D(p));
			}
			catch (Exception egf)
			{
				e.consume();
				return;
			}
			if (lastPage > currPage)
			{
				for (int i = currPage + 1; i < lastPage; i++)
				{
					JTextArea ja = pages.get(i);
					ja.select(0, ja.getText().length());
					try 
					{
						ja.getHighlighter().addHighlight(0, ja.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(hc));
					} 
					catch (Exception e1) 
					{ }
				}
				jta.select(0, jta.getCaretPosition());
			}
			else if (lastPage < currPage)
			{
				for (int i = currPage - 1; i > lastPage; i--)
				{
					JTextArea ja = pages.get(i);
					ja.select(0, ja.getText().length());
					try 
					{
						ja.getHighlighter().addHighlight(0, ja.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(hc));
					} 
					catch (Exception e1) 
					{ }
				}
				jta.select(jta.getCaretPosition(), jta.getText().length());
			}
			currPage = lastPage;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			threadRun = false;
			String btnPress = e.getActionCommand();
			switch (btnPress) 
			{
			case "Search":
				String toSearch = getPad().getSelectedText();
				if (toSearch == null)
					openDict();
				else
					openDict(toSearch);
				break;
			case "Copy":
				copy();
				break;
			case "Cut":
				cut();
				break;
			case "Delete":
				delete();
				break;
			case "Paste":
				paste();
				break;
			case "Select All":
				selectAll();
				break;
			case "lowercase":
				toLowerCase();
				break;
			case "UPPERCASE":
				toUpperCase();
				break;
			case "reverseCASE":
				reverseCase();
				break;
			case "Cap Every Word":
				capEvery();
				break;
			case "Undo":
				undo();
				break;
			case "Redo":
				redo();
				break;
			}
			threadRun = true;
		}
		
		@Override 
		public void mouseClicked  (MouseEvent e) 
		{
			int pg = getPage(e);
			currPage = pg >= pages.size() ? currPage : pg;
			updateInfo();
			if (SwingUtilities.isRightMouseButton(e)) 
			{
				JPopupMenu m = new JPopupMenu();
				boolean b = getPad().getSelectedText() != null;
				if (canRedo() || canUndo())
				{	
					if (canUndo())
						m.add(getItem("Undo"));
					if (canRedo())
						m.add(getItem("Redo"));
					m.addSeparator();
				}
				if (b)
				{
					m.add(getItem("Copy"));
					m.add(getItem("Cut"));
				}
				boolean canPaste = true;
				try 
				{
					String tmp = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
					canPaste = tmp == null ? false : tmp.length() != 0;
				}
				catch (Exception err) 
				{ 
					canPaste = false;
				}
				if (canPaste)
					m.add(getItem("Paste"));
				if (b)
					m.add(getItem("Delete"));
				if (!b && pages.get(0).getText().length() != 0)
					m.add(getItem("Select All"));
				if (b)
				{
					m.addSeparator();
					m.add(getCase());
					m.add(getItem("Search"));
				}
			    m.show(e.getComponent(), e.getX(), e.getY());
			}
			else
			{
				for (JTextArea jta : pages)
				{
					jta.getHighlighter().removeAllHighlights();
					int i = jta.getCaretPosition();
					jta.select(i, i);
				}
				closeDict();
				bitFlip = 1;
				pressing = true;
				long l = System.currentTimeMillis();
				if (l - lastClick < 500 && selLevel != 4)
				{
					SwingUtilities.invokeLater(new Runnable() 
			    	{
						@Override public void run() 
						{
							selLevel++;
							switch (selLevel)
							{
							case 1:
								selectWord();
								break;
							case 2:
								selectOther('.');
								break;
							case 3:
								selectOther('\n');
								break;
							}
						}
			    	});
				}
				else
				{
					bitFlip = 0;
					selLevel = 0;
				}
				if (bitFlip == 0)
					lastPos = getPad().getCaretPosition();
				lastClick = l;
			}
		}

		@Override
		public void mouseDragged(MouseEvent arg0) 
		{
			if (currPage == lastPage)
				return;
			JTextArea jta = pages.get(lastPage);
			Point p = jta.getMousePosition();
			int pos = jta.getCaretPosition();
			try
			{
				jta.setCaretPosition(jta.viewToModel2D(p));
				if (jta.getCaretPosition() == pos)
					return;
			}
			catch (Exception any)
			{
				return;
			}
			Highlight[] hl = jta.getHighlighter().getHighlights();
			try {
				if (hl.length == 0)
				{
					
					if (currPage > lastPage)
						jta.getHighlighter().addHighlight(jta.getCaretPosition(), jta.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(hc));
					else
						jta.getHighlighter().addHighlight(0, jta.getCaretPosition(), new DefaultHighlighter.DefaultHighlightPainter(hc));
				}
				else
				{
					if (currPage > lastPage)
						jta.getHighlighter().changeHighlight(hl[0], jta.getCaretPosition(), jta.getText().length());
					else
						jta.getHighlighter().changeHighlight(hl[0], 0, jta.getCaretPosition());
				}
			} 
			catch (Exception e1) 
			{ }
		}

		// THE METHODS BELOW ARE UNUSED BUT REQUIRED VIA IMPLEMENTATION
		
		@Override public void keyReleased (KeyEvent e)		{ }
		@Override public void keyTyped    (KeyEvent e)      { }
		@Override public void mouseMoved  (MouseEvent e)    { }
		

		
		
		
		
		
		
		private class FindBar extends JToolBar implements ActionListener, KeyListener
		{
		    private JLabel count = new JLabel("0  of  0");
			private JTextField find_TF = new JTextField(5);
			private JTextField replace_TF = new JTextField(5);
		    public JButton exit_B = new JButton("X");
		    private JCheckBox case_CB = new JCheckBox ("Case Sensitive");
		    private int currHL = 0, currPg = 0;
		    
		    public FindBar ()
		    {
		    	super();
		    	JButton next_B = new JButton ("Next");
				JButton prev_B = new JButton ("Previous");
				JButton replace_B = new JButton ("Replace");
				JButton replaceA_B = new JButton ("Replace All");
		        next_B.addActionListener(this);        
		        prev_B.addActionListener(this);
		        replace_B.addActionListener(this);
		        replaceA_B.addActionListener(this);
		        exit_B.addActionListener(this);
		        find_TF.addKeyListener(this);
		        case_CB.addActionListener(this);
				setFloatable(false);
				setLayout(new BorderLayout());
				Box box = Box.createHorizontalBox();
			    box.add(Box.createHorizontalGlue());
			    exit_B.setForeground(Color.red);
			    box.add(exit_B);
			    add(box, BorderLayout.NORTH);
			    Panel toAdd = new Panel();
			    Box group = Box.createVerticalBox();
			    group.add(Box.createVerticalStrut(25));
			    box = Box.createHorizontalBox();
			    Box subBox = Box.createVerticalBox();
			    subBox.add(new JLabel(" To Find: "));
			    subBox.add(Box.createVerticalStrut(5));
			    subBox.add(new JLabel(" Replace with: "));
			    box.add(subBox);
			    subBox = Box.createVerticalBox();
			    subBox.add(find_TF);
			    subBox.add(Box.createVerticalStrut(5));
			    subBox.add(replace_TF);
			    box.add(subBox);
			    group.add(box);
			    group.add(Box.createVerticalStrut(15));
			    box = Box.createHorizontalBox();
			    box.add(next_B);
			    box.add(Box.createHorizontalGlue());
			    box.add(prev_B);
			    group.add(box);
			    box = Box.createHorizontalBox();
			    box.add(replace_B);
			    box.add(Box.createHorizontalGlue());
			    box.add(replaceA_B);
			    group.add(box);
			    box = Box.createHorizontalBox();
			    group.add(Box.createVerticalStrut(5));
			    box.add(case_CB);
			    group.add(box);
			    toAdd.add(group);
			    toAdd.add(Box.createVerticalGlue());
			    box = Box.createVerticalBox();
			    subBox = Box.createHorizontalBox();
			    subBox.add(Box.createHorizontalGlue());
			    subBox.add(count);
			    subBox.add(Box.createHorizontalGlue());
			    box.add(subBox);
			    box.add(Box.createVerticalStrut(2));
			    add(toAdd, BorderLayout.CENTER);
			    add(box, BorderLayout.SOUTH);
		    }
		    
		    private void revert ()
		    {
		    	clearHL();
				count.setText("0  of  0");
				find_TF.setText("");
				replace_TF.setText("");
		    }
		    
		    private void clearHL()
		    {	
		    	for (JTextArea jta : pages)
		    		jta.getHighlighter().removeAllHighlights();
		    }
		    
		    private void h (char page[], char word[], Highlighter hl, HighlightPainter p) throws Exception
		    {
		    	for (int i = 0; i <= page.length - word.length; i++)
		    	{
		    		if (page[i] == word[0])
		    		{
		    			int flag = 0;
		    			while (flag < word.length && page[i+flag] == word[flag])
		    				flag++;
		    			if (flag == word.length)
		    			{
		    				hl.addHighlight(i, i + word.length, p);
		    				i += (word.length - 1);
		    			}
		    		}
		    	}
		    }
			
			private void queueScan()
			{
				if (isShowing())
				{
					SwingUtilities.invokeLater(new Runnable() 
			    	{
						@Override
						public void run() 
						{
							scan();
						}
			    	});
				}
			}
			
			private void scan () 
			{
				String s = find_TF.getText();
				clearHL();
				if (s.length() != 0)
					for (JTextArea jta : pages)
						scan (jta, s);
				else
					count.setText("0  of  0");
			}
			
			private void scan (JTextArea jta, String word) 
			{
				SwingUtilities.invokeLater(new Runnable() 
		    	{
					@Override
					public void run() 
					{
						if (!threadRun)
							return;
						String text = jta.getText();
						jta.getHighlighter().removeAllHighlights();
						int wLen = word.length();
						if (text.length() < wLen || text.length() == 0 || wLen == 0)
							return;
						Highlighter HL = jta.getHighlighter();
						HighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
						try 
						{
							if (case_CB.isSelected())
								h (text.toCharArray(), word.toCharArray(), HL, p);
							else
								h (text.toLowerCase().toCharArray(), word.toLowerCase().toCharArray(), HL, p);
							Highlight hls[] = HL.getHighlights();
							if (currHL >= hls.length)
								currHL--;
							HighlightPainter p2 = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
							if (hls.length == 0)
								return;
							int start = hls[currHL].getStartOffset();
							if (currPg == pages.indexOf(jta))
							{
								HL.addHighlight(start, hls[currHL].getEndOffset(), p2);
								HL.changeHighlight(hls[currHL], start, start);
							}
							else
								HL.addHighlight(0, 0, p2);
							updateCount();
						} 
						catch (Exception e) 
						{ }
					}
		    	});
			}
			
			private void moveCurr (int direction) 
			{
				JTextArea jta = pages.get(currPg);
				Highlighter HL = jta.getHighlighter();
				Highlight hls[] = HL.getHighlights();
				try
				{
					currHL = currHL < 0 ? 0 : currHL;
					int end = hls.length - 1;
					HL.changeHighlight(hls[currHL], hls[end].getStartOffset(), hls[end].getEndOffset());
					if (direction == 1 && currHL == end - 1)
					{
						if (currPg < pages.size() - 1)
							currPg++;
						else
							currPg = 0;
						currHL = 0;
						jta = pages.get(currPg);
						HL = jta.getHighlighter();
						hls = HL.getHighlights();
						end = hls.length - 1;
					}
					else if (direction == -1 && currHL == 0)
					{
						if (currPg > 0)
							currPg--;
						else
							currPg = pages.size() - 1;
						jta = pages.get(currPg);
						HL = jta.getHighlighter();
						hls = HL.getHighlights();
						end = hls.length - 1;
						currHL = end - 1;
					}
					else
						currHL += direction;
					if (hls.length == 0)
					{
						int i = currPg;
						while (true)
						{
							i += direction;
							i = i < 0 ? pages.size() - 1 : i == pages.size() ? 0 : i;
							if (pages.get(i).getHighlighter().getHighlights().length > 1)
								break;
						}
						currPg = i;
						currHL = 0;
						jta = pages.get(currPg);
						HL = jta.getHighlighter();
						hls = HL.getHighlights();
						end = hls.length - 1;
					}
					int start = hls[currHL].getStartOffset();
					HL.changeHighlight(hls[end], start, hls[currHL].getEndOffset());
					HL.changeHighlight(hls[currHL], start, start);
					String s = jta.getText();
					int t = jta.getTabSize();
					int i = hls[currHL].getEndOffset() + (find_TF.getText().length() / 2);
					int ln = Words.getLineCount(s.substring(0, i) , t, lineLen);
					gotoPgLn((double) currPg, ln);
					updateCount();
				} 
				catch (Exception e) 
				{ }
			}
			
			private void replace () 
			{
				SwingUtilities.invokeLater(new Runnable() 
		    	{
					@Override
					public void run() 
					{
						JTextArea jta = pages.get(currPg);
						Highlighter HL = jta.getHighlighter();
						Highlight hls[] = HL.getHighlights();
						if (currHL < 0)
						{
							if (hls.length > 0)
								currHL = 0;
							else return;
						}
						int start = hls[currHL].getStartOffset();
						int end = start + find_TF.getText().length();
						String s = jta.getText();
						jta.setText(s.substring(0, start) + replace_TF.getText() + s.substring(end));
						autoCorrPg(currPg);
					}
		    	});
				queueScan();
			}
			
			private void replaceAll ()
			{
				int start = 0;
				int size = pages.size();
				for (int i = 0; i < pages.size(); i++)
				{
					if (pages.get(i).getHighlighter().getHighlights().length != 0)
					{
						start = i;
						break;
					}
				}
				clearHL();
				currHL = 0;
				int r = start;
				SwingUtilities.invokeLater(new Runnable() 
		    	{
					@Override
					public void run() 
					{
						for (int i = r; i < size; i++)
						{
							JTextArea jta = pages.get(i);
							String s = jta.getText();
							String word = find_TF.getText();
							String rep = replace_TF.getText();
							jta.setText(repA(s, word, rep));
						}
						System.gc();
						count.setText("0  of  0");
						autoCorrPg(r);
					}
		    	});
			}
			
			private String repA (String s, String word, String rep)
			{
				int index = case_CB.isSelected() ? s.indexOf(word) : s.toLowerCase().indexOf(word.toLowerCase());
				if (index == -1)
					return s;
				String r = s.substring(0, index) + rep + repA(s.substring(index + word.length()), word, rep);
				return r;
			}
			
			private void updateCount () 
			{
				int found = 0;
				int buff = 1;
				for (int i = 0; i < pages.size(); i++)
				{
					int l = pages.get(i).getHighlighter().getHighlights().length - 1;
					l = l >= 0 ? l : 0;
					if (i < currPg)
						buff += l;
					found += l;
				}
				count.setText((currHL + buff) + "  of  " + found);			
			}

			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				String btnPress = evt.getActionCommand();
				switch (btnPress) 
				{
				case "X":
					currHL = 0;
					revert();
					clearHL();
					closeFindBar();
					break;
				case "Next":
					moveCurr(1);
					break;
				case "Previous":
					moveCurr(-1);
					break;
				case "Replace":
					replace();
					break;
				case "Replace All":
					replaceAll();
					break;
				case "Case Sensitive":
					queueScan();
					SwingUtilities.invokeLater(new Runnable() 
			    	{
						@Override
						public void run() 
						{
							currHL = 0;
							JTextArea jta = pages.get(currPg);
							String s = jta.getText();
							int t = jta.getTabSize();
							Highlighter HL = jta.getHighlighter();
							Highlight hls[] = HL.getHighlights();
							int i = hls[currHL].getEndOffset() + (find_TF.getText().length() / 2);
							int ln = Words.getLineCount(s.substring(0, i) , t, lineLen);
							gotoPgLn((double) currPg, ln);
						}
			    	});
					break;
				}
			}

			@Override
			public void keyPressed (KeyEvent k) 
			{
				queueScan();
			}

			// unused
			@Override public void keyReleased (KeyEvent arg0) { }
			@Override public void keyTyped    (KeyEvent arg0) {	}
		}

		
		
		
		


		private class SpellCheckBar extends JToolBar implements ActionListener, ListSelectionListener
		{
		    private JTextField found_TF = new JTextField(5);
			private JTextField replace_TF = new JTextField(5);
		    private DefaultListModel <String> lm = new DefaultListModel <String> ();
		    private JList <String> suggs = new JList <String> (lm);
		    private JScrollPane sug = new JScrollPane(suggs);
			private LinkedList <String> alt = new LinkedList <String> ();
			private LinkedList <String> al = new LinkedList <String> ();
			private LinkedList <String> ig = new LinkedList <String> ();
			private ArrayList <word> words = new ArrayList <word> ();
			private byte pres = 0;
			private int presPg = -1;
			private int presPos = -1;
			private int flag = 0;
			private String aRep;
			private byte scanning = 0;
			
		    private class word
		    {
		    	int start = -1;
		    	int end = -1;
		    	int page = -1;
		    	String word = "";
		    	
		    	public word (int pg, String w, int s, int e)
		    	{
		    		page = pg;
		    		word = w;
		    		start = s;
		    		end = e;
		    		validate();
		    	}
		    	
		    	private void validate () 
		    	{
		    		if (word.length() == 0)
		    		{
		    			end = -1;
		    			return;
		    		}
		    		char izard[] = word.toCharArray();
		    		for (char mander : izard)
		    			if (!Character.isLetterOrDigit(mander))
		    			{
		    				end = -1;
		    				break;
		    			}
		    	}
		    }
		    
		    public SpellCheckBar ()
		    {
		    	super();
		    	found_TF.setEditable(false);
		    	found_TF.setBackground(Color.white);
		    	suggs.addListSelectionListener(this);
		        suggs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        JButton exit_B = new JButton("X");
		        JButton add_B = new JButton ("Add");
		    	JButton ignore_B = new JButton ("Ignore");
				JButton ignoreA_B = new JButton ("Ignore All");
				JButton replace_B = new JButton ("Replace");
				JButton replaceA_B = new JButton ("Replace All");
				add_B.addActionListener(this);
		        ignore_B.addActionListener(this);        
		        ignoreA_B.addActionListener(this);
		        replace_B.addActionListener(this);
		        replaceA_B.addActionListener(this);
		        exit_B.addActionListener(this);
				setFloatable(false);
				setLayout(new BorderLayout());
				Box box = Box.createHorizontalBox();
			    box.add(Box.createHorizontalGlue());
			    exit_B.setForeground(Color.red);
			    box.add(exit_B);
			    add(box, BorderLayout.NORTH);
			    Panel toAdd = new Panel();
			    Box group = Box.createVerticalBox();
			    group.add(Box.createVerticalStrut(25));
			    box = Box.createHorizontalBox();
			    Box subBox = Box.createVerticalBox();
			    subBox.add(new JLabel(" Found: "));
			    subBox.add(Box.createVerticalStrut(5));
			    subBox.add(new JLabel(" Replace with: "));
			    box.add(subBox);
			    subBox = Box.createVerticalBox();
			    subBox.add(found_TF);
			    subBox.add(Box.createVerticalStrut(5));
			    subBox.add(replace_TF);
			    box.add(subBox);
			    group.add(box);
			    group.add(Box.createVerticalStrut(15));
			    box = Box.createHorizontalBox();
			    box.add(ignore_B);
			    box.add(Box.createHorizontalGlue());
			    box.add(ignoreA_B);
			    box.add(Box.createHorizontalGlue());
			    box.add(add_B);
			    group.add(box);
			    box = Box.createHorizontalBox();
			    box.add(Box.createHorizontalGlue());
			    box.add(replace_B);
			    box.add(Box.createHorizontalGlue());
			    box.add(replaceA_B);
			    box.add(Box.createHorizontalGlue());
			    group.add(box);
			    JPanel jp = new JPanel();
			    jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
			    toAdd.add(group);
			    jp.add(toAdd);
			    box = Box.createHorizontalBox();
			    box.add(Box.createHorizontalStrut(5));
			    box.add(sug);
			    box.add(Box.createHorizontalStrut(5));	    
			    jp.add(box);
			    add(jp, BorderLayout.CENTER);
		    }
		    
		    private void prepare ()
		    {
		    	setVisible (false);
		    	scan();
		    }
		    
		    private void rescan()
		    {
		    	if (flag == 1)
		    	{
		    		String temp = "";
					int len = aRep.length();
					for (int i = 0; i < len; i++)
						temp += (char) 0;
		    		String w = words.get(0).word;
		    		for (int i = 0; i < pages.size(); i++)
		    		{
		    			JTextArea page = pages.get(i);
		    			String s = page.getText();
		    			s = s.replace(temp, w);
		    			int index = s.indexOf(w);
		    			if (index != -1)
		    			{
		    				if (presPos == -1)
		    				{
		    					presPg = i;
		    					presPos = index;
		    				}
							page.setText(s.replace(w, aRep));
		    			}
					}
		    		for (int i = 0; i < words.size(); i++)
		    			if (words.get(i).word.equals(w))
		    			{
		    				words.remove(i);
		    				i--;
		    			}
		    		flag = 0;
		    	}
		    	else if (isShowing())
		    	{
			    	words.clear();
			    	scan();
		    	}
		    }
		    
		    private void getNext ()
			{
		    	while (words.isEmpty() && scanning == 1)
		    	{ }
		    	
		    	if (words.isEmpty())
		    	{
					JOptionPane.showMessageDialog(null, "Spell check has finished running.", "Notice", JOptionPane.PLAIN_MESSAGE);
					preClose();
		    		return;
		    	}
				reset();
				word wd = words.get(0);
				String w = wd.word;
				found_TF.setText(w);
				try 
				{
					int pg = wd.page;
					JTextArea jta = pages.get(pg);
					Highlighter hl = jta.getHighlighter();
					hl.removeAllHighlights();
					int pos = (wd.start + wd.end) / 2;
					String s = jta.getText();
					double ln = Words.getLineCount(s.substring(0, pos), jta.getTabSize(), lineLen);
					gotoPgLn(pg, ln);
					
					hl.addHighlight(wd.start, wd.end, new DefaultHighlighter.DefaultHighlightPainter(Color.magenta));
				} 
				catch (Exception e) 
				{ }
				wrds.getList(w, al);
				if (al.isEmpty())
				{
					wrds.suggs(w, alt);
					thirdOption(w);
				}
				if (al.isEmpty())
					while (!alt.isEmpty())
						lm.addElement(alt.remove(0));
				else
					while (!al.isEmpty())
						lm.addElement(al.remove(0));
			}
		    
		    private String[] getProp (int pg)
			{
				String s = pages.get(pg).getText();
				while (s.contains("\n"))
					s = s.replace("\n", " ");
				while (s.contains("\t"))
					s = s.replace("\t", " ");
				return s.split(" ");
			}
		    
		    private void scan ()
		    {
		    	SwingUtilities.invokeLater(new Runnable() 
		    	{
		    		@Override
					public void run() 
					{
		    			scanning = 1;
		    			int buff = 0;
		    			byte found = 0;
		    			for (int pg = 0; threadRun && pg < pages.size(); pg++)
						{
		    				buff = 0;
		    				int startPos = 0;
							String text[] = getProp(pg);
							if (pres == 1)
								while (pg < presPg)
									pg++;
					    	for (int pos = startPos; threadRun && pos < text.length; pos++)
					    	{
					    		word w = new word(pg, text[pos], buff, buff + text[pos].length());
					    		if (w.end != -1 && found(w.word) == 0 && !ig.contains(w.word))
					    		{
					    			if (pres == 1)
					    			{
					    				if (w.start >= presPos)
					    					pres = 0;
					    			}
					    			else
					    			{
						    			words.add(w);
						    			if (found == 0)
						    			{
						    				getNext();
						    				if (!isShowing())
						    					setVisible(true);
						    				found = 1;
						    			}
					    			}
					    		}
					    		buff += text[pos].length() + 1;
					    	}
						}
						scanning = 0;
		    			if (found == 0)
		    			{
		    				JOptionPane.showMessageDialog(null, "Spell check has finished running.", "Notice", JOptionPane.PLAIN_MESSAGE);
		    				preClose();
		    			}
					}
		    	});
		    }
		    
		    private int found (String a)
			{
		    	if (a.isEmpty())
		    		return 1;
		    	else if (Words.isWS(a.charAt(0)) == 1)
		    		return 1;
		    	else if (wrds.isWord(a) == 1)
		    		return 1;
		    	return 0;
			}
		    
		    private void thirdOption (String s)
		    {
		    	ArrayList <String> toAdd = new ArrayList <String> ();
		    	for (int i = 0; i < alt.size(); i++)
		    		for (int j = i+1; j < alt.size(); j++)
		    		{
		    			String w = words.get(0).word.toLowerCase();
		    	    	String a = alt.get(i);
		    	    	int la = a.length();
		    	    	String b = alt.get(j);
		    	    	int lb = b.length();
		    	    	if (la + lb == w.length())
		    	    	{
		    	    		String A = a.toLowerCase();
		    	    		String B = b.toLowerCase();
		        	    	if (w.equals(A+B))
		        	    		toAdd.add(s.substring(0,a.length())+" "+b);
		        	    	else if (w.equals(B+A))
		        	    		toAdd.add(s.substring(0,b.length())+" "+a);
		    	    	}
		    		}
		    	for (String str : toAdd)
		    		al.add(str);
		    }

			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				String btnPress = evt.getActionCommand();
				if (words.size() == 0)
					return;
				word wd = words.get(0);
				int pg = wd.page;
				String w = wd.word;
				JTextArea jta = pages.get(pg);
				String text = jta.getText();
				switch (btnPress) 
				{
				case "X":
					threadRun = false;
					preClose();
					break;
				case "Add":
					wrds.add(w);
				case "Ignore All":
					ig.add(words.get(0).word);
					for (int i = 1; i < words.size(); i++)
						if (words.get(i).word.toLowerCase().equals(w.toLowerCase()))
						{
							words.remove(i);
							i--;
						}
				case "Ignore":
					words.remove(0);
					getNext();
					break;
				case "Replace All":
					if (suggs.getSelectedIndex() == -1 && replace_TF.getText().isEmpty())
						suggs.setSelectedIndex(0);
					aRep = replace_TF.getText();
					presPg = pg;
					presPos = -1;
					JTextArea page;
					String temp = "";
					int len = aRep.length();
					for (int i = 0; i < len; i++)
						temp += (char) 0;
					for (int i = pg; i < pg; i++)
					{
						page = pages.get(i);
						page.setText(page.getText().replace(w, temp));
					}
					page = pages.get(pg);
					String s = page.getText();
					int index = s.indexOf(w);
					int shift = replace_TF.getText().length() - w.length();
					while (index != -1 && index < wd.start)
					{
						s.replaceFirst(w, temp);
						index = s.indexOf(w);
						wd.start += shift;
						wd.end += shift;
					}
					flag = pres = 1;
					autoCorrPg(presPg);
					break;
				case "Replace":
					if (suggs.getSelectedIndex() == -1 && replace_TF.getText().isEmpty())
						suggs.setSelectedIndex(0);
					jta.setText(text.substring(0, words.get(0).start)+replace_TF.getText()+text.substring(words.get(0).end));
					pres = 1;
					presPg = pg;
					presPos = wd.start;
					autoCorrPg(pg);
					break;
				}
			}
			
			private void reset ()
			{
				alt.clear();
				al.clear();
				replace_TF.setText("");
				lm.clear();
				gotoPg(0.0);
			}
			
			private void preClose ()
			{
				setVisible(false);
				closeSpellCheck();
				ig.clear();
				words.clear();
				reset();
				found_TF.setText("");
				for (JTextArea jta : pages)
					jta.getHighlighter().removeAllHighlights();
				threadRun = true;
			}

			public void valueChanged(ListSelectionEvent evt) 
			{
				replace_TF.setText(suggs.getSelectedValue());
			}
		}
	}
}
