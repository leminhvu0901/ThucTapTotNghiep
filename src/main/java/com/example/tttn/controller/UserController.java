package com.example.tttn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

	@GetMapping({"/", "/homeUser"})
	public String homeUser(Model model) {
		return "homeUser";
	}
	


}
