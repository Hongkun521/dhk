<%--
  Created by IntelliJ IDEA.
  User: Hongkun Ding
  Date: 2021/8/14
  Time: 11:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    $.ajax({
    url:"",
    data:{

    },
    type:"" ,
    dataType:"",
    success:function (data) {

    }
    })

    $(".time").datetimepicker({
        minView: "month",
        language:  'zh-CN',
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true,
        pickerPosition: "bottom-left"
    });
</body>
</html>
