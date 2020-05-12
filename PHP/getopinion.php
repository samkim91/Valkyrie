<?php

  require('dbconn.php');

  $paperId = $_GET['paperId'];

  $sql = "SELECT * FROM afterauth WHERE paperId = $paperId";

  $result = mysqli_query($conn, $sql);

  if(mysqli_num_rows($result) > 0){
    echo "ok";
  }else{
    echo "no";
  }

  $conn -> close();
 ?>
