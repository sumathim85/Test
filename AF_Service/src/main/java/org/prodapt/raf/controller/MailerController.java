package org.prodapt.raf.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.prodapt.raf.dto.Mail;
import org.prodapt.raf.service.MailerService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@RequestMapping("/mail")
@Configuration
@ComponentScan("org.prodapt.raf.service")
@RestController
public class MailerController {

	@Autowired
	private MailerService emailService;

	private static Logger log = LoggerFactory.getLogger(MailerController.class);

	@GetMapping("/")
	public String welcome(@RequestParam(value = "name", defaultValue = "TEST OK !!") String name){
		return String.format("Prodapt RAF UI check %s!", name);
	}

	@PostMapping(value = "/send", produces=MediaType.APPLICATION_JSON_VALUE)
	public String rafMail(@RequestBody String payload) throws Exception {
		JSONObject json = (JSONObject) new JSONParser().parse(payload);
		String result = "{\"response\":\"Failed to Send Email !\"}";

		log.info("START... Sending email");
		try {
			Mail mail = new Mail();
			mail.setFrom("testportaluser@prodapt.com");// replace with your desired email
			mail.setMailTo(((JSONObject) json.get("testingRecipients")).get("to").toString().split(","));
			mail.setMailCc(((JSONObject) json.get("testingRecipients")).get("cc").toString().split(","));
			mail.setMailBcc(((JSONObject) json.get("testingRecipients")).get("Bcc").toString().split(","));
			//			mail.setMailBcc(json.get("Bcc").toString().split(","));
			if(json.get("subject") != null) {
				mail.setSubject(json.get("subject").toString());
			} else {
				mail.setSubject("");
			}
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("mailContent", json.get("mailContent"));
			model.put("actualTo", json.get("to"));
			model.put("actualCc", json.get("cc"));
			model.put("actualBcc", json.get("Bcc"));
			model.put("entity", "IntakeMailTemplate");
			model.put("bg", true);
			mail.setProps(model);
			emailService.sendEmail(mail);
			result = "{\"response\":\"Mail Sent Successfully\"}";
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		log.info("END... Email sent success");

		return result;
	}
}