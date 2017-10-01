<?php
	/* UBACUJEMO POENE */

	include 'config.php';

	$player_id = mysql_real_escape_string(htmlentities($_REQUEST['id']));
	$points = mysql_real_escape_string(htmlentities($_REQUEST['points']));

	
	if(isset($_REQUEST['id']) && isset($_REQUEST['description']))
	{
		
		$sql = mysql_query("UPDATE `users` SET `Points`=`Points` + '$points' WHERE `id`='$player_id'");

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

	else
	{
		//nije sve poslato
		echo "-2";
	}

?>