package com.springboot.expensetracker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.expensetracker.constant.Role;
import com.springboot.expensetracker.dao.UserRepository;
import com.springboot.expensetracker.entity.User;
import com.springboot.expensetracker.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private UserRepository uRes;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// for home page
	@GetMapping("/")
	public String home(Principal principal) {
		if(principal!=null) {
			return "redirect:user/dashboard";
		}
		return "home";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	// for register page
	@GetMapping("/register")
	public String register(@ModelAttribute(value = "user") User user, Model model, Principal principal) {
		if(principal!=null) {
			return "redirect:user/dashboard";
		}
		try {
			System.out.println("User received in register: " + user);

			if (user == null) {
				model.addAttribute("user", new User());
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}

		return "register";
	}

	// for registration process
	@PostMapping("/do-register")
	public String doRegister(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(name = "cpassword") String cpassword,
			@RequestParam(name = "terms", defaultValue = "false") boolean terms, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (bindingResult.hasErrors()) {
				System.out.println(bindingResult);
				return "register";
			}
			if (uRes.checkIfEmailExists(user.getEmail()) == 1) {
				redirectAttributes.addFlashAttribute("errorMessage",
						new Message("Email has been used already", "emailError"));
				redirectAttributes.addFlashAttribute("user", user);
				return "redirect:register";
			}
			if (uRes.checkIfPhoneExists(user.getPhone()) == 1) {
				redirectAttributes.addFlashAttribute("errorMessage",
						new Message("Phone no. has been used already", "phoneError"));
				redirectAttributes.addFlashAttribute("user", user);
				return "redirect:register";
			}
			if (!user.getPassword().equals(cpassword)) {
				redirectAttributes.addFlashAttribute("errorMessage",
						new Message("Passwords don't match", "passwordError"));
				redirectAttributes.addFlashAttribute("user", user);
				return "redirect:register";
			}
			if (!terms) {
				redirectAttributes.addFlashAttribute("errorMessage",
						new Message("You need to agree to the terms and conditions to proceed", "termsError"));
				redirectAttributes.addFlashAttribute("user", user);
				return "redirect:register";
			}

			// setting unset values
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setRole(Role.USER);
			user.setEnabled(true);
			user.setImageUrl("default.png");
			// saving user
			System.out.println(uRes.save(user) + "saved");

			// model.addAttribute("user", new User());
			redirectAttributes.addFlashAttribute("message", new Message("Registration Successful", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", new Message("Something Went Wrong", "alert-danger"));
		}
		return "redirect:register";
	}

	// to load login page
	@GetMapping("/login")
	public String login(Principal principal) {
		if(principal!=null) {
			return "redirect:user/dashboard";
		}
		return "login";
	}
}
