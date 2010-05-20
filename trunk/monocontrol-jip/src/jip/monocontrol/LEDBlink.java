/*
 *  LEDBlink.java
 * 
 *  Copyright (c) 2008, Tom Dinchak , Julien Bayle
 * 
 *  This file is part of Pages.
 *
 *  Pages is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Pages is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with Pages; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package jip.monocontrol;

/**
 * A thread to blink leds cleanly.
 * 
 * @author Tom Dinchak, Julien Bayle
 *
 */

public class LEDBlink implements Runnable {
	int x, y;
	int delay, halfinterval;
		
	/**
	 * The MonomeConfiguration that the fader page this thread belongs to is on
	 */
	private Monome monome;
	
	/**
	 * @param monome
	 * @param x
	 * @param y
	 * @param delay
	 */
	public LEDBlink(Monome monome, int x, int y, int delay, int interval) {
        this.monome = monome;
        this.x = x;
        this.y = y;
        this.delay = delay;
        this.halfinterval = interval/2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis()<start+delay){
			this.monome.led(this.x, this.y, 1);
			try {
				Thread.sleep(this.halfinterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.monome.led(this.x, this.y, 0);
			try {
				Thread.sleep(this.halfinterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
	}

}