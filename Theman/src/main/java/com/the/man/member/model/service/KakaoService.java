package com.the.man.member.model.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class KakaoService {
	
	public String getToken(String code) throws IOException, ParseException{
		
		String tokenUrl = "https://kauth.kakao.com/oauth/token";
		URL url = new URL(tokenUrl);
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("client_id=ed41cca16bdbb59e0d8ba7532bf4b74c");
		sb.append("&grant_type=authorization_code");
		sb.append("&redirect_uri=http://localhost:8717/man/code");
		sb.append("&code="+code);
		
		bw.write(sb.toString());
		bw.flush();
		
		// System.out.println(urlConnection.getResponseCode());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		
		String line = "";
		String responseData = "";
		while((line = br.readLine()) != null){
			responseData += line;
		}
		
		JSONParser parser = new JSONParser();
		JSONObject element = (JSONObject)parser.parse(responseData);
		
		String accessToken = element.get("access_token").toString();
		// System.out.println(accessToken);
		
		br.close();
		bw.close();
		
		return accessToken;
	}
	
	
	
}