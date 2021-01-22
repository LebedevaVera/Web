 <?php
 
 $dbhost = "localhost";
 $dbuser = "root";
 $dbpassword = "";
 $dbname = "backend_aufgabe";
 
 $conn = new mysqli($dbhost, $dbuser, $dbpassword, $dbname) 
	or die("Connect failed: %s\n". $conn -> error); 