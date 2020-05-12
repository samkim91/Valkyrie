<?php

require('../dbconn.php');

$id = $_POST['id'];
$pwd = $_POST['pwd'];
$name = $_POST['name'];

$sql = "INSERT INTO users (uid, upwd, uname) VALUES ('$id', '$pwd', '$name')";

if($conn -> query($sql) === TRUE ){
  echo "ok";
}else{
  echo "error: ". $sql . "<br>" . $conn->error;
}

$conn -> close();









 ?>
