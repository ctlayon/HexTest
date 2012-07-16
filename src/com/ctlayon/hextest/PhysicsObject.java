package com.ctlayon.hextest;

public class PhysicsObject {
	
	public String name;
	public int unique;
	public boolean isDead;
	public boolean gotHit;
	public int hitDamage;

	public PhysicsObject(String id, int num) {
		this.name = id;
		this.unique = num;
		this.isDead = false;
		this.gotHit = false;
		this.hitDamage = 0;
	}	
	
	public PhysicsObject(String id) {
		this.name = id;
		this.unique = 0;
		this.isDead = false;
		this.gotHit = false;
	}
	
	/**
	 * Creates a unique name
	 * @return a unique string identifying a hexagon
	 */
	
	public String uniqueName() {
		return unique + name;
	}

}
