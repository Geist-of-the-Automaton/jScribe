package jScribe_final;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/** 
 * The Dictionary Class an extension of the JToolBar Class and is the visual component
 * of displaying word definitions. It works in conjunction with a method from the Word Class
 * to display fetched word definitions to the user.
 * Copyright 2018
 * GeistfulAutomaton@gmail.com
 * @author Auden Childress
 * @version 4.0
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Dictionary extends JToolBar implements ActionListener 
{
	private JTextField searchBar;
	private JButton searchButton;
	private static JTextArea definition;
	
	/**
	 * Constructs the visual layout and properties of the JToolBar and
	 * the added components.
	 */
	public Dictionary () 
	{
		super();
		setVisible(false);
		setFloatable(false);
		setLayout(new BorderLayout());
		removeAll();
		searchBar = new JTextField(10);
		searchButton = new JButton("Search");
		searchBar.addActionListener(this);
		searchButton.addActionListener(this);
		definition = new JTextArea();
		definition.setEditable(false);
		definition.setLineWrap(true);
		definition.setWrapStyleWord(true);
		Box box = Box.createVerticalBox();
		Box box2 = Box.createHorizontalBox();
		box2.add(Box.createHorizontalStrut(5));
		box2.add(searchBar);
		box2.add(Box.createHorizontalStrut(5));
		box2.add(searchButton);
		box2.add(Box.createHorizontalStrut(5));
		box2.setMaximumSize(new Dimension(300, 100));
		add(box2, BorderLayout.NORTH);
		box.add(box2);
		box.add(Box.createVerticalStrut(5));
		box2 = Box.createHorizontalBox();
		box2.add(Box.createHorizontalStrut(5));
		JScrollPane scroller = new JScrollPane(definition);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		box2.add(scroller);
		box2.add(Box.createHorizontalStrut(5));
		box.add(box2);
		add(box, BorderLayout.CENTER);
	}
	
	/**
	 * Takes a String (word) and sets the search TextField to the word and
	 * sets the definition JTextArea to the fetched definition of the word.
	 * This method is used when the Dictionary JToolBar is not visible and
	 * the user has selected a word and searched it with the right click
	 * menu.
	 * @param toSearch The word to fetch the definition of.
	 */
	public void queueSearch (String toSearch) 
	{
		searchBar.setText(toSearch);
		definition.setText(Words.search(toSearch));
	}
	
	/**
	 * Sets the definition JTextArea to the fetched definition of the word
	 * in the search TextField. This method is used when the Dictionary 
	 * JToolBar is visible and the user has clicked the search button.
	 */
	@Override
	public void actionPerformed (ActionEvent arg0) 
	{
		definition.setText(Words.search(searchBar.getText()));
	}
}
