package com.thinkpc.easingroi.plugin;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.thinkpc.easingroi.utils.Observer;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class ProfileMeasurerPluginFilter implements PlugInFilter {

	ImagePlus imagePlus;
	JFrame panel;
	
	private static final double MIN_INTENSITY_RATIO = 1. / (Math.E * Math.E); 
	
	public ProfileMeasurerPluginFilter() {
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		imagePlus = imp;
		return DOES_8G + DOES_16;
	}

	@Override
	public void run(ImageProcessor ip) {
		panel = new CPanel(this, imagePlus.getTitle());
	}
	
	private void measureProfile() {
		Calibration calibration = imagePlus.getCalibration();
		String unit = calibration.getUnit();
		double h = calibration.pixelHeight;
		double w = calibration.pixelWidth;
		
		//IJ.log(unit + " " + h + " " + w);
		
		Line line = (Line) imagePlus.getRoi();
		double angle = line.getAngle();
		double[] pixs = line.getPixels();
		double max_p = Double.MIN_VALUE;
		int center = 0;
		
		for (int i = 0; i < pixs.length; i++) {
			//max_p = max_p > pixs[i] ? max_p : pixs[i];
			if (pixs[i] > max_p) {
				max_p = pixs[i];
				center = i;
			}
		}
		
		double min_p = max_p * MIN_INTENSITY_RATIO;
		double half_max_p = max_p * 0.5;
		
		int width_es_px = 0, width_fwhm_px = 0;
		
		for (int i = center; i < pixs.length; i++) {
			if (pixs[i] >=  min_p){
				width_es_px ++;	
				if (pixs[i] >= half_max_p) {
					width_fwhm_px ++;
				}
			}
			else 
				break;
		}
		for (int i = center - 1; i >= 0; i--) {
			if (pixs[i] >=  min_p){
				width_es_px ++;	
				if (pixs[i] >= half_max_p) {
					width_fwhm_px ++;
				}
			}
			else 
				break;
		}
		
		double width_es = caluRealLength(width_es_px, angle, h, w);
		double width_fwhm = caluRealLength(width_fwhm_px, angle, h, w);
		
		IJ.log("Width: " + width_es + "  FWHM: " + width_fwhm);
	}
	

	private double caluRealLength(int lengthInPix, double angle, double pixelHeight, double pixelWidth) {
		double a = Math.abs(Math.sin(angle)) * lengthInPix * pixelHeight;
		double b = Math.abs(Math.cos(angle)) * lengthInPix * pixelWidth;
	
		return Math.sqrt(a * a + b * b);
	}
	private class CPanel extends JFrame {
		private ProfileMeasurerPluginFilter observer;
		
		public CPanel(ProfileMeasurerPluginFilter observer, String title) {
			super("Profile Measurer");
			
			this.observer = observer;
			
			Container pane = getContentPane();
			
			pane.setLayout(new GridBagLayout());
			
			GridBagConstraints c = new GridBagConstraints();
			
			c.fill = GridBagConstraints.HORIZONTAL;
			
			c.insets = new Insets(10,20,10,15);
						
			c.gridx = 0;
			c.gridy = 0;
			pane.add(new JLabel(title), c);
			
			c.gridx = 0;
			c.gridy = 1;
			JButton btnMeasure = new JButton("measure");
			pane.add(btnMeasure, c);
			
			
			pack();
			setVisible(true);
		
			btnMeasure.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					CPanel.this.observer.measureProfile();
					
				}
			});
		}
		
	
	}
	

}
