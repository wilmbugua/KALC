/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
*/


package ke.kalc.pos.util;

import java.util.Timer;
import java.util.TimerTask;

public class Refresh {
		
	private Timer refreshTimer;
	//private TimerTask task;
        private Integer period = 10000;
        private Boolean running = false;
	
	private static Refresh INSTANCE = new Refresh();

        
	public static Refresh getInstance() {		
		if (INSTANCE == null){
			synchronized (Refresh.class){
				if (INSTANCE == null){
					INSTANCE = new Refresh();
				}
			}
		}			
		return INSTANCE;
	}
	
	public void pause(){
            if (running)
		refreshTimer.cancel();
                running = false;
	}
	
        public void stop(){                        
            if (running)
		refreshTimer.cancel();
                running = false;
        }
        
        public void start(TimerTask task){
            if (running)stop();
            refreshTimer = new Timer();
            refreshTimer.scheduleAtFixedRate(task,100, this.period);            
            running = true;
        }
               
        public void setTimer(TimerTask task, Integer period){
            if (running) stop();
            this.period = period;
            refreshTimer = new Timer();
            refreshTimer.scheduleAtFixedRate(task, 100, period);
            running = true;
	}		
      
        public Boolean isRunning(){
            return this.running;
        }
}

