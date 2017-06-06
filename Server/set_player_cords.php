<?php
	/* POSTAVLJAMO KORDINATE IGRACA */

	include 'confing.php';

	$player_id = mysql_real_escape_string(htmlentities($_REQUEST['id']));
	$player_lon = mysql_real_escape_string(htmlentities($_REQUEST['lon']));
	$player_lat = mysql_real_escape_string(htmlentities($_REQUEST['lat']));

	if( isset($_REQUEST['id']) && isset($_REQUEST['lon']) && isset($_REQUEST['lat']) )
	{
		echo $player_lon . " ____ " ;
		$sql = mysql_query("UPDATE `users` SET `Longitude`='$player_lon', `Latitude`='$player_lat' WHERE `Id`='$player_id'");

		if($sql)
		{
			//success
			echo "1";
		}

		else
		{
			//greska prilikom upisa u bazu
			echo "-1";
		}
	}

	else
	{
		//nisi poslao sve parametre
		echo "-2";
	}

?>