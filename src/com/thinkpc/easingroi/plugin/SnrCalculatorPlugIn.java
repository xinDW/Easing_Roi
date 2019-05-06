package com.thinkpc.easingroi.plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.GaussianBlur3D;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;

public class SnrCalculatorPlugIn implements PlugInFilter {

	private ImagePlus imagePlus;
	
	private double NA;
	private int lambda;
	private double pixelSize;
	
	public SnrCalculatorPlugIn() { }

	@Override
	public int setup(String arg, ImagePlus imp) {
		imagePlus = imp;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (showDialog()) {
			ImagePlus tmp = imagePlus.duplicate();	
			double abbeLimit = 1.22 * lambda / 1000. / NA / pixelSize;
			
			ImageProcessor filtered = tmp.getProcessor();
			new GaussianBlur().blurGaussian(filtered, abbeLimit);
			
			ImageProcessor noise = ipSubstrctor(ip, filtered);
			
			new ImagePlus("signal", filtered).show();
			new ImagePlus("noise", noise).show();
			
			double signalVal = calcuSignal(filtered);
			double noiseVal = calcuNoise(noise);
			IJ.log("Signal : Noise " + signalVal + " : " +  noiseVal);
			IJ.log("SNR of " + this.imagePlus.getShortTitle() + " : " + signalVal / noiseVal);
		}
	}
	
	/**
	 * Calculate the mean of the top 20% brightest pixels as the Signal;
	 * @param ip
	 */
	private double calcuSignal(ImageProcessor ip) {
		int[] histo = ip.getHistogram();
		int size = ip.getHeight() * ip.getWidth();
		
		long sum = 0;
		int accumulatedPixelNum = 0;
		for (int i = histo.length - 1; i >= 0; i--) {
			if (accumulatedPixelNum > size * 0.2) break;
			sum += i * histo[i];
			accumulatedPixelNum += histo[i];
		}
		return sum / accumulatedPixelNum;
	}
	
	/**
	 * Calculate the RMS value of the top 20% pixels of a noise image
	 * @param ip
	 * @return
	 */
	private double calcuNoise(ImageProcessor ip) {
		int[] histo = ip.getHistogram();
		int size = ip.getHeight() * ip.getWidth();
		
		long sum = 0;
		int accumulatedPixelNum = 0;
		for (int i = histo.length - 1; i >= 0; i--) {
			if (accumulatedPixelNum > size * 0.2) break;
			sum += i * i * histo[i];
			accumulatedPixelNum += histo[i];
		}
		return Math.sqrt(sum / accumulatedPixelNum);
	}
	
	private ImageProcessor ipSubstrctor(ImageProcessor ip1, ImageProcessor ip2) {
		
		ImageProcessor tmp = ip1.duplicate();
		for (int i = 0; i < ip1.getWidth(); i++) {
			for (int j = 0; j < ip1.getHeight(); j++) {
				int val = ip1.get(i, j) - ip2.get(i, j);
				
				tmp.set(i, j, (val > 0 ? val : 0));
			}
		}
		
		return tmp;
	}
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("ROI Size");
		gd.addNumericField("Lambda", 505, 0, 5, "nm");
		gd.addNumericField("N.A.", 0.8, 1, 5, "");
		gd.addNumericField("Pixel size", 1.625, 3, 5, "um");
		
		gd.showDialog();
		this.lambda =  (int) gd.getNextNumber();
		this.NA =  gd.getNextNumber();
		this.pixelSize = gd.getNextNumber();
		
		if (gd.wasCanceled())
			return false;

		return true;
	}


}
