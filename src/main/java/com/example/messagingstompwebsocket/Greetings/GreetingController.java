package com.example.messagingstompwebsocket.Greetings;

import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {


	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String handleTrip(Map<String, Object> tripData) {
	    return "Trip created with " + tripData.toString();
	}

}