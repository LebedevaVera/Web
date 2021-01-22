<?php

if(isset($_POST['submit-signup'])) {
	require 'db.php';
	
	$firstname = $_POST['firstname'];
	$lastname = $_POST['lastname'];
	$age = $_POST['age'];
	$email = $_POST['email'];
	$password = $_POST['password'];
	if(isset($_POST['newsletter_signup'])) {
		$newsletter_signup = $_POST['newsletter_signup'];
	} else {
		$newsletter_signup = 0;
	}
	
	
	if(!filter_var($email, FILTER_VALIDATE_EMAIL)) {
		header("Location: ../index.html?error=invalid_email");
		exit();
	} else if(preg_match('/[\/|\'^!$%&*()}{@#~?<>,=_+.1234567890]/', $firstname)) {
		header("Location: ../index.html?error=invalid_firstname");
		exit();
	} else if(preg_match('/[\/|\'^!$%&*()}{@#~?<>,=_+.1234567890]/', $lastname)/*preg_match("/^[^<,\"@/{}()*$%?=>:|;#]*$/", $lastname)*/) {
		header("Location: ../index.html?error=invalid_lastname");
		exit();
	} else {
		if (mysqli_connect_errno()) {
			die("Connection failed");
		}
		$res = get_email($conn, $email);
		
		if($res) {
			header("Location: ../index.html?error=email_extsts");
			exit();
		} else {
			create_user($conn, $firstname, $lastname, $age, $email, $password, $newsletter_signup);
			header("Location: ../index.html");
		}
	}
} else {
	header("Location: ../index.html");
	exit();
}

function get_email($conn, $email) {
	$query_email = "SELECT email FROM users WHERE email=?";
	$stmt = $conn->prepare($query_email);
	$stmt->bind_param("s", $email);
	$stmt->execute();
	$result_email = $stmt->get_result();
	$res_email = 0;
	if($row = $result_email->fetch_assoc()) {
		$res_email = $row['email']; 
	}
	$stmt->close();
	return $res_email;
}

function create_user($conn, $firstname, $lastname, $age, $email, $password, $newsletter_signup) {
	if($newsletter_signup == "on") {
		$newsletter_signup = 1;
	}
	$query_insertuser = "INSERT INTO users (firstname, lastname, age, email, password, newsletter_signup) VALUES (?, ?, ?, ?, ?, ?)";
	$stmt = $conn->prepare($query_insertuser);
	$pass_hashed = md5($password);
	$stmt->bind_param("ssissi", $firstname, $lastname, $age, $email, $pass_hashed, $newsletter_signup);
	$stmt->execute();
	$stmt->close();
	
	$userid = get_userid($conn, $email);
	$query_insertdate = "INSERT INTO users_entity (userid) VALUES (?)";
	$stmt = $conn->prepare($query_insertdate);
	$stmt->bind_param("i", $userid);
	$stmt->execute();
	$stmt->close();
}	

function get_userid($conn, $email) {
	$query_userid = "SELECT userid FROM users WHERE email=?";
	$stmt = $conn->prepare($query_userid);
	$stmt->bind_param("s", $email);
	$stmt->execute();
	$res_userid = $stmt->get_result();
	$userid = 0;
	if($row = $res_userid->fetch_assoc()) {
		$userid = $row['userid']; 
	}
	$stmt->close();
	return $userid;
}