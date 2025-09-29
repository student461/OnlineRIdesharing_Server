package com.example.messagingstompwebsocket;

public class HelloMessage {
private String name;

public HelloMessage() { }

public HelloMessage(String name)
{
	System.out.println("inside setter");
	this.name=name;
}

public String getName()
{
	return name;
}

public void SetName(String name)
{
	this.name=name;
}
	
}
