<?php

  require('dbconn.php');

  $paperId = $_POST['paperId'];
  $opinion = $_POST['opinion'];

  $sql = "INSERT INTO afterauth (paperId, opinion) VALUES ('$paperId', '$opinion')";

  if($conn -> query($sql) === TRUE ){
    echo "ok";
  }else{
    echo "error: ". $sql . "<br>" . $conn->error;
  }

  $conn -> close();

 ?>
