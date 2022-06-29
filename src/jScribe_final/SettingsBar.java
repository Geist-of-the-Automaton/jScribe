/** 
 * @author Auden Childress
 * @version final
 * @since March, 2018
 * Copyright 2019
 * GeistfulAutomaton@gmail.com
 */

package jScribe_final;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class SettingsBar extends JToolBar implements ActionListener, ChangeListener 
{
	private static byte MRD;
	private static byte MU;
	private static JSlider MRD_s;
	private static JSlider MU_s;
	private static JTextField MRD_tf;
	private static JTextField MU_tf;
	private static JCheckBox af;

	public SettingsBar ()
	{
		// Use the system's look and feel.
		
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e)
		{ }
		
		// Pull user defaults from jScribe.
		
		MRD = jScribe.getMRD();
		MU = jScribe.getMU();
		
		// Set up the components and the visual of the settings bar.
		
		MRD_s = new JSlider(5,15, MRD);
		MU_s = new JSlider(5,25, MU);
		MRD_tf = new JTextField(2);
		MU_tf = new JTextField(2);
		af = new JCheckBox("Auto Format When Typing");
		af.setSelected(jScribe.getAF());
		setVisible(false);
		setLayout(new BorderLayout());
		setFloatable(true);
		Panel toAdd = new Panel();
		Box archGroup = Box.createVerticalBox();
		Box group = Box.createHorizontalBox();
		toAdd.add(archGroup);
		archGroup.add(group);
		group.add(Box.createHorizontalStrut(10));
		Box box = Box.createVerticalBox();
		box.add(Box.createVerticalGlue());
		box.add(Box.createVerticalStrut(5));
		box.add(new JLabel("Maximum Recent Documents"));
		box.add(Box.createVerticalStrut(10));
		box.add(Box.createVerticalGlue());
		box.add(Box.createVerticalStrut(10));
		box.add(new JLabel("Maximum Undos"));
		box.add(Box.createVerticalStrut(5));
		box.add(Box.createVerticalGlue());
		group.add(box);
		group.add(Box.createHorizontalStrut(10));
		box = Box.createVerticalBox();
		box.add(MRD_tf);
		box.add(Box.createVerticalStrut(10));
		box.add(MU_tf);
		group.add(box);
		group.add(Box.createHorizontalStrut(10));
		box = Box.createVerticalBox();
		box.add(MRD_s);
		box.add(Box.createVerticalStrut(10));
		box.add(MU_s);
		group.add(box);
		group.add(Box.createHorizontalStrut(10));
		archGroup.add(Box.createVerticalStrut(25));
		group = Box.createHorizontalBox();
		group.add(af);
		group.add(Box.createHorizontalGlue());
		archGroup.add(group);
		MRD_s.addChangeListener(this);
		MRD_tf.setEditable(false);
		MU_s.addChangeListener(this);
		MU_tf.setEditable(false);
		group = Box.createVerticalBox();
		group.add(Box.createVerticalStrut(10));
		group.add(toAdd);
		group.add(Box.createVerticalStrut(50));
		add(group, BorderLayout.CENTER);
		MRD_tf.setText(MRD + "");
		MU_tf.setText(MU + "");
		archGroup = Box.createVerticalBox();
		group = Box.createHorizontalBox();
		JButton apply = new JButton("Apply and Close");
		apply.addActionListener(this);
		JButton def = new JButton("Restore Defaults");
		def.addActionListener(this);
		group.add(Box.createHorizontalGlue());
		group.add(apply);
		group.add(Box.createHorizontalGlue());
		group.add(def);
		group.add(Box.createHorizontalGlue());
		archGroup.add(group);
		archGroup.add(Box.createVerticalStrut(10));
		add(archGroup, BorderLayout.SOUTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) 
	{
		String btnPress = evt.getActionCommand();
		switch (btnPress) 
		{
		case "Apply and Close":			
			this.setVisible(false);
			jScribe.setMRD((byte) MRD_s.getValue());
			jScribe.setMU((byte) MU_s.getValue());
			jScribe.setAF(af.isSelected());
			
			// Save the setting from the user end.
			
			jScribe.pushSettings();
			break;
		case "Restore Defaults":
			MRD_s.setValue(5);
			MU_s.setValue(15);
			MRD_tf.setText(5 + "");
			MU_tf.setText(15 + "");
			af.setSelected(false);
			break;
		}
	}

	@Override
	public void stateChanged(ChangeEvent evt) 
	{
		// Adjust the text to reflect the slider position value.
		
		if (evt.getSource() == MRD_s)
			MRD_tf.setText(MRD_s.getValue() + "");
		else if (evt.getSource() == MU_s)
			MU_tf.setText(MU_s.getValue() + "");
	}
}
