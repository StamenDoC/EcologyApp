<?php

	include 'confing.php';

	$username = mysql_real_escape_string(htmlentities($_REQUEST['username']));
	$password = mysql_real_escape_string(htmlentities($_REQUEST['password']));

	if( isset($_REQUEST['username']) && isset($_REQUEST['password']) )
	{
		$sql = mysql_query("SELECT `Id`,`Password` FROM `users` WHERE `Username`='$username'");

		if(mysql_num_rows($sql) > 0)
		{
			$row = mysql_fetch_row($sql);

			$password_db = $row[1];

			if(password_verify($password, $password_db)) 
			{
				//vracam Id igraca
				echo $row[0];
			}

			else
			{
				//netacna sifra
				echo "-1";
			}
		}

		else
		{
			//nepostojeci username
			echo "-2";
		}
	}

	else
	{
		//nisi poslao sve parametre
		echo "-3";
	}

?>