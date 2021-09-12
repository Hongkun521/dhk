<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String basePath=request.getScheme()+"://" +request.getServerName() +":"+request.getServerPort() +request.getContextPath()+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
    <script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/en.js"></script>
<script type="text/javascript">

	$(function(){

        //为查询按钮绑定事件，触发pageList()方法
        $("#searchBtn").click(function () {
            /**
             * 点击查询按钮的时候,我们应该将搜索框中的信息保存起来,保存到隐藏域中
             */
            $("#hidden-owner").val($.trim($("#search-owner").val()));
            $("#hidden-name").val($.trim($("#search-name").val()));
            $("#hidden-customerName").val($.trim($("#search-customerName").val()));
            $("#hidden-stage").val($.trim($("#search-stage").val()));
            $("#hidden-transactionType").val($.trim($("#search-transactionType").val()));
            $("#hidden-source").val($.trim($("#search-source").val()));
            $("#hidden-contactsName").val($.trim($("#search-contactsName").val()));

            pageList(1,4);
        });

        //显示线索列表
        pageList(1,4);


        //为全选的复选框绑定事件
        $("#qx").click(function () {
            $("input[name=xz]").prop("checked",this.checked)
        });
        //这种方式是不行的，动态生成的元素（由js生成并拼接的），是不能以普通绑定事件的形式来进行操作的
        /*$("input[name=xz]").click(function () {

        })*/
        /**
         * 动态生成的元素,我们要以on方法的形式来触发事件
         * 语法:
         * 		$(需要绑定元素的有效的外层元素).on(绑定事件的方式,需要绑定的元素的jquery对象,回调函数)
         */
        $("#tranBody").on("click",$("input[name=xz]"),function () {
            $("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);
        })



	});


	function pageList(pageNo, pageSize) {
		//将全选的复选框的√给干掉
		$("#qx").prop("checked",false);


		// 将隐藏域中的值赋值给查询框,防止更换页数时,查询条件为空
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-customerName").val($.trim($("#hidden-customerName").val()));
		$("#search-stage").val($.trim($("#hidden-stage").val()));
		$("#search-transactionType").val($.trim($("#hidden-transactionType").val()));
		$("#search-source").val($.trim($("#hidden-source").val()));
		$("#search-contactsName").val($.trim($("#hidden-contactsName").val()));

		$.ajax({
			url:"workbench/transaction/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,

				"owner" : $.trim($("#search-owner").val()),
				"name" : $.trim($("#search-name").val()),
				"customerId" : $.trim($("#search-customerName").val()),
				"stage" : $.trim($("#search-stage").val()),
				"type" : $.trim($("#search-transactionType").val()),
				"source" : $.trim($("#search-source").val()),
				"contactsId" : $.trim($("#search-contactsName").val())
			},
			type:"get" ,
			dataType:"json",
			success:function (data) {
				/**
				 * data
				 * 	我们需要的: 交易活动信息列表
				 * 	[{市交易活动1},{2},{3}...] List<Activity> aList
				 * 	一会分页插件需要的: 查询出来的总记录数
				 *	{"total":?} int total
				 *
				 *	{"total":?,"dataList":[{交易活动1},{2},{3}...]}
				 */
				var html="";
				//每一个n就是一个市场活动对象
				$.each(data.tranList,function (i,n) {
					html+='<tr>',
					html+='<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>',
					html+='<td><a style="text-decoration: none; cursor: pointer;" href="workbench/transaction/detail.do?id='+n.id+'">'+n.customerId+'-'+n.name+'</a></td>',
					html+='<td>'+n.customerId+'</td>',
					html+='<td>'+n.stage+'</td>',
					html+='<td>'+n.type+'</td>',
					html+='<td>'+n.owner+'</td>',
					html+='<td>'+n.source+'</td>',
					html+='<td>'+n.contactsId+'</td>',
					html+='</tr>'

				})

				$("#tranBody").html(html);

				//计算总页数
				var  totalPages=data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;

				//数据处理完毕后，结合分页查询，对前端展现分页相关的信息
				$("#tranPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//回调函数是点击分页组件的时候触发的
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}

				})
			}
		})
	}
</script>
</head>
<body>

    <%--隐藏域,存放临时数据的--%>
    <input type="hidden" id="hidden-owner" />
    <input type="hidden" id="hidden-name" />
    <input type="hidden" id="hidden-customerName" />
    <input type="hidden" id="hidden-stage" />
    <input type="hidden" id="hidden-transactionType" />
    <input type="hidden" id="hidden-source" />
    <input type="hidden" id="hidden-contactsName" />
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>交易列表</h3>
			</div>
		</div>
	</div>
	
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
	
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">客户名称</div>
				      <input class="form-control" type="text" id="search-customerName">
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">阶段</div>
					  <select class="form-control" id="search-stage">
					  	<option></option>
                          <c:forEach items="${stageList}" var="s">
                              <option value="${s.value}">${s.text}</option>
                          </c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">类型</div>
					  <select class="form-control" id="search-transactionType">
					  	<option></option>
                          <c:forEach items="${transactionTypeList}" var="t">
                              <option value="${t.value}">${t.text}</option>
                          </c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">来源</div>
				      <select class="form-control" id="search-source">
						  <option></option>
						  <c:forEach items="${sourceList}" var="s">
                              <option value="${s.value}">${s.text}</option>
                          </c:forEach>
						</select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">联系人名称</div>
				      <input class="form-control" type="text" id="search-contactsName">
				    </div>
				  </div>
				  
				  <button type="submit" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" onclick="window.location.href='workbench/transaction/add.do';"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" onclick="window.location.href='workbench/transaction/edit.jsp';"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
							<td>客户名称</td>
							<td>阶段</td>
							<td>类型</td>
							<td>所有者</td>
							<td>来源</td>
							<td>联系人名称</td>
						</tr>
					</thead>
					<tbody id="tranBody">

						<%--<tr>
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/transaction/detail.jsp';">动力节点-交易01</a></td>
							<td>动力节点</td>
							<td>谈判/复审</td>
							<td>新业务</td>
							<td>zhangsan</td>
							<td>广告</td>
							<td>李四</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.html';">动力节点-交易01</a></td>
                            <td>动力节点</td>
                            <td>谈判/复审</td>
                            <td>新业务</td>
                            <td>zhangsan</td>
                            <td>广告</td>
                            <td>李四</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 20px;">
				<div id="tranPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>