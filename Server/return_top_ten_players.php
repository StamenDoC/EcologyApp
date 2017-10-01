<?php

	include 'config.php';

	$sql = mysql_query("SELECT `Username`,`Avatar`,`Points` FROM `users` ORDER BY `Points` DESC LIMIT 10");

	if($sql)
	{

		if(mysql_num_rows($sql) > 0)
		{
			$top_10 = array();

			while(($row = mysql_fetch_assoc($sql)) !== false)
			{
				$top_10[] = array(

						'Username' => $row['Username'],
						'Avatar' => $row['Avatar'],
						'Points' => $row['Points']

					);
			}

			echo json_encode($top_10);
		}

		else
		{
			//nema usera
			echo "-2"
		}
	}

	else
	{
		//greska u DB
		echo "-1";
	}

?>