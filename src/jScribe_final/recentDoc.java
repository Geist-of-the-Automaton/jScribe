package jScribe_final;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

/** 
 * The RecentDoc Class is used to keep track of recently opened files and documents.
 * Copyright 2018
 * GeistfulAutomaton@gmail.com
 * @author Auden Childress
 * @version 4.0
 * @since 2.0
 */
@SuppressWarnings("serial")
public class RecentDoc extends JMenuItem 
{
	private File docFile;
	
	/**
	 * Creates a new RecentDoc Object (extension of JMenuItem) stores the given File into
	 * the docFile (a File Object) so that the path to the file is preserved. The text title
	 * is set to the title of the recently opened File and the icon is set to the recent 
	 * document icon from resources.
	 * @param file The File Object associated with the recent document.
	 */
	public RecentDoc (File file) 
	{
		super();
		docFile = file;
		if (file != null)
			setText(file.getName());
		try
		{
			ImageIcon ii = new ImageIcon(getClass().getResource("/fileLoad.png"));
			setIcon(ii);
		}
		catch (Exception e)
		{ }
	}
	
	/**
	 * Returns the title of the File in String format.
	 * @return File title in String format.
	 */
	public String getDocTitle () 
	{
		return docFile.getName();
	}
	
	/**
	 * Returns the File path in String format.
	 * @return File path in String format.
	 */
	public String getDocPath () 
	{
		return docFile.getPath();
	}
	
	/**
	 * Compares two RecentDocs for equality.
	 * @param doc The RecentDoc to compare to.
	 * @return The Equality of File paths and titles.
	 */
	public boolean equals (RecentDoc doc) 
	{		
		if (this == null || doc == null)
			return false;
		return doc.getDocPath().equals(getDocPath()) && doc.getDocTitle().equals(getDocTitle());
	}
	
	/**
	 * Returns the docFile.
	 * @return The associated File Object
	 */
	public File getFile () 
	{
		return docFile;
	}
}
