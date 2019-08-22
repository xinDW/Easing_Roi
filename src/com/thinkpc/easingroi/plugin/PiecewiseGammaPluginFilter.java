package com.thinkpc.easingroi.plugin;

import java.awt.AWTEvent;
import java.awt.Checkbox;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

public class PiecewiseGammaPluginFilter implements ExtendedPlugInFilter, DialogListener {

	private final int FLAGS = DOES_16 + DOES_8G + SUPPORTS_MASKING;
	
	private int max_pixel_val;
	
	private float gamma, thres;
	
	public PiecewiseGammaPluginFilter() {
		
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		this.max_pixel_val = traverseForMax(imp.getProcessor());
		//IJ.log("" + max_pixel_val);
		return this.FLAGS;
	}

	
	@Override
	public void run(ImageProcessor ip) {
		//IJ.log("run");
		ip = piecewiseGammaAdjust(ip, this.gamma, this.thres);
	}
	
	/**
	 * Apply a piecewise gamma adjustment to the image.
	 * @param ip
	 * @param gamma
	 * @param thres: float within [0, 1], a threshold under which the pixels keep unchanged. (The image pixel values are scaled to [0, 1])
	 */
	private ImageProcessor piecewiseGammaAdjust(ImageProcessor ip, float gamma, float thres) {
		if (thres < 1. && thres > 0.) {
			float maxf = this.max_pixel_val;
			int thresPixelVal = (int) (thres * maxf);
			float cliff = (float) (Math.pow(thres, gamma) - thres);
			
			for (int i = 0; i < ip.getHeight(); i++) {
				for (int j = 0; j < ip.getWidth(); j++) {
					int pv = ip.get(j, i);
					if (pv < thresPixelVal) continue;
					
					int v = (int) ((Math.pow(pv/maxf, gamma) - cliff) * maxf);
					ip.set(j, i, v);
				}
			}
			
		}
		return ip;
	}
	
	private int traverseForMax(ImageProcessor ip) {
		int max_val = Integer.MIN_VALUE;
		for (int i = 0; i < ip.getHeight(); i++) {
			for (int j = 0; j < ip.getWidth(); j++) {
				int val = ip.get(j, i);
				max_val = val > max_val ? val : max_val;
			}
		}
		return max_val;
	}
	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		Checkbox checkbox = gd.getPreviewCheckbox();
		if (checkbox == null) return false;
		
		// the right way to get the value of the slider
		this.gamma = (float) gd.getNextNumber();
		this.thres = (float) gd.getNextNumber();
		
		return true;
	}

	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		GenericDialog dialog = new GenericDialog("Piecewise Gamma");
		dialog.addSlider("gamma", 0., 1., 0.6);
		dialog.addSlider("threshold", 0., 1., 0.6);
		dialog.addPreviewCheckbox(pfr);
		dialog.addDialogListener(this);
		
		dialog.showDialog();
		if (dialog.wasCanceled()) {
			return DONE;
		}

		return IJ.setupDialog(imp, this.FLAGS);
	}

	@Override
	public void setNPasses(int nPasses) {
		// TODO Auto-generated method stub
		
	}

}
