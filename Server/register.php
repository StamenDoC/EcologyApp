<?php

	include 'confing.php';

	$username = mysql_real_escape_string(htmlentities($_REQUEST['username']));
	$password = mysql_real_escape_string(htmlentities($_REQUEST['password']));
	$email = mysql_real_escape_string(htmlentities($_REQUEST['email']));
	$firstname = mysql_real_escape_string(htmlentities($_REQUEST['firstname']));
	$lastname = mysql_real_escape_string(htmlentities($_REQUEST['lastname']));
	$encoded_image = $_REQUEST['picture'];
	$phonenumber = mysql_real_escape_string(htmlentities($_REQUEST['phonenumber']));


	if( isset($username) && isset($password) && isset($email) && isset($firstname) && isset($lastname) && isset($encoded_image) && isset($phonenumber) )
	{

		//prvo ispitujemo da li postoji neko na tu email adresu i da li je je slobodan taj email

		$not_exist = true;
		$email_exist = false;
		$username_exist = false;

		$sql = mysql_query("SELECT `Id` FROM `users` WHERE `Email`='$email'");

		if(mysql_num_rows($sql) > 0)
		{
			$not_exist = false;
			$email_exist = true;
		}

		else
		{
			$sql = mysql_query("SELECT `Id` FROM `users` WHERE `Username`='$username'");

			if(mysql_num_rows($sql) > 0)
			{
				$not_exist = false;
				$username_exist = true;
			}
		}

		if($not_exist)
		{
			//kodiram password

			$password_code = password_hash($password, PASSWORD_DEFAULT);

			$decoded_image = base64_decode($encoded_image);

			$image_name = $username . "_" . $phonenumber . ".png";
			$path_folder = 'images/' . $username;
			$path = 'images/' . $username . "/" . $image_name;

			if (!file_exists($path_folder)) {
			    mkdir($path_folder, 0777, true);
			}

			$file = fopen($path, 'wb');

			$is_written = fwrite($file, $decoded_image);

			if($is_written > 0)
			{
				$sql = mysql_query("INSERT INTO `users` VALUES ('','$username','$password_code','$email','$firstname','$lastname','$path','$phonenumber')");

				if($sql)
				{
					echo "sucess";
				}

				else
				{
					echo "failed";
				}
			}
		}

		else
		{
			if($email_exist)
			{
				echo "zauzeta email adresa";
			}

			else if($username_exist)
			{
				echo "zauzet username";
			}
		}
	}

	else
	{
		echo "nije sve poslato";
	}

?>