<%@page import="org.springframework.security.web.WebAttributes; org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<%@page import="org.springframework.security.core.context.SecurityContextHolder" %>

<html>

<head>
	<title>Exit User</title>
</head>

<body>
<div class="body">
	<div class="dialog">

		<h1>Exit User</h1>

		<g:if test="${params.login_error}">
			<font color="red">
				Your 'Exit User' attempt was not successful, try again.<br/><br/>
				Reason: ${session[WebAttributes.AUTHENTICATION_EXCEPTION]?.message}
			</font>
		</g:if>

		<g:set var='auth' value='${SecurityContextHolder.context.authentication}'/>
		<g:form controller='/logout/impersonate'>
			<table>
				<tr>
					<td>Current User:</td>
					<td><g:if test='${auth}'>${auth.principal}</g:if>&nbsp;</td>
				</tr>
				<tr>
					<td colspan='2'><input name="exit" type="submit" value="Exit"></td>
				</tr>
			</table>
		</g:form>

	</div>
</div>
</body>
</html>
