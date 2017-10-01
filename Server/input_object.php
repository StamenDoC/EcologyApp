<?php
	/* UBACUJEMO OBJEKAT */

	include 'config.php';

	$player_id = mysql_real_escape_string(htmlentities($_REQUEST['id']));
	$description = mysql_real_escape_string(htmlentities($_REQUEST['description']));
	$encoded_image = $_REQUEST['picture'];
	$type_object = mysql_real_escape_string(htmlentities($_REQUEST['type']));

	$player_lon = mysql_real_escape_string(htmlentities($_REQUEST['lon']));
	$player_lat = mysql_real_escape_string(htmlentities($_REQUEST['lat']));
	
	if(isset($_REQUEST['id']) && isset($_REQUEST['description']) && isset($_REQUEST['picture']) && isset($_REQUEST['type']) && isset($_REQUEST['lon']) && isset($_REQUEST['lat']))
	{
		$decoded_image = base64_decode($encoded_image);

		$image_name = $player_id . "_" . $type_object . "_" . time() . ".png";
		$path_folder = 'images/' . $type_object;
		$path = 'images/' . $type_object . "/" . $image_name;

		if (!file_exists($path_folder)) {
		    mkdir($path_folder, 0777, true);
		}

		$file = fopen($path, 'wb');

		$is_written = fwrite($file, $decoded_image);

		if($is_written > 0)
		{
			$sql = mysql_query("INSERT INTO `object` (`Id_user`,`Description`,`Image`,`Type`,`Longitude`,`Latitude`) VALUES ($player_id','$description','$path','$type_object','$player_lon','$player_lat')");

			if($sql)
			{
				//sucess
				echo "1";
			}

			else
			{
				//faild
				echo "-1";
			}
		}
	}

	else
	{
		//nije sve poslato
		echo "-2";
	}

?>