package sample.contact

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Contact implements Serializable {

	private static final long serialVersionUID = 1

	String email
	String name

	@Override
	String toString() {
		"Id: $id; Name: $name; Email: $email"
	}

	static constraints = {
		email size: 3..50, email: true
		name  size: 3..50
	}
}
