package com.thinkpc.easingroi;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ij.IJ;
import ij.WindowManager;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ControlPanel extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private static ControlPanel INSTANCE;
	private EasingROIPlugIn observer;
	
	private static JComboBox<String> cbMaster;
	private static JComboBox<String> cbSlave;
	private static JCheckBox ckbIsFollowing;
	private static JCheckBox ckbScalable;
	private static JButton btnRefreshList;
	
	private static int[] imageIdList;
	
	private ControlPanel() {
		super("Easing ROI");
	}
	
	public static ControlPanel createAndShow() {
		if (INSTANCE == null)
			INSTANCE = new ControlPanel();
		
		
		Container pane = INSTANCE.getContentPane();
		
		pane.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.insets = new Insets(10,20,10,15);
		
		// JLabel
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		pane.add(new JLabel("Master"), c);
		
		c.gridx = 0;
		c.gridy = 1;
		pane.add(new JLabel("Slave"), c);
		
		// ComboBox 1
		c.insets = new Insets(10,10,10,15);
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 7;
		cbMaster = new JComboBox<String>();
		pane.add(cbMaster, c);
		//ComboBox 2
		c.gridx = 1;
		c.gridy = 1;
		
		cbSlave = new JComboBox<String>();
		pane.add(cbSlave, c);
		
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		ckbIsFollowing = new JCheckBox("following", true);
		pane.add(ckbIsFollowing, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		ckbScalable = new JCheckBox("scalable", true);
		pane.add(ckbScalable, c);
		
		c.gridx = 3;
		c.gridy = 3;
		btnRefreshList= new JButton("Refresh List");
		pane.add(btnRefreshList, c);
		
		refreshList();
		INSTANCE.pack();
		INSTANCE.setVisible(true);
		
		cbMaster.addActionListener(INSTANCE);
		cbSlave.addActionListener(INSTANCE);
		ckbIsFollowing.addActionListener(INSTANCE);
		ckbScalable.addActionListener(INSTANCE);
		btnRefreshList.addActionListener(INSTANCE);
		
		INSTANCE.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				//IJ.log("Active");
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				//IJ.log("Closed");
				
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				//IJ.log("Closing");
				notifyObserverFollowing(false);
				ControlPanel.INSTANCE = null;
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {}

			@Override
			public void windowDeiconified(WindowEvent arg0) {}

			@Override
			public void windowIconified(WindowEvent arg0) {	}

			@Override
			public void windowOpened(WindowEvent arg0) {}
			
		});
		return INSTANCE;
	}
	
	private static void refreshList() {
		if (cbMaster == null) 
			cbMaster = new JComboBox<String>();
		if (cbSlave == null) 
			cbSlave = new JComboBox<String>();
		
		cbMaster.removeAllItems();
		cbSlave.removeAllItems();
		
		if (WindowManager.getImageCount() == 0) {
			return;
		}
		imageIdList = WindowManager.getIDList();
		
		for (int i = 0; i < imageIdList.length; i++) {
			
			String s = WindowManager.getImage(imageIdList[i]).getTitle();
			cbMaster.addItem(s);
			cbSlave.addItem(s);
		}
		if (cbSlave.getItemCount() >1) {
			cbSlave.setSelectedIndex(1);
		}
	}
	
	public static void registerObserver(EasingROIPlugIn e) {
		INSTANCE.observer = e;
	}
	public static void notifyObserverMaster() {
		String master = (String) cbMaster.getSelectedItem();
		INSTANCE.observer.getNotificationMaster(master);
	}
	public static void notifyObserverSlave() {
		String slave = (String) cbSlave.getSelectedItem();
		INSTANCE.observer.getNotificationSlave(slave);
	}
	
	public static void notifyObserverFollowing(boolean f) {
		INSTANCE.observer.setFollowing(f);
	}
	public static void notifyObserverScalable(boolean s) {
		INSTANCE.observer.setScalable(s);
	}
	
	public static void notifyObserver() {
		notifyObserverMaster();
		notifyObserverSlave();
		notifyObserverFollowing(ckbIsFollowing.isSelected());
		notifyObserverScalable(ckbScalable.isSelected());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == cbMaster)
			notifyObserverMaster();
		else if (o == cbSlave)
			notifyObserverSlave();
		else if (o == ckbIsFollowing)
			notifyObserverFollowing(ckbIsFollowing.isSelected());
		else if (o == ckbScalable)
			notifyObserverScalable(ckbScalable.isSelected());
		else if (o == btnRefreshList) {
			refreshList();
		}
					
	}
}
