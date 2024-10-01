
<%@page import="java.util.Map.Entry"%>
<%@page import="com.pay10.commons.util.Fields"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.pay10.commons.api.Hasher"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="java.util.Map"%>
<%@page import="com.pay10.commons.util.Amount"%>
<%@page import="org.json.JSONObject"%>
<%@page import="com.pay10.pg.core.util.MerchantHostedUtils"%>
<%@page import="com.pay10.commons.api.TransactionControllerServiceProvider"%>


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="../css/bootstrap.min.css">
		<script src="../js/bootstrap.min.js"></script>
		<script src="../js/jquery.min.js"></script>
		<link rel="stylesheet" href="../css/invoiceResponse.css">
			<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Response Format</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
	<style>
div.example {
	background-color: lightgrey;
	padding: 20px;
}

@media screen and (min-width: 601px) {
	div.example {
		font-size: 30px;
	}
}

@media screen and (max-width: 600px) {
	div.example {
		font-size: 30px;
	}
}

.container {
	margin: 0 auto;
	margin-top: 35px;
	padding: 40px;
	width: 750px;
	height: auto;
	background-color: #fff;
}

table {
	border: 1px solid #333;
	border-collapse: collapse;
	margin: 0 auto;
	width: 100%;
}

td, tr, th {
	padding: 5px;
	border: 1px solid #333;
}

th {
	background-color: #f0f0f0;
}

h4, p {
	margin: 0px;
}

::-webkit-scrollbar {
	-webkit-appearance: none;
}

::-webkit-scrollbar:vertical {
	width: 12px;
}

::-webkit-scrollbar:horizontal {
	height: 12px;
}

::-webkit-scrollbar-thumb {
	background-color: #468bd8;
	border-radius: 10px;
	border: 2px solid #ffffff;
}

::-webkit-scrollbar-track {
	border-radius: 10px;
	background-color: #ffffff;
}

@media screen and (max-width: 480px) {
#failureResponse {
   width: 100% !important;
    height: 519px !important;
}
#successResponse {
  width: 100% !important;
    height: 519px !important;
}
.screen_red h3 {
    top: 26% !important;
}
}
</style>
</head>

