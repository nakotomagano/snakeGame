package com.snakegame.controller;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snakegame.beans.Player;


@Controller
@RequestMapping(value = {"/"})
public class BaseController {
	List<Double> positions1 = new ArrayList<Double>();
	List<Double> positions2 = new ArrayList<Double>();
	String player1Name;
	String player2Name;
	boolean player1Ready = false;
	boolean player2Ready = false;
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String view(
			 @ModelAttribute("ajdee") String str,
			ModelMap model){
		model.addAttribute("name1", player1Name);
		model.addAttribute("name2", player2Name);
		return "index";
	}

	//consumes ="application/json",produces = "application/json", 
	//,headers="Accept=application/json",produces = "application/json",consumes ="application/json"
	@RequestMapping(value="/playerjson", method=RequestMethod.POST)
	public @ResponseBody List<Player> addPlayer(ModelMap model, @RequestBody Player player){ //, HttpServletResponse rRes, HttpServletRequest rReq
		List<Player> lp = new ArrayList<Player>();
		Player p = new Player();
		String playerName = "";
		if(!player1Ready) {
			player1Name = player.getName(); //readNames(rReq);
			model.addAttribute("name1", player1Name);
			player1Ready = true;
			playerName = player1Name;
			model.addAttribute("name1", player1Name);
			Player p1 = new Player();
			p1.setId(1);
			p1.setName(playerName);
			lp.add(p1);
		}
		else if(!player2Ready) {
			player2Name = player.getName(); //readNames(rReq);
			model.addAttribute("name1", player2Name);
			player2Ready = true;
			playerName = player2Name;
			model.addAttribute("name2", player2Name);
			Player p2 = new Player();
			p2.setId(2);
			p2.setName(playerName);
			lp.add(p2);
		}
		return lp;
		//return "";
	}
	String readNames(HttpServletRequest rReq){
		String line = null;
		StringBuffer requestStr = new StringBuffer();
		try {
			BufferedReader reader = rReq.getReader();
			while ((line = reader.readLine()) != null)
				requestStr.append(line);
		} catch (Exception e) {
			// TODO : exception handling return errorPage;
		}
		return requestStr.toString();
	}
	
	
	@RequestMapping("phcheck")
	public @ResponseBody List<Player> pay(@RequestParam("empid") int empid, String fdate, String tdate) {
		Player p1 = new Player();
		p1.setId(1);
		p1.setName("maaetgw");
		List<Player> pl = new ArrayList<Player>();
		pl.add(p1);
	   return pl;
	}
}
