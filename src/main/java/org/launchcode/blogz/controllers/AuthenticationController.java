package org.launchcode.blogz.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.launchcode.blogz.models.User;
import org.launchcode.blogz.models.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationController extends AbstractController {
	
	@Autowired
	private  UserDao userDao;
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signupForm() {
		return "signup";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, Model model) {
		
		//get parameters from request
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		String verify=request.getParameter("verify");

		if (!(User.isValidUsername(username)& User.isValidPassword(password)& password.equals(verify))){
			if (!User.isValidUsername(username)){
				username="";
				model.addAttribute("username_error","Invalid Username");
			}
			else{
				model.addAttribute("username",username);
			}
			if(!User.isValidPassword(password)){
				password="";	
				model.addAttribute("password_error","Invalid Password");
			}
			if (!password.equals(verify)||verify==""){
			verify="";
				model.addAttribute("verify_error", "Invalid password verification");
			}
			return "signup";
		}		
		//if they validate, create a new user , and put them in the session use setUserInSession method
		User user= new User(username,password);
		userDao.save(user);
		HttpSession thisSession=request.getSession();
		setUserInSession(thisSession, user);
		
		return "redirect:blog/newpost";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm() {
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, Model model) {
		
		//get parameters from request
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		
		//get user by their Username
		User user= userDao.findByUsername(username);
		
		//check password is correct pass error if incorrect
		if(!user.isMatchingPassword(password)){
			model.addAttribute("error", "Invalid password");
			return "login";
		}
		
		//log them in, if so use setUserInSession(HttpSession session, User user)
		HttpSession thisSession=request.getSession();
		setUserInSession(thisSession, user);
		return "redirect:blog/newpost";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request){
        request.getSession().invalidate();
		return "redirect:/";
	}
}
