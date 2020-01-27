package com.client.therevgo.services.database;


public class ContactBean {

    private int id ;
	private String name;
	private String number;

	public ContactBean(int id , String name, String number) {
        this.id = id ;
		this.name = name;
		this.number = number;
	}

    public int getId() {
        return id;
    }

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}
}
