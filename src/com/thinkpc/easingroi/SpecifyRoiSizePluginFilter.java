package com.thinkpc.easingroi;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class SpecifyRoiSizePluginFilter implements PlugInFilter {
	private ImagePlus imp;
	
	private String unit = "px";
	private double width;
	private double height;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		// TODO Auto-generated method stub
		if (showDialog()) {
			this.imp.setRoi(10, 10, (int)width, (int)height);
			this.imp.updateAndDraw();
		}
	}
	
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("ROI Size");
		gd.addNumericField("width", 0, 0, 5, unit);
		gd.addNumericField("height", 0, 0, 5, unit);
		
		gd.showDialog();
		this.width =  gd.getNextNumber();
		this.height =  gd.getNextNumber();
		
		if (gd.wasCanceled())
			return false;

		return true;
	}

}
