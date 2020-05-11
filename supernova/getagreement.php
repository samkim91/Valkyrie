<?php

  require('dbconn.php');

  $userid = $_GET['userid'];

  $sql = "SELECT * FROM agreement WHERE userid = '$userid'";

  $result = mysqli_query($conn, $sql);

  $data = array();

  if(mysqli_num_rows($result) > 0){
    //echo "ok";
    while($row=mysqli_fetch_array($result)){
      array_push($data,
        array('num'=>$row[0],
              'time'=>$row[1],
              'docname'=>$row[2],
              'patname'=>$row[3],
              'content'=>$row[4],
              'papername'=>$row[5],
              'paperver'=>$row[6],
              'userid'=>$row[7],
      ));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("agreement"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;

  }else{
    echo "no";
  }

  $conn -> close();
 ?>
