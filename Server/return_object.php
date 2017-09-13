<?php
	/* VRACAMO INFORMACIJE IGRACA */

	include 'confing.php';

	function getDistanceFromLatLonInKm($lat1,$lon1,$lat2,$lon2) 
	{
	  $R = 6371; // Radius of the earth in km
	  $dLat = deg2rad($lat2-$lat1);  // deg2rad below
	  $dLon = deg2rad($lon2-$lon1); 
	  $a = sin($dLat/2) * sin($dLat/2) + cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * sin($dLon/2) * sin($dLon/2); 
	  $c = 2 * atan2(sqrt($a), sqrt(1-$a)); 
	  $d = $R * $c; // Distance in km

	  return $d;
	}

	$player_id = mysql_real_escape_string(htmlentities($_REQUEST['id']));
	$type_search = mysql_real_escape_string(htmlentities($_REQUEST['type_search']));
	$value_search = mysql_real_escape_string(htmlentities($_REQUEST['value_search']));

	$player_lon = mysql_real_escape_string(htmlentities($_REQUEST['lon']));
	$player_lat = mysql_real_escape_string(htmlentities($_REQUEST['lat']));

	if(isset($_REQUEST['id']) && isset($_REQUEST['type_search']) && isset($_REQUEST['value_search']) && isset($_REQUEST['lon']) && isset($_REQUEST['lat']))
	{
		$return_object = array();

		if($type_search == "type")
		{
			$sql = mysql_query("SELECT * FROM `object` WHERE `Type`='$value_search' AND `Id_user`!='$player_id'");

			if(mysql_num_rows($sql) > 0)
			{
				while(($row = mysql_fetch_assoc($sql)) !== false)
				{

					$return_object[] = array(

						'Description'	=> $row['Description'], 
						'Image' 		=> $row['Image'],
						'Type' 			=> $row['Type'],
						'Longitude' 	=> $row['Longitude'],
						'Latitude' 		=> $row['Latitude'],
					);
				}
			}
		}

		else if($return_type == "radius")
		{
			$sql = mysql_query("SELECT * FROM `object` WHERE `Type`='$value_search' AND `Id_user`!='$player_id'");

			if(mysql_num_rows($sql) > 0)
			{
				//jesu prijatelji
				while(($row = mysql_fetch_assoc($sql)) !== false)
				{
					$km = getDistanceFromLatLonInKm($player_lat, $player_lon, $row['Latitude'], $row['Longitude']);

					$m = $km * 1000;

					if($value_search >= $m)
					{
						$return_object[] = array(

							'Description'	=> $row['Description'], 
							'Image' 		=> $row['Image'],
							'Type' 			=> $row['Type'],
							'Longitude' 	=> $row['Longitude'],
							'Latitude' 		=> $row['Latitude'],
						);
					}
				}
				
			}

		}

		echo json_encode($return_object);
	}

	else
	{
		//nisi poslao sve parametre
		echo "-1";
	}
?>