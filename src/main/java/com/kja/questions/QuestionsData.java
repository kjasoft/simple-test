package com.kja.questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsData {

	public List<Question> questions = new ArrayList<QuestionsData.Question>();
	public Map<Integer, Double> grades = new HashMap<>();
	public Integer right;
	public Integer total;
	public Integer mark;
	public String title;
	
	public static class Question {
		public Integer id;
		public String question;
		public List<Answer> answers = new ArrayList<>();
		public Integer a;
	}
	
	public static class Answer {
		public Boolean right;
		public String text;
		public Integer id;
	}
	
	/*public static class CheckData {
		public List<Question> questions = new ArrayList<QuestionsData.Question>();
	}*/
	
//	public static void main(String[] args) throws Exception {
////		Path p = new ;
////		System.out.println(text);
//		QuestionsData data = loadData();
//		System.out.println(data);
////		mapper.enable(SerializationFeature.INDENT_OUTPUT);
////		StringWriter sw = new StringWriter();
////		mapper.writeValue(sw, data);
////		System.out.println(
////				sw.toString()
////				);
//	}
	
}
