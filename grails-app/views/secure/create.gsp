<html>

<head>
	<title>Add New Contact</title>
</head>

<body>
<div class="body">
	<div class="dialog">

		<h1>Add Contact</h1>

		<g:form action='add' method="POST">
			<table width="95%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
				<tr>
					<td alignment="right" width="20%">Name:</td>
					<td width="20%">
						<g:textField name="name" value="${fieldValue(bean: command, field: 'name')}"/>
					</td>
					<td width="60%">
						<font color="red"><g:fieldError bean='${command}' field='name' /></font>
					</td>
				</tr>

				<tr>
					<td alignment="right" width="20%">Email:</td>
					<td width="20%">
						<g:textField name="email" value="${fieldValue(bean: command, field: 'email')}"/>
					</td>
					<td width="60%">
						<font color="red"><g:fieldError bean='${command}' field='email' /></font>
					</td>
				</tr>

			</table>

			<br/>

			<g:hasErrors bean='${command}'>
				<b>Please fix all errors!</b><br/><br/>
			</g:hasErrors>

			<br/><br/>

			<input name="execute" type="submit" alignment="center" value="Execute">

		</g:form>

		<g:link controller='hello'>Home</g:link>
	</div>
</div>
</body>
</html>
