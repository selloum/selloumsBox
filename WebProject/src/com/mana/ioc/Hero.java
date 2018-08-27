package com.mana.ioc;

public class Hero {

	private String name;
	private String outfit;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOutfit() {
		return outfit;
	}
	public void setOutfit(String outfit) {
		this.outfit = outfit;
	}
	
	public void say() {
		System.out.println(name + "π∫¬Ú¡À" + outfit);
	}
}
