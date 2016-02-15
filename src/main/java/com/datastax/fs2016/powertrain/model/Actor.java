package com.datastax.fs2016.powertrain.model;

public class Actor
{
	private String firstName;
	private String lastName;

	public Actor(String firstName, String lastName)
	{
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() { return firstName; }

	public String getLastName() { return lastName; }

	@Override
	public String toString()
	{
		return "Actor{firstName=" + firstName
				+ ", lastName=" + lastName
				+ "}";
	}
}
