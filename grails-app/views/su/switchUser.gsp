<%@page import="org.springframework.security.web.WebAttributes; org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>

<html>

<head>
	<title>Switch User</title>
</head>

<body>
<div class="body">
	<div class="dialog">

		<h1>Switch to User</h1>

		<h3>Valid users:</h3>

		<p>username <b>rod</b>, password <b>koala</b></p>
		<p>username <b>dianne</b>, password <b>emu</b></p>
		<p>username <b>scott</b>, password <b>wombat</b></p>
		<p>username <b>bill</b>, password <b>wombat</b></p>
		<p>username <b>bob</b>, password <b>wombat</b></p>
		<p>username <b>jane</b>, password <b>wombat</b></p>
		<%-- this form-login-page form is also used as the form-error-page to ask for a login again. --%>
		<g:if test="${params.login_error}">
			<p>
				<font color="red">
					Your 'su' attempt was not successful, try again.<br/><br/>
					Reason: ${session[WebAttributes.AUTHENTICATION_EXCEPTION]?.message}
				</font>
			</p>
		</g:if>

		<g:form controller='/login/impersonate'>
			<table>
				<tr>
					<td>User:</td>
					<td><input type='text' name='username'></td>
				</tr>
				<tr>
					<td colspan='2'><input name="switch" type="submit" value="Switch to User"></td>
				</tr>
			</table>
		</g:form>

	</div>
</div>
</body>
</html>
