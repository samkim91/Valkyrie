<?php

  require('dbconn.php');

  $userid = $_GET['userid'];

  $sql = "SELECT * FROM userkey WHERE userid = '$userid'";

  $result = mysqli_query($conn, $sql);
  $data = array();

  if(mysqli_num_rows($result) > 0){
    //echo "ok";
    while($row=mysqli_fetch_array($result)){
      array_push($data,
        array('documentkey'=>$row[2]
      ));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("documentkey"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;

  }else{
    echo "no";
  }

  $conn -> close();
 ?>
