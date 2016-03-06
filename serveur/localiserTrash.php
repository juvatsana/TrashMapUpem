//nombre de visite d'une poubelle

<?php
$con=mysqli_connect("mysql.hostinger.fr","u978485106_admin","trashadmin","u978485106_trash");

$sql=select id_poubelle from poubelle where longitude = ???;

if (mysqli_query($con,$sql))
{
   echo "poubelle trouvee";
}
?>
