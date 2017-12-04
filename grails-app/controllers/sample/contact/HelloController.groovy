package sample.contact

import groovy.transform.CompileStatic
import grails.plugin.springsecurity.annotation.Secured

@CompileStatic
@Secured(['permitAll'])
class HelloController {

	ContactService contactService

	/**
	 * The public index page, used for unauthenticated users.
	 */
	def index() {
		[contact: contactService.findRandomContact()]
	}
}
