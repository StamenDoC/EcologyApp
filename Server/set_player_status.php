<?php
	/* POSTAVLJAMO STATUS IGRACA */

	// Prilikom ulaska u igru postavljamo ga na online, a pri izlasku postavljamo na offline (pause, stop....)

	include 'confing.php';

	$player_id = mysql_real_escape_string(htmlentities($_REQUEST['id']));
	$player_status = $_REQUEST['status'];

	if( isset($_REQUEST['id']) && isset($_REQUEST['status']) )
	{
		$sql = mysql_query("UPDATE `users` SET `Status`='$player_status' WHERE `Id`='$player_id'");

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