<%@page import="org.springframework.security.web.WebAttributes; org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<html>

<head>
	<title>Login</title>
</head>

<body onload="document.f.elements['username'].focus();">

	<h1>Login</h1>

	<p>Valid users:
	<p>
	<p>username <b>rod</b>, password <b>koala</b>
	<p>username <b>dianne</b>, password <b>emu</b>
	<p>username <b>scott</b>, password <b>wombat</b>
	<p>username <b>peter</b>, password <b>opal</b> (user disabled)
	<p>username <b>bill</b>, password <b>wombat</b>
	<p>username <b>bob</b>, password <b>wombat</b>
	<p>username <b>jane</b>, password <b>wombat</b>
	<p>

	<p>Locale is: ${request.locale}</p>
	<%-- this form-login-page form is also used as the form-error-page to ask for a login again. --%>
	<g:if test="${params.login_error}">
		<font color="red">
			Your login attempt was not successful, try again.<br/><br/>
			Reason: ${session[WebAttributes.AUTHENTICATION_EXCEPTION]?.message}
		</font>
	</g:if>

	<g:if test="${params.login_error}">
		<g:set var='lastUsername' value="${session.SPRING_SECURITY_LAST_USERNAME}"/>
	</g:if>

	<form name="f" action='/login/authenticate' method="POST">
		<table>
			<tr>
				<td>User:</td>
				<td><input type='text' name='username' id='username' value="${lastUsername}"/></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='password' id='password'></td>
			</tr>
			<tr>
				<td><input type="checkbox" name="_spring_security_remember_me" id='remember_me'></td>
				<td>Don't ask for my password for two weeks</td>
			</tr>

			<tr><td colspan='2'><input name="submit" type="submit"></td></tr>
			<tr><td colspan='2'><input name="reset" type="reset"></td></tr>
		</table>
	</form>

</body>
</html>
