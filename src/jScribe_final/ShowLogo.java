package jScribe_final;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/** 
 * The ShowLogo Class is used to display the logo on the first Startup of jScribe.
 * Copyright 2018
 * GeistfulAutomaton@gmail.com
 * @author Auden Childress
 * @version 4.0
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ShowLogo extends JFrame
{
	/**
	 * Creates an undecorated window (350 x 150).
	*/
	public ShowLogo ()
	{
		setUndecorated(true);
		setSize(350,150);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Overrides the default component paint method to draw the jScribe Logo onto it.
	*/
	public void paint(Graphics g)
	{
		ImageIcon logo = new ImageIcon(getClass().getResource("/Logo.png"));
		g.drawImage(logo.getImage(), 0, 0, this);
    }
}
