package com.test.diaotz.bean;

import java.util.List;

import lombok.Data;

@Data
public class ResponseBean {

	private Boolean isValid;
	private String message;
	private List<Users> userList;
	private List<Student> studentList;
}
