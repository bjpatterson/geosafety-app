<?php 

 if($_SERVER['REQUEST_METHOD']=='GET'){
	 
 
 $lat = (float)$_GET['Latitude'];
 $long = (float)$_GET['Longitude'];
  
 require_once('dbConnect.php');
 
 $sql = "SELECT * FROM AlertEntry as A WHERE SQRT(POW(A.Latitude-$lat,2) + POW(A.Longitude-$long,2))*80 <= A.Radius;"; // a crude approximation to narrow down results
 
 $r = mysql_query($sql);

 $result = array();
 
 while($res = mysql_fetch_assoc($r)){
	 array_push($result,array(
	 "event"=>$res['DescribeEvents'],
	 "lat"=>$res['Latitude'],
	 "lon"=>$res['Longitude'],
	 "level"=>$res['Level'],
	 "radius"=>$res['Radius']
	 )
	 );
 }
 
 echo json_encode(array("result"=>$result));
 
 mysql_close($conn);

 }
 ?>
 