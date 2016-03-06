//supprimer une poubelle

<?php
$con=mysqli_connect("mysql.hostinger.fr","u978485106_admin","trashadmin","u978485106_trash");

$sql= DELETE FROM `poubelle` WHERE `id`=???;
if (mysqli_query($con,$sql))
{
   echo "Trash have been deleted successfully";
}
?>
