<?php
	$dbhost = 'localhost:3306';
    $dbuser = 'root';
	$dbpass = 'root';
	$database = 'Project1';
	$conn = mysql_connect($dbhost, $dbuser, $dbpass) or die("could not connect");
	mysql_select_db($database, $conn) or die("Could not connect to database");
?>
