package com.thinkpc.easingroi;

import java.awt.Rectangle;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.plugin.PlugIn;

public class EasingROIPlugIn implements PlugIn {
	private ImagePlus master = null;
	private ImagePlus slave = null;
	
	private boolean isFollowing = true;
	private boolean scalable = false;
	private double scale = 1.0;
	
	@Override
	public void run(String arg) {
		// TODO Auto-generated method stub
		ControlPanel.createAndShow();
		ControlPanel.registerObserver(this);
		ControlPanel.notifyObserver();
		
		if (this.master != null && this.slave != null) {
			
			//followRoiInNewThread();
		} 
		else {
			IJ.error("2 opened images required");
		}
		
	
	}
	
	public void getNotificationMaster(String masterTitle) {
		
		this.master = WindowManager.getImage(masterTitle);
		//IJ.log("master : " + masterTitle);
		
	}
	public void getNotificationSlave(String slaveTitle) {
		
		this.slave = WindowManager.getImage(slaveTitle);		
		//IJ.log("slave : " + slaveTitle);
		
	}
	
	public void setScalable(boolean s) {
		this.scalable = s;
	}
	
	public void setFollowing(boolean f) {
		this.isFollowing = f;
		if (this.isFollowing) {
			followRoiInNewThread();
		}		
	}
	private void followRoiInNewThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isFollowing) {
					if (checkScale()) {
						
						//IJ.log("followROI in Thread " + Thread.currentThread().getId());
						
						Roi roi = EasingROIPlugIn.this.master.getRoi();
						
						if (roi != null ) {
							//IJ.log("[master] " + roi.toString());
							Rectangle rect = roi.getBounds();
							Roi newRoi = new Roi(rect.getX() / scale, rect.getY() / scale, rect.getWidth() / scale, rect.getHeight() / scale);
							EasingROIPlugIn.this.slave.setRoi(newRoi);
							slave.updateAndDraw();
							//IJ.log("[slave] " + EasingROIPlugIn.this.slave.getRoi().toString());
						} else {
							//IJ.log("roi null");
						}						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							IJ.log("interrupted");
						}
					} else {
						//IJ.log("hehe");
					}
					
				}
				
			}
		}).start();;
		
	}
	
	/**
	 * check if the two selected ImagePlus having the same ratio in both width and height 
	 * @return 
	 * true if master.size / slave.size == this.scale or the "scalable" option is set to false, which means scale check is unnecessary
	 * 
	 */
	private boolean checkScale() {
		if (this.master == null || this.slave == null) 
			return false;
		else if (!scalable) {
			this.scale = 1.;
			return true;
		}
			
		else  {
			double scale_width = this.master.getWidth() / this.slave.getWidth();
			double scale_height = this.master.getHeight() / this.slave.getHeight();
			//return (scale_width == scale_height)? true : false;
			if (scale_width == scale_height) {
				this.scale = scale_width;
				return true;
			}
			return false;
			
		}
		
		
		
	}
}
