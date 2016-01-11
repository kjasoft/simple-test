package com.kja.questions;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kja.questions.QuestionsData.Answer;
import com.kja.questions.QuestionsData.Question;

/**
 * 
 */
public class App {
	
	private static final int DEF_HTTP_PORT = 80;

	private static final String DATA_FILE = "data.json";
	
	private static ObjectMapper mapper = initMapper();
	private static QuestionsData data;

	public static ObjectMapper initMapper() {
		ObjectMapper om = new ObjectMapper();
		om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return om;
	}
	
    public static void main( String[] args ) throws Exception {
		data = loadData();
		int port = setupWeb(DEF_HTTP_PORT);
		Thread.sleep(1000);
		String appUrl = null;
		System.out.println("Адрес приложения: ");
		String portStr = (port == DEF_HTTP_PORT ? "/" : (":"+port+"/"));
		for(String ip: NetUtils.getCurIPs(port)) {
			String url = ip + portStr;
			System.out.println(url);
			if(appUrl == null) appUrl = url;
		}
		if(appUrl == null) appUrl = "localhost"+portStr;
		try {
			Desktop.getDesktop().browse(new URI("http://"+appUrl));
		} catch(Exception e) {
			System.out.println(e.getMessage());			
		}
    }

	private static int setupWeb(int portWant) throws Exception {
		int port = NetUtils.getAvailablePort(portWant);
		port(port);
		staticFileLocation("/web-static");
		get("/", (req, res) -> {
			res.redirect("/index.html");
			return "";
		});
		get("/init.json", (req, res) -> {
			QuestionsData qd = new QuestionsData();
			qd.title = data.title;
			for(Question q : data.questions) {
				Question q1 = new Question(); qd.questions.add(q1);
				q1.question = q.question;
				for(Answer a : q.answers) {
					Answer a1 = new Answer(); q1.answers.add(a1);
					a1.text = a.text;
				}
			}
			String adr = req.raw().getRemoteAddr();
			System.out.println(adr + " начал тест");
			return dataToJson(qd);
		});
		post("/checkResults.json", (req, res) -> {
			QuestionsData cd = mapper.readValue(req.body(), QuestionsData.class);
			int right = 0;
			for (Question q : cd.questions) {
				Question qq = data.questions.get(q.id);				
				int rightA = getRightA(qq);
				int usrA = q.a;
				if(usrA == rightA)
					right+=1;
			}
			QuestionsData qd = new QuestionsData();
			qd.total = data.questions.size();
			qd.right = right;
			qd.mark = getMark(qd.right, qd.total, data.grades);
			String adr = req.raw().getRemoteAddr();
			System.out.println(adr + " закончил тест, оценка: " + qd.mark);
			return dataToJson(qd);
		});
		exception(Exception.class, (e, request, response) -> {
			response.status(500);
			response.body("Произошла ошибка: " + e.getMessage());
			e.printStackTrace();
		});
		return port;
	}

	private static Integer getMark(Integer right, Integer total,
			Map<Integer, Double> grades) 
	{
		double d = 1.0 * right / total;
		int mark = 0;
		for(Integer m : grades.keySet()) {
			if(mark > m) continue;
			Double limit = grades.get(m);
			if(d >= limit)
				mark = m;
		}
		return mark;
	}

	private static int getRightA(Question q) {
		int i = 0, rightA = -1;
		for(Answer a : q.answers) {
			if(a.right != null && a.right) {
				rightA = i;
				break;
			}
			i++;
		}
		return rightA;
	}
	
	public static class ServData {
		public String title = "hello title";
	}
	
	public static String dataToJson(Object data) throws Exception {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, data);
		return sw.toString();
    }
	/*
				NewPostPayload creation = mapper.readValue(request.body(), NewPostPayload.class);
			if (!creation.isValid()) {
				response.status(HTTP_BAD_REQUEST);
				return "";
			}
			int id = model.createPost(creation.getTitle(), creation.getContent(), creation.getCategories());
			response.status(200);
			response.type("application/json");
			return id;
	*/
	
	public static QuestionsData loadData()
			throws UnsupportedEncodingException, IOException,
			JsonParseException, JsonMappingException 
	{
		String text = new String(Files.readAllBytes(new File(DATA_FILE).toPath()), "utf-8");
		text = text.replace("\uFEFF", ""); // UTF-8 BOM
		//text = text.replace("\"", "||");
		//text = text.replace("'", "\"");
		//text = text.replace("||", "\'");
		ObjectMapper mapper = App.initMapper();
		QuestionsData data = mapper.readValue(text, QuestionsData.class);
		System.out.println("Загружено вопросов: " + data.questions.size());
		return data;
	}
	
}
