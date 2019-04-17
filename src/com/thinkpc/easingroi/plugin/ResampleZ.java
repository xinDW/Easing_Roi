package com.thinkpc.easingroi;

import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;

public class ResampleZ implements PlugInFilter {

	private double scale = 1;
	private int range;
	
	private ImagePlus imagePlus;
	private ImageStack imageStack;
	private String newLabel;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		imagePlus = imp;
		return DOES_8G + DOES_16;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		if (showDialog()) {
			//IJ.log("" + scale);
			
			newLabel = imagePlus.getShortTitle();
						
			ImageStack scaledStack = resamplingZ(imageStack, scale, range);
			if (scaledStack != null) {
				ImagePlus scaledPlus = new ImagePlus(newLabel + "-scale" + scale, scaledStack);
				scaledPlus.show();
				scaledPlus.updateAndDraw();
			}
			
		}
		
		
		
	}
	
	public boolean showDialog() {
		imageStack = imagePlus.getImageStack();
		range = imageStack.getSize();
		
		if (imageStack == null) {
			IJ.log("Image STACK required");
			return false;
		}
			
		GenericDialog dialog = new GenericDialog("resample z-axis"); 
		dialog.addNumericField("range", range, 0);
		dialog.addNumericField("scale", scale, 3);
		dialog.showDialog();
		
		range = (int)(dialog.getNextNumber());
		scale = dialog.getNextNumber();
		
		if (dialog.wasCanceled())
			return false;
		
		return true;
	}
	
	private ImageStack resamplingZ(ImageStack stack, double scale, int range) {
		int sliceNumber = stack.getSize();
		if (range <1  || range > sliceNumber) {
			IJ.log("illegal argument : range must be in range [1, stack size]");
			return null;
		}
		int interval = (int)(1. / scale);
		//IJ.log(interval  + "");
		if (scale <= 0.0 || scale > 1) {
			IJ.log("illegal argument : scale must be in range (0, 1]");
			return null;
		}
			
		
		int height = stack.getHeight();
		int width = stack.getWidth();
		ImageStack rescaledStack = new ImageStack(width, height);
		
		for (int i = 1; i <= range; i+=interval) {
			rescaledStack.addSlice(stack.getProcessor(i));
		}
		return rescaledStack;
	}

}
