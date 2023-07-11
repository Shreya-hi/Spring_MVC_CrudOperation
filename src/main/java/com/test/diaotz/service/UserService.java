package com.test.diaotz.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.test.diaotz.bean.ResponseBean;
import com.test.diaotz.bean.Student;
import com.test.diaotz.bean.StudentDetails;
import com.test.diaotz.bean.UserDetails;
import com.test.diaotz.bean.Users;
import com.test.diaotz.dao.UserDao;

@Service
public class UserService implements UserDao{

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public ResponseBean createUser(UserDetails userDetails) {
		ResponseBean response = new ResponseBean();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		int result = 0 ;
		String userAccess = null;

		try {
			if(userDetails.getUsers() != null) {
				if(userDetails.getCreatedBy() != null){
					String query = "Select user_role from users where user_name = '"+userDetails.getCreatedBy()+"' limit 1";
					try{
						userAccess = jdbcTemplate.queryForObject(query, String.class);
					}catch (Exception e) {
						response.setIsValid(false);
						response.setMessage("Something is wrong..");
					}

					if(userAccess != null && userAccess.equalsIgnoreCase("admin")) {
						if(userDetails.getUserRole() != null && (userDetails.getUserRole().equalsIgnoreCase("teacher") || userDetails.getUserRole().equalsIgnoreCase("student"))){

							String insertQuery = "insert into users (user_name, user_password, user_role,user_created_by,user_created_date,user_mobile, user_status) values(?,?,?,?,?,?,?)";
							String updateQuery = "update users set user_password = ?, user_role = ?, user_created_by = ?, user_updated_date = ?, user_status = ? where user_name = ?";

							for(Users user : userDetails.getUsers()) {

								String queryForValidation = "select case when exists(select user_name from users where user_name = '"+user.getUserName()+"') then 'true' else 'false' end";
								Boolean ifExists = jdbcTemplate.queryForObject(queryForValidation, Boolean.class);

								if(ifExists) {
									result = jdbcTemplate.update(updateQuery, user.getPassword(), userDetails.getUserRole(), userDetails.getCreatedBy(), timestamp, "ACTIVE", user.getUserName());
								}else {
									result = jdbcTemplate.update(insertQuery, user.getUserName(), user.getPassword(), userDetails.getUserRole(), 
											userDetails.getCreatedBy(), timestamp, user.getMobile(), "ACTIVE");
								}
							}
							if(result > 0) {
								response.setIsValid(true);
								response.setMessage("User successfully created..");
							}else {
								response.setIsValid(false);
								response.setMessage("Fail to create user..");
							}
						}else {
							response.setIsValid(false);
							response.setMessage("You have no access to create record other then teacher");
						}
					}else {
						response.setIsValid(false);
						response.setMessage("You have no access to create records..");
					}
				}else {
					response.setIsValid(false);
					response.setMessage("Created By is mandatory..");
				}
			}
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while creating records" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseBean getRecordsTeacher(String userRole) {
		ResponseBean response = new ResponseBean();
		List<Users> list = new ArrayList<Users>();

		try {
			String query = "select id, user_name, user_password, user_mobile from users where user_status = 'active' and user_role = '"+userRole+"'";
			SqlRowSet rs = jdbcTemplate.queryForRowSet(query);

			while(rs.next()) {
				Users user = new Users();
				user.setId(rs.getInt("id"));
				user.setUserName(rs.getString("user_name"));
				user.setPassword(rs.getString("user_password"));
				user.setMobile(rs.getLong("user_mobile"));

				list.add(user);
			}

			if(list.size() > 0) {
				response.setIsValid(true);
				response.setMessage("Details available..");
				response.setUserList(list);
			}else {
				response.setIsValid(false);
				response.setMessage("No records found..");
			}
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while getting records" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseBean updateTeacherRecord(Users users) {
		ResponseBean response = new ResponseBean();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		StringBuffer str = new StringBuffer();

		try {
			String queryForValidation = "select case when exists(select id from users where id = "+users.getId()+" and user_status = 'active') then 'true' else 'false' end";
			Boolean ifExists = jdbcTemplate.queryForObject(queryForValidation, Boolean.class);

			if(ifExists) {
				if(!TextUtils.isEmpty(users.getUserName()) && users.getUserName() != null) {
					str.append(" , user_name = '"+users.getUserName()+"'");
				}if(!TextUtils.isEmpty(users.getPassword()) && users.getPassword() != null) {
					str.append(" , user_password = '"+users.getPassword()+"'");
				}if(users.getMobile() != null) {
					str.append(" , user_mobile = '"+users.getMobile()+"'");
				}

				String updateQuery = "update users set user_updated_date = '"+timestamp+"'"+str+" where id = "+users.getId()+" limit 1";
				int result = jdbcTemplate.update(updateQuery);

				if(result > 0) {
					response.setIsValid(true);
					response.setMessage("Updated SuccessFully..");
				}else {
					response.setIsValid(false);
					response.setMessage("Fail to update...");
				}
			}else {
				response.setIsValid(false);
				response.setMessage("No records found...");
			}
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while getting records" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseBean deleteTeacher(Users users) {
		ResponseBean response = new ResponseBean();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		try {
			String queryForValidation = "select case when exists(select id from users where id = "+users.getId()+" and user_status = 'active') then 'true' else 'false' end";
			Boolean ifExists = jdbcTemplate.queryForObject(queryForValidation, Boolean.class);

			if(ifExists) {
				String updateQuery = "update users set user_updated_date = '"+timestamp+"', user_status = 'inactive' where id = "+users.getId()+" limit 1";
				int result = jdbcTemplate.update(updateQuery);

				if(result > 0) {
					response.setIsValid(true);
					response.setMessage("Updated SuccessFully..");
				}else {
					response.setIsValid(false);
					response.setMessage("Fail to update...");
				}
			}else {
				response.setIsValid(false);
				response.setMessage("No records found...");
			}
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while getting records" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseBean createStudent(StudentDetails studentDetails) {
		ResponseBean response = new ResponseBean();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		int result = 0 ;

		try {
			if(studentDetails.getStudent() != null) {

				String insertQuery = "insert into student (name, subject, mobile, student_status,marks, grade, created_date) values(?,?,?,?,?,?,?)";
				String updateQuery = "update student set marks = ?, grade = ?, updated_date where mobile = ?";

				for(Student student : studentDetails.getStudent()) {

					String queryForValidation = "select case when exists(select name from student where name = '"+student.getName()+"') then 'true' else 'false' end";

					Boolean ifExists = jdbcTemplate.queryForObject(queryForValidation, Boolean.class);

					if(ifExists) { 
						result = jdbcTemplate.update(updateQuery,student.getMarks(), student.getGrade(), timestamp, student.getMobile()); 
					}else { result = jdbcTemplate.update(insertQuery,student.getName(), student.getSubject(), student.getMobile(),
							"ACTIVE", student.getMarks(), student.getGrade(), timestamp); 
					} 
				}
				if(result > 0) {
					response.setIsValid(true);
					response.setMessage("Student record successfully created..");
				}else {
					response.setIsValid(false);
					response.setMessage("Fail to create student record..");
				}
			}else {
				response.setIsValid(false);
				response.setMessage("You have no access to create record other then teacher");
			}
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while creating student records" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseBean getRecordsStudent() {
		ResponseBean response = new ResponseBean();
		List<Student> list = new ArrayList<Student>();

		try {
			String query = "select id, name,subject, mobile, marks,grade from student where student_status = 'active'";
			SqlRowSet rs = jdbcTemplate.queryForRowSet(query);

			while(rs.next()) {
				Student student = new Student();
				student.setId(rs.getInt("id"));
				student.setName(rs.getString("name"));
				student.setSubject(rs.getString("subject"));
				student.setMobile(rs.getLong("mobile"));
				student.setMarks(rs.getInt("marks"));
				student.setGrade(rs.getString("grade"));

				list.add(student);
			}

			if(list.size() > 0) {
				response.setIsValid(true);
				response.setMessage("Details available..");
				response.setStudentList(list);
			}else {
				response.setIsValid(false);
				response.setMessage("No records found..");
			}
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while getting records" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseBean updateStudentRecord(Student student) {
		ResponseBean response = new ResponseBean();
		StringBuffer str = new StringBuffer();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		try {
			String queryForValidation = "select case when exists(select id from student where id = "+student.getId()+" and student_status = 'active') then 'true' else 'false' end";
			Boolean ifExists = jdbcTemplate.queryForObject(queryForValidation, Boolean.class);

			if(ifExists) {
				if(student.getMarks() != 0) {
					str.append(" , marks = '"+student.getMarks()+"'");
				}if(!TextUtils.isEmpty(student.getGrade()) && student.getGrade() != null) {
					str.append(" , grade = '"+student.getGrade()+"'");
				}
				if(!TextUtils.isEmpty(student.getName()) && student.getName() != null) {
					str.append(" , name = '"+student.getName()+"'");
				}


				String updateQuery = "update student set updated_date = '"+timestamp+"'"+str+" where id = "+student.getId()+" limit 1";
				int result = jdbcTemplate.update(updateQuery);

				if(result > 0) {
					response.setIsValid(true);
					response.setMessage("Updated SuccessFully..");
				}else {
					response.setIsValid(false);
					response.setMessage("Fail to update...");
				}
			}else {
				response.setIsValid(false);
				response.setMessage("No records found...");
			}
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while getting records" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseBean deleteStudent(Student student) {
		ResponseBean response = new ResponseBean();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		try {
			String queryForValidation = "select case when exists(select id from student where id = "+student.getId()+" and student_status = 'active') then 'true' else 'false' end";
			Boolean ifExists = jdbcTemplate.queryForObject(queryForValidation, Boolean.class);

			if(ifExists) {
				String updateQuery = "update student set updated_date = '"+timestamp+"', student_status = 'inactive' where id = "+student.getId()+" limit 1";
				int result = jdbcTemplate.update(updateQuery);

				if(result > 0) {
					response.setIsValid(true);
					response.setMessage("Updated SuccessFully..");
				}else {
					response.setIsValid(false);
					response.setMessage("Fail to update...");
				}
			}else {
				response.setIsValid(false);
				response.setMessage("No records found...");
			}
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while getting records" + e.getMessage());
		}
		return response;
	}
	
	@Override
	public ResponseBean getRecordsStudent(String name) {
		ResponseBean response = new ResponseBean();
		List<Student> list = new ArrayList<Student>();

		try {
			String queryForValidation = "select case when exists(select id from student where name = '"+name+"' and student_status = 'active') then 'true' else 'false' end";
			Boolean ifExists = jdbcTemplate.queryForObject(queryForValidation, Boolean.class);

			if(ifExists) {
				String query = "select id, name,subject, mobile, marks,grade from student where student_status = 'active' and name = '"+name+"'";
				SqlRowSet rs = jdbcTemplate.queryForRowSet(query);

				while(rs.next()) {
					Student student = new Student();
					student.setId(rs.getInt("id"));
					student.setName(rs.getString("name"));
					student.setSubject(rs.getString("subject"));
					student.setMobile(rs.getLong("mobile"));
					student.setMarks(rs.getInt("marks"));
					student.setGrade(rs.getString("grade"));

					list.add(student);
				}

				if(list.size() > 0) {
					response.setIsValid(true);
					response.setMessage("Details available..");
					response.setStudentList(list);
				}else {
					response.setIsValid(false);
					response.setMessage("No records found..");
				}
			}else {
				response.setIsValid(false);
				response.setMessage("No records found..");
			}	
		}catch (Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while getting records" + e.getMessage());
		}
		return response;
	}
	
	@Override
	public ResponseBean login(Users users) {
		ResponseBean response = new ResponseBean();
		String userName = null;
		
		try {
			String queryForUserName = "select user_name from users where user_name = '"+users.getUserName()+"' and user_status = 'active' limit 1";
			userName = jdbcTemplate.queryForObject(queryForUserName, String.class);
			
			if(userName != null ) {
				String queryForValidation = "select case when exists(select user_password from users where user_name = '"+userName+"' and user_password = '"+users.getPassword()+"') then 'true' else 'false' end";
				Boolean ifExists = jdbcTemplate.queryForObject(queryForValidation, Boolean.class);
				
				if(ifExists) {
					response.setIsValid(true);
					response.setMessage("Login successfully..");
				}else {
					response.setIsValid(false);
					response.setMessage("Fail to Login..");
				}
			}else {
				response.setIsValid(false);
				response.setMessage("No user found..");
			}
		}catch(Exception e) {
			response.setIsValid(false);
			response.setMessage("Something is wrong..");
			LOGGER.error("Error while login" + e.getMessage());
		}
		return response;
	}
}
