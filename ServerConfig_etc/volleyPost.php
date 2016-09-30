<?php
	error_reporting(0);
	include("dbConnect.php");
	$response = array();

 	$event_type = $_POST['DescribeEvents'];
 	$datetime = $_POST['CurrentTimeEvents'];
 	$lat = (float)$_POST['Latitude'];
  	$long = (float)$_POST['Longtitude'];
	$level = (int)$_POST['Level'];
	$radius = (int)$_POST['Radius'];
  	$timestamp = strtotime($datetime);
	$date = date("Y-m-d H:i:s", $timestamp);
 	$sql = "INSERT INTO AlertEntry (DescribeEvents, Latitude, Longitude, CurrentTimeEvents, Level, Radius) VALUES ('$event_type','$lat','$long','$date','$level','$radius')";
 	$result = mysql_query($sql);
 	if($result>0){
           $response["status"] = "Successfully updated event";
         }    
     else{
           $response["status"] = "Something went wrong";
         }
     // echoing JSON response
     echo json_encode($response);
?>