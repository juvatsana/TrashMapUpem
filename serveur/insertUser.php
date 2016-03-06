<?php
$con=mysqli_connect("mysql.hostinger.fr","u978485106_admin","trashadmin","u978485106_trash");

$sql="INSERT INTO user (login, pass) VALUES ('?????', '?????','?????')";
if (mysqli_query($con,$sql))
{
   echo "User have been inserted successfully";
}
?>
