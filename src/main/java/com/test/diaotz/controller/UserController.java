package com.test.diaotz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.test.diaotz.bean.ResponseBean;
import com.test.diaotz.bean.Student;
import com.test.diaotz.bean.StudentDetails;
import com.test.diaotz.bean.UserDetails;
import com.test.diaotz.bean.Users;
import com.test.diaotz.dao.UserDao;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserDao userDao;
	
	@RequestMapping(value = "/createUser", method = RequestMethod.POST, produces = "application/json")
	public ResponseBean createUser(@RequestBody UserDetails userDetails) {
		return userDao.createUser(userDetails);
	}
	
	@RequestMapping(value = "/records/{userRole}", method = RequestMethod.GET, produces = "application/json")
	public ResponseBean getRecordsTeacher(@PathVariable String userRole) {
		return userDao.getRecordsTeacher(userRole);
	}
	
	@RequestMapping(value = "/updateTeacherRecord", method = RequestMethod.PUT, produces = "application/json")
	public ResponseBean updateTeacherRecord(@RequestBody Users users) {
		return userDao.updateTeacherRecord(users);
	}
	
	@RequestMapping(value = "/deleteTeacher", method = RequestMethod.PUT, produces = "application/json")
	public ResponseBean deleteTeacher(@RequestBody Users users) {
		return userDao.deleteTeacher(users);
	}
	
	@RequestMapping(value = "/createStudent", method = RequestMethod.POST, produces = "application/json")
	public ResponseBean createStudent(@RequestBody StudentDetails studentDetails) {
		return userDao.createStudent(studentDetails);
	}
	
	@RequestMapping(value = "/studentRecords", method = RequestMethod.GET, produces = "application/json")
	public ResponseBean getRecordsStudent() {
		return userDao.getRecordsStudent();
	}
	
	@RequestMapping(value = "/updateStudentRecord", method = RequestMethod.PUT, produces = "application/json")
	public ResponseBean updateStudentRecord(@RequestBody Student student) {
		return userDao.updateStudentRecord(student);
	}
	
	@RequestMapping(value = "/deleteStudent", method = RequestMethod.PUT, produces = "application/json")
	public ResponseBean deleteStudent(@RequestBody Student student) {
		return userDao.deleteStudent(student);
	}
	
	@RequestMapping(value = "/studentRecord/{name}", method = RequestMethod.GET, produces = "application/json")
	public ResponseBean getRecordsStudent(@PathVariable String name) {
		return userDao.getRecordsStudent(name);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	public ResponseBean login(@RequestBody Users users) {
		return userDao.login(users);
	}
}
