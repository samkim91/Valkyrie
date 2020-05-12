<?php

 // DB에 사진주소를 저장하기 위한 값
 $serverURL = "http://34.64.150.54/images/";
 // 이미지를 저장할 폴더를 지정해줌.
 $saveLocation = './images/';
 // 사진 주소들을 클라이언트로 다시 보내주기 위해 임시배열을 하나 만듦.(제이슨화 필요)
 $tmpArray = array();

 // 클라이언트에서 보낸 파일들을 하나씩 받아와 이름을 생성해주고 다시 보낼 준비를 한다.
 // 다수 이미지를 저장하기 위해 foreach문을 사용함.
 // files라는 태그를 달고 클라이언트로부터 받아온 데이터를 다룬다.
 // $_FILES['태그명']['필요한 내용']에 대해서 보자면 '필요한 내용'에 name이 들어가면 받은 파일의 이름, size가 들어가면 받은 파일의 크기,
 // type은 받은 파일의 형식, tmp_name은 받은 파일의 임시 이름 등을 출력할 수 있다.
 foreach ($_FILES['files']['name'] as $key => $value) {
   // key번째 파일의 이름을 받아온다.
   $name = $_FILES['files']['name'][$key];
   // 위 파일의 이름이 파일명.확장자 형식으로 되어 있으니 이것을 쪼갠다.
   $uploadName = explode('.', $name);
   // 서버에 저장할 이름이 중복되면 안되기 때문에 시간+키값(몇번째 파일)로 다시 이름을 만들고, 여기에 위에서 빼온 파일확장자를 붙여준다.(uploadName[1]이 확장자)
   $uploadName = time().$key.'.'.$uploadName[1];
   // 저장할 위치와 파일이름을 합쳐서 서버에 저장하기 위한 마무리 작업을 준비한다.
   $uploadFile = $saveLocation.$uploadName;

   // 서버에 본격적으로 저장하는 부분. key번째 파일을, 위에서 만들어준 이름으로 이동(저장)하겠다는 의미.
   if(move_uploaded_file($_FILES['files']['tmp_name'][$key], $saveLocation.$uploadName)){
     $tmpArray[] = $serverURL.$uploadName;
   }
 }

 echo json_encode($tmpArray);


 ?>
