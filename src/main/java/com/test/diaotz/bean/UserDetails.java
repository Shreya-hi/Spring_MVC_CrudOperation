package com.test.diaotz.bean;

import java.util.List;

import lombok.Data;

@Data
public class UserDetails {

	private String createdBy;
	private String userRole;
	private List<Users> users;
}