<body>
	<%
		Map<String, String> requestMap = new HashMap<String, String>();
	
		String encData = request.getParameter("ENCDATA");
		String payId = request.getParameter("PAY_ID");
		
		System.out.println("<<<<<<<<<<<<<<> encData <>>>>>>>>>>>>>> "+encData);
		
		requestMap = new TransactionControllerServiceProvider().decrypt(payId, encData);
		
		String mapResp = requestMap.get("ENCDATA");
		System.out.println("<<<<<<<- mapResp ->>>>>>>>> "+mapResp);
		System.out.println("<<<<<<<- requestMap ->>>>>>>>> ");
		System.out.println("<<<<<<<<<<<<<<> requestMap <>>>>>>>>>>>>>> "+requestMap);
		
		
		Map<String, String> myMap = new HashMap<String, String>();
		String[] pairs = mapResp.split("~");
		for (int i=0;i<pairs.length;i++) {
		    String pair = pairs[i];
		    String[] keyValue = pair.split("=");
		    if(keyValue[0]!= null && !keyValue[0].isEmpty()){
		    	if (keyValue.length > 1) {
					myMap.put(keyValue[0], keyValue[1]);
				}
		        /* myMap.put(keyValue[0],keyValue[1]); */
		    }
		
	%>



	<%
		}
	%>

	<%
		Fields fields = new Fields(myMap);
		//String hashRecieved = request.getParameter("HASH");
		String hashRecieved = myMap.get("HASH");
		fields.remove("HASH");
		String generatedHash = "";
		try {
			generatedHash = Hasher.getHash(fields);
		} catch (Exception exception) {

		}
		String parsedAmount = myMap.get("TOTAL_AMOUNT") != null && Integer.valueOf(myMap.get("TOTAL_AMOUNT")) > 0 ? 
				Amount.toDecimal(myMap.get("TOTAL_AMOUNT"), myMap.get("CURRENCY_CODE")) : 
					Amount.toDecimal(myMap.get("AMOUNT"), myMap.get("CURRENCY_CODE"));
	%>
	<div class="modal " id="myModal" role="dialog" style="display: none;">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header"
					style="background-color: #337ab7; color: white;">
					<!-- <button type="button" class="close" data-dismiss="modal">&times;</button> -->
					<h4 class="modal-title">Payment Status</h4>
				</div>
				<div class="modal-body"
					style="text-align: center; font-size: 20px; font-weight: 500;">
					<%
						if (myMap.get("RESPONSE_CODE") != null && myMap.get("STATUS") != null) {
							String respSer = (String) myMap.get("RESPONSE_CODE");
							String respGen = "000";
							String statSer = (String) myMap.get("STATUS");
							String statGen = "Captured";
							
							if (respGen.equals(respSer) && statGen.equalsIgnoreCase(statSer) && fields.get("ACQUIRER_TYPE") != null && fields.get("ACQUIRER_TYPE").equalsIgnoreCase("SBI")) {
								JSONObject reqJson = null;
								if(fields.get("SBI_FINAL_REQUEST")!=null){
								reqJson = new JSONObject(fields.get("SBI_FINAL_REQUEST"));
								}
								if (reqJson != null && reqJson.getString("paymentOption") != null && reqJson.getString("paymentOption").trim().equalsIgnoreCase("card")) {
									out.print("Payment Status:SUCCESS");
								} else if(respSer.equals(respGen) && statSer.equalsIgnoreCase(statGen) && generatedHash.equals(hashRecieved)){
									out.print("Payment Status:SUCCESS");
								}else {
									out.print("Payment Status:Not Successfull");
								}
							} else if (respSer.equals(respGen) && statSer.equalsIgnoreCase(statGen) && generatedHash.equals(hashRecieved)) {
								out.print("Payment Status:SUCCESS");
							} else {
								out.print("Payment Status:Not Successfull");
							}
						}
					%>
				</div>
				<div class="modal-footer"
					style="text-align: center; overflow: scroll !important;">
					<table>

						<thead>
							<tr>
								<th colspan="3">InvoiceNo: <%
									out.print(myMap.get("ORDER_ID"));
								%></th>
								<th></th>
							</tr>

						</thead>
						<tbody>
							<tr>
								<th>Customer Name</th>
								<th>Customer Phone</th>
								<th>Customer Email</th>
								<th>Product Description</th>


							</tr>
							<tr>
								<td>
									<%
										out.print(myMap.get("CUST_NAME"));
									%>
								</td>
								<td>
									<%
										out.print(myMap.get("CUST_PHONE"));
									%>
								</td>
								<td>
									<%
										out.print(myMap.get("CUST_EMAIL"));
									%>
								</td>
								<%-- <td><% out.print(request.getParameter("PRODUCT_DESC"));%></td> --%>
							</tr>
							<tr>
								<th>PG Response</th>
								<th>Pg Ref Number</th>
								<th>Transaction Id</th>
								<th>Response Date & Time</th>
							</tr>
							<tr>
								<td>
									<%
										out.print(myMap.get("RESPONSE_MESSAGE"));
									%>
								</td>
								<td>
									<%
										out.print(myMap.get("PG_REF_NUM"));
									%>
								</td>
								<td>
									<%
										out.print(myMap.get("TXN_ID"));
									%>
								</td>
								<td>
									<%
										out.print(myMap.get("RESPONSE_DATE_TIME"));
									%>
								</td>
							</tr>
							<tr>
								<th>Currency Type</th>
								<th>Total Amount</th>
								<th>Payment Type</th>
								<th>Payment Status</th>
							</tr>
							<tr>
								<td>
									<%
										String cur = (String) myMap.get("CURRENCY_CODE");
										String statCur = "356";
										if (cur.equals(statCur)) {
											out.print("INR");
										} else {
											out.print(myMap.get("CURRENCY_CODE"));
										}
									%>
								</td>
								<td>
									<%
										out.print(parsedAmount);
									%>
								</td>
								<td>
									<%
										out.print(myMap.get("PAYMENT_TYPE"));
									%>
								</td>
								<td>
									<%
										out.print(myMap.get("STATUS"));
									%>
								</td>
							</tr>
						</tbody>

					</table>
				</div>
			</div>

		</div>
	</div>


	<input type="hidden"
		value='<%out.print(myMap.get("RESPONSE_CODE"));%>'
		id="responseCodeId" />
	<input type="hidden"
		value='<%out.print(myMap.get("STATUS"));%>'
		id="responseMessage" />

	<div class="screen un" id="successResponse" style="display: none;">
		<div class="border-top"></div>

		<img src="../image/success.gif" style="width: 45%">
			<h3>
				<%
					out.print("Payment Status:SUCCESS");
				%>
			</h3> <br>
				<div class="row">
					<div class="col-md-12">
						<table>

							<thead>


							</thead>
							<tbody>
								<tr>
									<th>Invoice Number</th>
									<th>Order Id</th>
								</tr>
								<tr>
									<td>
										<%
											out.print(myMap.get("INVOICE_NO"));
										%>
									</td>
									<td>
										<%
											out.print(myMap.get("ORDER_ID"));
										%>
									</td>
								</tr>

								<tr>

									<th>Name</th>
									<th>Phone</th>
								</tr>

								<tr>

									<td>
										<%
											out.print(myMap.get("CUST_NAME"));
										%>
									</td>
									<td>
										<%
											out.print(myMap.get("CUST_PHONE"));
										%>
									</td>

								</tr>
								<tr>

									<th>Email</th>
									<th>Total Amount</th>

								</tr>
								<tr>

									<td>
										<%
											out.print(myMap.get("CUST_EMAIL"));
										%>
									</td>
									<td>
										<%
											out.print(parsedAmount);
										%>
									</td>

								</tr>
								<tr>
									<th>PG Response</th>
									<th>Pg Ref Number</th>
								</tr>
								<tr>
									<td>
										<%
											out.print(myMap.get("RESPONSE_MESSAGE"));
										%>
									</td>
									<td>
										<%
											out.print(myMap.get("PG_REF_NUM"));
										%>
									</td>
								</tr>
								<tr>
									<th>Transaction Id</th>
									<th>Response Date & Time</th>
								</tr>
								<tr>
									<td>
										<%
											out.print(myMap.get("TXN_ID"));
										%>
									</td>
									<td>
										<%
											out.print(myMap.get("RESPONSE_DATE_TIME"));
										%>
									</td>
								</tr>

							</tbody>

						</table>

					</div>
				</div> <!-- <button id="btnClick">CONTINUE</button> -->
	</div>


	<div class="screen_red red_un" id="failureResponse"
		style="display: none;">
		<div class="border-top-red"></div>

		<img src="../image/failed.gif" style="width: 45%">

			<h3>
				<%
					System.out.print("");
					out.print("Payment Status:Not Successfull");
				%>
			</h3> <br>
				<div class="row">
					<div class="col-md-12">
						<table>

							<thead>


							</thead>
							<tbody>
								<tr>
									<th class="red_th">Invoice Number</th>
									<th class="red_th">Order Id</th>

								</tr>
								<tr>
									<td>
										<%
											out.print(myMap.get("INVOICE_NO"));
										%>
									</td>
									<td>
										<%
											out.print(myMap.get("ORDER_ID"));
										%>
									</td>
								</tr>

								<tr>

									<th class="red_th">Name</th>
									<th class="red_th">Phone</th>
								</tr>

								<tr>

									<td>
										<%
											out.print(myMap.get("CUST_NAME"));
										%>
									</td>
									<td>
										<%
											out.print(myMap.get("CUST_PHONE"));
										%>
									</td>

								</tr>
								<tr>

									<th class="red_th">Email</th>
									<th class="red_th">Total Amount</th>

								</tr>
								<tr>

									<td>
										<%
											out.print(myMap.get("CUST_EMAIL"));
										%>
									</td>
									<td>
										<%
											out.print(parsedAmount);
										%>
									</td>

								</tr>
								<tr>
									<th class="red_th">PG Response</th>
									<th class="red_th">Pg Ref Number</th>
								</tr>
								<tr>
									<td>
										<%
											out.print(myMap.get("RESPONSE_MESSAGE"));
										%>
									</td>
									<td>
										<%
											out.print(myMap.get("PG_REF_NUM"));
										%>
									</td>
								</tr>
								<tr>
									<th class="red_th">Transaction Id</th>
									<th class="red_th">Response Date & Time</th>
								</tr>
								<tr>
									<td>
										<%
											out.print(myMap.get("TXN_ID"));
										%>
									</td>
									<td>
										<%
											out.print(myMap.get("RESPONSE_DATE_TIME"));
										%>
									</td>
								</tr>

							</tbody>

						</table>

					</div>
				</div> <!-- <button id="btnClick">Try Again</button> -->
	</div>

	<script>
		var resCode = $('#responseCodeId').val();
		var resMsg = $('#responseMessage').val();
		console.log("Response Code : " + resCode);
		console.log("Response Msg : " + resMsg);
		if (resCode == 000 && resMsg == "Captured") {
			$("#successResponse").show();
			$("#failureResponse").hide();
		} else {
			$("#failureResponse").show();
			$("#successResponse").hide();
		}
	</script>
	<script>
		var myWindow;

		function closeWin() {
			window.close();
		}
	</script>
</body>

			</html>