package com.thinkpc.easingroi;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.plugin.PlugIn;

public class EasingROI implements PlugIn {
	private ImagePlus master = null;
	private ImagePlus slave = null;
	
	private boolean isFollowing = true;
	
	private Roi masterRoi;
	
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
					if (EasingROI.this.master != null && EasingROI.this.slave != null ) {
						
						//IJ.log("followROI in Thread " + Thread.currentThread().getId());
						
						Roi roi = EasingROI.this.master.getRoi();
						if (roi != null ) {
							IJ.log("[master] " + roi.toString());
							EasingROI.this.slave.setRoi(roi);
							slave.updateAndDraw();
							IJ.log("[slave] " + EasingROI.this.slave.getRoi().toString());
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

}
