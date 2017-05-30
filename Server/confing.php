<?php
	session_start();
	error_reporting(1);

	$connect_error='Connect error with DataBase';
	mysql_connect("mysql.hostinger.co.uk","u621771648_root1","03032015") or die($connect_error);
	mysql_select_db("u621771648_eco") or die ($connect_error);
?>