<?php
$con=mysqli_connect("mysql.hostinger.fr","u978485106_admin","trashadmin","u978485106_trash");

if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

$username = $_GET['username'];
$password = $_GET['password'];

$result = mysqli_query($con,"SELECT * FROM user where login='$username' and pass='$password'");
$row = mysqli_fetch_array($result);

$data = $row[0];

if($data){
	echo "Connexion user ok";
}
else{
	echo "User dont exist";
}

mysqli_close($con);
?>