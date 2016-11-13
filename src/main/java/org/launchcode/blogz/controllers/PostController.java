package org.launchcode.blogz.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.launchcode.blogz.models.Post;
import org.launchcode.blogz.models.User;
import org.launchcode.blogz.models.dao.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PostController extends AbstractController {
	
	@Autowired
	private  PostDao postDao;

	@RequestMapping(value = "/blog/newpost", method = RequestMethod.GET)
	public String newPostForm() {
		return "newpost";
	}
	
	@RequestMapping(value = "/blog/newpost", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, Model model) {
		
		//get parameters from request
		String title=request.getParameter("title");
		String body=request.getParameter("body");
		
		//Validate parameters if not valid send back to form w/ error message
		if (title=="" || body==""){
			if (title!=""){
				model.addAttribute("title",title);
			}
			if(body!=""){
				model.addAttribute("body",body);
			}
			model.addAttribute("error","Post must have a Title and Body!");
			return "newpost";
		}
		
		//if they validate, create a new post
		Integer userId = (Integer) request.getSession().getAttribute(AbstractController.userSessionKey);
		User author= userDao.findByUid(userId);
		Post post = new Post(title,body,author);
		postDao.save(post);
		model.addAttribute("post", post);
		return "redirect:"+ post.getAuthor().getUsername()+"/"+ post.getUid();  		
	}
	
	@RequestMapping(value = "/blog/{username}/{uid}", method = RequestMethod.GET)
	public String singlePost(@PathVariable String username, @PathVariable int uid, Model model) {
		
		//get the given post
		Post post = postDao.findOne(uid);
		
		//pass the post into the template
		model.addAttribute("post", post);
		return "post";
	}
	
	@RequestMapping(value = "/blog/{username}", method = RequestMethod.GET)
	public String userPosts(@PathVariable String username, Model model) {
		
		//get all of the user posts
		User author = userDao.findByUsername(username);
		List <Post> posts = postDao.findByAuthor_uid(author.getUid());
		
		//pass all of the posts into the template model.addattribute("name", obj)
		model.addAttribute("posts", posts);
		return "blog";
	}
	
}
