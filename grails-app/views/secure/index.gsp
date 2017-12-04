<html>

<head>
	<title>Your Contacts</title>
</head>

<body>

<div class="body">
	<div class="dialog">

		<h1><sec:username/>'s Contacts</h1>

		<g:if test="${flash.message}">
			<div class="message">${flash.message}</div>
		</g:if>

		<p>

		<table cellpadding='3' border='0'>
		<tr>
			<th>id</th>
			<th>Name</th>
			<th>Email</th>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
		</tr>
<g:each var="contact" in="${contacts}">
		<tr>
			<td>${contact.id}</td>
			<td>${contact.name}</td>
			<td>${contact.email}</td>
			<g:if test="${hasDeletePermission[contact]}">
				<td>
					<g:form action="del" controller="secure">
						<g:hiddenField name="contactId" value="${contact.id}"/>
						<g:submitButton name="del" value="${message(code: 'contact.del', default: 'Delete')}"/>
					</g:form>
				</td>
			</g:if>
			<g:else>
				<td>&nbsp;</td>
			</g:else>
			<g:if test="${hasAdminPermission[contact]}">
				<td><g:link action='adminPermission' params='[contactId: contact.id]'>Admin Permission</g:link></td>
			</g:if>
			<g:else>
				<td>&nbsp;</td>
			</g:else>
		</tr>
</g:each>
	</table>

	<p><g:link action='create'>Add</g:link></p>

	<g:form controller='logout'>
		<g:submitButton name='logoff' value='Logoff (also clears any remember-me cookie)' />
	</g:form>

	</div>
</div>
</body>
</html>
