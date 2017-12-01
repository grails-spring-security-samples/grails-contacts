package sample.contact

import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic

@CompileStatic
@Secured(['ROLE_SUPERVISOR'])
class SuController {

	def exitUser() {}

	def switchUser() {}
}
