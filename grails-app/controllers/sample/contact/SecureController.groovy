package sample.contact

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource
import sample.contact.auth.ContactDataService

import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION
import static org.springframework.security.acls.domain.BasePermission.DELETE
import static org.springframework.security.acls.domain.BasePermission.READ

import org.springframework.dao.DataAccessException
import org.springframework.security.acls.domain.PermissionFactory
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.Sid
import org.springframework.security.core.Authentication

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.acl.AclUtilService
import grails.plugin.springsecurity.annotation.Secured

@CompileStatic
@Secured(['ROLE_USER'])
class SecureController {

	static allowedMethods = [index: 'GET',
							 create: 'GET',
							 add: 'POST',
							 del: 'POST',
							 addPermission: 'POST',
							 adminPermission: 'GET',
							 deletePermission: 'POST',
							 debug: 'GET',
	]

	private static final Permission[] HAS_DELETE = [DELETE, ADMINISTRATION]
	private static final Permission[] HAS_ADMIN = [ADMINISTRATION]

	PermissionFactory aclPermissionFactory
	AclUtilService aclUtilService
	ContactService contactService
	SpringSecurityService springSecurityService
	MessageSource messageSource
	ContactDataService contactDataService
	/**
	 * The index page for an authenticated user.
	 * <p>
	 * This controller displays a list of all the contacts for which the current user has read or
	 * admin permissions. It makes a call to {@link ContactService#findAll()} which
	 * automatically filters the returned list using Spring Security's ACL mechanism (see
	 * the expression annotations for the details).
	 * <p>
	 * In addition to rendering the list of contacts, the view will also include a "Del" or "Admin" link
	 * beside the contact, depending on whether the user has the corresponding permissions
	 * (admin permission is assumed to imply delete here). This information is stored in the model
	 * using the injected <code>aclUtilService</code> instance.
	 */
	def index() {
		List<Contact> myContactsList = contactService.findAll()
		Map<Contact, Boolean> hasDelete = [:]
		Map<Contact, Boolean> hasAdmin = [:]

		Authentication user = springSecurityService.authentication
		for (Contact contact in myContactsList) {
			hasDelete[contact] = aclUtilService.hasPermission(user, contact, HAS_DELETE)
			hasAdmin[contact] = aclUtilService.hasPermission(user, contact, HAS_ADMIN)
		}

		[contacts: myContactsList, hasDeletePermission: hasDelete, hasAdminPermission: hasAdmin]
	}


	def create() {
		[command: new SaveContactCommand()]
	}

	/**
	 * Displays the "add contact" form for GET and handles the submission of the contact form,
	 * creating a new instance if the username and email are valid.
	 */
	def add(SaveContactCommand cmd) {
		if (cmd.hasErrors()) {
			return [command: cmd]
		}

		contactService.create cmd.name, cmd.email

		redirect action: 'index'
	}

	def del(Long contactId) {
		Contact contact = contactService.findById(contactId)
		contactService.delete contact
		[contact: contact]
	}

	/**
	 * Displays the permission admin page for a particular contact.
	 */
	def adminPermission(Long contactId) {
		Contact contact = contactService.findById(contactId)
		[contact: contact, acl: aclUtilService.readAcl(contact)]
	}

	/**
	 * Displays the "add permission" page for a contact and
	 * handles submission of the "add permission" form.
	 */
	def addPermission(AddPermissionCommand addPermission) {

		if ( addPermission.hasErrors() ) {
			if ( !addPermission.contactId ) {
				flash.message = 'Contact id is required'
			}
			redirect action: 'index'
			return
		}

		Contact contact = contactService.findById(addPermission.contactId)

		def buildModel = { ->
			[command: addPermission, contact: contact,
			 recipients: contactService.allRecipients, permissions: listPermissions()]
		}

		if (request.get) {
			return buildModel()
		}

		if (request.post) {
			if (addPermission.hasErrors()) {
				return buildModel()
			}

			PrincipalSid sid = new PrincipalSid(addPermission.recipient)
			Permission permission = aclPermissionFactory.buildFromMask(addPermission.permission)

			try {
				contactService.addPermission contact, sid, permission
				redirect action: 'index'
			}
			catch (DataAccessException existingPermission) {
				log.error existingPermission.message, existingPermission
				addPermission.errors.rejectValue 'recipient', 'err.recipientExistsForContact'
				return buildModel()
			}
		}
	}

	/**
	 * Deletes a permission.
	 */
	def deletePermission(Long contactId, String sid, Integer permission) {

		Contact contact = contactService.findById(contactId)
		Sid sidObject = new PrincipalSid(sid)
		Permission p = aclPermissionFactory.buildFromMask(permission)

		contactService.deletePermission contact, sidObject, p

		[contact: contact, sid: sidObject, permission: p]
	}

	def debug() {
		[auth: springSecurityService.authentication]
	}

	private Map<Integer, String> listPermissions() {
		[
				(ADMINISTRATION.mask): messageSource.getMessage('select.administer', [] as Object[], 'Admnistration', request.locale),
		 		(READ.mask): messageSource.getMessage('select.read', [] as Object[], 'Admnistration', request.locale),
		 		(DELETE.mask): messageSource.getMessage('select.delete', [] as Object[], 'Admnistration', request.locale)
		] as Map<Integer, String>
	}
}