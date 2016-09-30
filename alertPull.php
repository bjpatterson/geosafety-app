<?php
	error_reporting(0);
	include("dbConnect.php");
	$lat = (float)$_GET['Lat'];
	$long = (float)$_GET['Lon'];
	$response = array();

 	$sql = "SELECT * FROM AlertEntry as A WHERE SQRT(POW(A.Latitude-$lat,2) + POW(A.Longitude-$long,2))*80 <= A.Radius;"; // a crude approximation to narrow down results
	
 	$result = mysql_query($sql) or die("Did not run the query");
	
 	if(mysql_num_rows($result)>0){
		$response["orders"] = array();
		while ($row = mysql_fetch_array($result)) {
			// temp user array
			$alert = array();
			$alert["id"] = $row["Id"];
			$alert["DescribeEvents"] = $row["DescribeEvents"];
			$alert["Latitude"] = $row["Latitude"];
			$alert["Longitude"] = $row["Longitude"];
			$alert["CurrentTimeEvents"] = $row["CurrentTimeEvents"];
			$alert["Level"] = $row["Level"];
			$alert["Radius"] = $row["Radius"];
			
        
            // push ordered items into response array 
            array_push($response["orders"], $alert);
		}	
		// success
		$response["success"] = 1;
	}
	else {
    // order is empty 
      $response["success"] = 0;
    
}
     // echoing JSON response
     echo json_encode($response);
?>