package com.thinkit.model;

/**
 * Guest info: name, available time slots, timezone/offset
 * @author Doaa.farouk
 *
 */
public class Guest {
	
	public Guest() {}
	
	public Guest(String name,int[][] availability,String offset) {
		this.name=name;
		this.availability=availability;
		this.offset=offset;
	}
	

	String name;
	int[][] availability;
	String offset;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int[][] getAvailability() {
		return availability;
	}
	public void setAvailability(int[][] availability) {
		this.availability = availability;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}

}
