<?php
	/* VRACAMO INFORMACIJE IGRACA */

	include 'config.php';

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

	$player_lon = mysql_real_escape_string(htmlentities($_REQUEST['lon']));
	$player_lat = mysql_real_escape_string(htmlentities($_REQUEST['lat']));

	$status = "online";

	$value_search = 50; // koliko metara

	$test_var = 0;

	if(isset($_REQUEST['id']) && isset($_REQUEST['type_search']) && isset($_REQUEST['lon']) && isset($_REQUEST['lat']))
	{
		$return_object = array();

		if($type_search == "friends")
		{
			$sql = mysql_query("SELECT `Id_user1`,`Id_user2` FROM `friends` WHERE `Id_user1`='$player_id' OR `Id_user2`='$player_id'");

			if(mysql_num_rows($sql) > 0)
			{
				//jesu prijatelji
				while(($row = mysql_fetch_assoc($sql)) !== false)
				{
					//uzimam id od mog prijatelja

					$id_player_db;

					if($player_id == $row['Id_user1'])
					{
						$id_player_db = $row['Id_user2'];
					}

					else
					{
						$id_player_db = $row['Id_user1'];
					}

					//ovde sada za tog igraca vadimo sve informacije (moglo je da se napravi slozeniji upit i da se zavrsi sve u jednom upitu gore ali ae da vidimo da li ce ovo sve da radi kako treba posle ide optimizacija)

					$sql1 = mysql_query("SELECT `Longitude`,`Latitude` FROM `users` WHERE `Status`='$status' AND `Id`='$id_player_db'");

					if(mysql_num_rows($sql1) > 0)
					{
						while(($row1 = mysql_fetch_assoc($sql1)) !== false)
						{
							$km = getDistanceFromLatLonInKm($player_lat, $player_lon, $row1['Latitude'], $row1['Longitude']);

							$m = $km * 1000;

							if($value_search <= $m)
							{
								$test_var = 1;

								echo "1";

								return;
							}
						}
					}
					else
					{
						echo "-1";
						return;
					}

					if($test_var == 1)
						return;
				}
				
			}
		}

		else if($type_search == "object")
		{
			$sql = mysql_query("SELECT `Longitude`,`Latitude` FROM `object` WHERE `Id_user`!='$player_id'");

			if(mysql_num_rows($sql) > 0)
			{
				while(($row = mysql_fetch_assoc($sql)) !== false)
				{
					$km = getDistanceFromLatLonInKm($player_lat, $player_lon, $row['Latitude'], $row['Longitude']);

					$m = $km * 1000;

					if($value_search <= $m)
					{
						echo "1";

						return;
					}
				}
				
			}
			else
				echo "-1";

		}

		
	}

	else
	{
		//nisi poslao sve parametre
		echo "-2";
	}
?>