package sample.contact

import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class HelloController {

	ContactService contactService

	/**
	 * The public index page, used for unauthenticated users.
	 */
	def index() {
		[contact: contactService.randomContact]
	}
}
