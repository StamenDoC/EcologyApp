<?php
	/* VRACAMO INFORMACIJE IGRACA */

	include 'config.php';

	$player_id = mysql_real_escape_string(htmlentities($_REQUEST['id']));
	$return_type = mysql_real_escape_string(htmlentities($_REQUEST['type'])); //da li vracam sve registrovane igrace ili samo prijatelje

	if(isset($_REQUEST['id']) && isset($_REQUEST['type']))
	{
		$return_users = array();

		$status = "online";

		if($return_type == "all")
		{
			//izvlacimo sve online korisnike

			$sql = mysql_query("SELECT * FROM `users` WHERE `Status`='$status' AND `Id`!='$player_id'");

			if(mysql_num_rows($sql) > 0)
			{
				while(($row = mysql_fetch_assoc($sql)) !== false)
				{
					$id_player_db = $row['Id'];

					//ispitujemo da li su prijatelji
					$sql1 = mysql_query("SELECT `Id` FROM `friends` WHERE (`Id_user1`='$player_id' AND `Id_user2`='$id_player_db') OR (`Id_user1`='$id_player_db' AND `Id_user2`='$player_id')");

					if(mysql_num_rows($sql1) > 0)
					{
						//jesu prijatelji

						$return_users[] = array(

							'Username'		=> $row['Username'], 
							'Email' 		=> $row['Email'],
							'FirstName' 	=> $row['FirstName'],
							'LastName' 		=> $row['LastName'],
							'Avatar' 		=> $row['Avatar'],
							'PhoneNumber' 	=> $row['PhoneNumber'],
							'Longitude' 	=> $row['Longitude'],
							'Latitude' 		=> $row['Latitude']
						);
					}

					else
					{
						//nisu prijatelji
						$return_users[] = array(

							'Username' 		=> '', 
							'Email' 		=> '',
							'FirstName' 	=> $row['FirstName'],
							'LastName' 		=> $row['LastName'],
							'Avatar' 		=> '',
							'PhoneNumber' 	=> '',
							'Longitude' 	=> $row['Longitude'],
							'Latitude' 		=> $row['Latitude']
						);
					}
				}
			}
		}

		else if($return_type == "friends")
		{
			//izvlacimo sve moje prijatelje

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

					$sql1 = mysql_query("SELECT * FROM `users` WHERE `Status`='$status' AND `Id`='$id_player_db'");

					if(mysql_num_rows($sql1) > 0)
					{
						while(($row1 = mysql_fetch_assoc($sql1)) !== false)
						{
							$return_users[] = array(

								'Username'		=> $row1['Username'], 
								'Email' 		=> $row1['Email'],
								'FirstName' 	=> $row1['FirstName'],
								'LastName' 		=> $row1['LastName'],
								'Avatar' 		=> $row1['Avatar'],
								'PhoneNumber' 	=> $row1['PhoneNumber'],
								'Longitude' 	=> $row1['Longitude'],
								'Latitude' 		=> $row1['Latitude']
							);
						}
					}
				}
				
			}

		}

		echo json_encode($return_users);
	}

	else
	{
		//nisi poslao sve parametre
		echo "-1";
	}
?>