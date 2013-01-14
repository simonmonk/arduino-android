/**
 * code from the book Arduino + Android Projects for the Evil Genius
 * <br>Copyright 2011 Simon Monk
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.simonmonk.duinodroid;


public class ValueMsg {
	private char flag;
	private int reading;

	public ValueMsg(char flag, int reading) {
		this.flag = flag;
		this.reading = reading;
	}

	public int getReading() {
		return reading;
	}
	
	public char getFlag() {
		return flag;
	}
}
