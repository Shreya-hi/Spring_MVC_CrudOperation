package com.test.diaotz.dao;

import com.test.diaotz.bean.ResponseBean;
import com.test.diaotz.bean.Student;
import com.test.diaotz.bean.StudentDetails;
import com.test.diaotz.bean.UserDetails;
import com.test.diaotz.bean.Users;

public interface UserDao {

	public ResponseBean createUser(UserDetails userDetails);
	
	public ResponseBean getRecordsTeacher(String userRole);
	
	public ResponseBean updateTeacherRecord(Users users);
	
	public ResponseBean deleteTeacher(Users users);
	
	public ResponseBean createStudent(StudentDetails studentDetails);
	
	public ResponseBean getRecordsStudent();
	
	public ResponseBean updateStudentRecord(Student student);
	
	public ResponseBean deleteStudent(Student student);
	
	public ResponseBean getRecordsStudent(String name);
	
	public ResponseBean login(Users users);
	
}
