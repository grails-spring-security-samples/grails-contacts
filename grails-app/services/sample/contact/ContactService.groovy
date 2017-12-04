package sample.contact

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.acl.AclClass
import grails.plugin.springsecurity.acl.AclEntry
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.model.AccessControlEntry
import org.springframework.security.acls.model.MutableAcl
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.Sid
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclUtilService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import sample.contact.auth.ContactDataService
import sample.contact.auth.UserDataService

@CompileStatic
@Slf4j
class ContactService {

	AclService aclService
	AclUtilService aclUtilService
	SpringSecurityService springSecurityService
	ContactDataService contactDataService
	UserDataService userDataService

	@PreAuthorize('hasPermission(#contact, admin)')
	@Transactional
	void addPermission(Contact contact, Sid recipient, Permission permission) {
		aclUtilService.addPermission Contact, contact.id, recipient, permission
	}

	@Transactional
	protected void addPermission(Contact contact, Permission permission) {
		aclUtilService.addPermission Contact, contact.id, loggedUsername(), permission
	}

	protected String loggedUsername() {
		springSecurityService.authentication.name
	}

	@PreAuthorize('hasRole("ROLE_USER")')
	Contact create(String name, String email) {
		Contact contact = contactDataService.save(name, email)
		if ( contact.hasErrors() ) {
			log.error 'Error while saving contact'
			return contact
		}

		addPermission(contact, BasePermission.ADMINISTRATION)

		log.debug "Created contact $contact and granted admin permission to recipient ${loggedUsername()}"
		contact
	}

	@PreAuthorize('hasPermission(#contact, "delete") or hasPermission(#contact, admin)')
	@Transactional
	void delete(Contact contact) {
		Long id = contact.id
		contactDataService.delete(id)

		// Delete the ACL information as well
		aclUtilService.deleteAcl contact

		log.debug "Deleted contact $contact including ACL permissions"
	}

	@PreAuthorize('hasPermission(#contact, admin)')
	@Transactional
	void deletePermission(Contact contact, Sid recipient, Permission permission) {
		MutableAcl acl = (MutableAcl)aclUtilService.readAcl(contact)

		// Remove all permissions associated with this particular recipient (string equality to KISS)
		acl.entries.eachWithIndex { AccessControlEntry entry, int i ->
			if (entry.sid == recipient && entry.permission == permission) {
				acl.deleteAce i
			}
		}

		aclService.updateAcl acl

		log.debug "Deleted contact $contact ACL permissions for recipient $recipient"
	}

	@PreAuthorize('hasRole("ROLE_USER")')
	@PostFilter('hasPermission(filterObject, "read") or hasPermission(filterObject, admin)')
	@ReadOnly
	List<Contact> findAll() {
		log.debug 'Returning all contacts'
		List<Contact> contactList = contactDataService.findAllOrderById()
		contactList
	}

	@PreAuthorize('hasRole("ROLE_USER")')
	@ReadOnly
	List<String> getAllRecipients() {
		log.debug 'Returning all recipients'
		userDataService.findUserUsername()
	}

	@PreAuthorize('hasPermission(#id, "sample.contact.Contact", read) or hasPermission(#id, "sample.contact.Contact", admin)')
	@ReadOnly
	Contact findById(Long id) {
		log.debug "Returning contact with id: $id"
		contactDataService.findById(id)
	}

	@ReadOnly
	Contact findRandomContact() {
		log.debug 'Returning random contact'
		List<Long> ids = contactDataService.findContactId()
		Long id = ids[new Random().nextInt(ids.size())]
		contactDataService.findById(id)
	}

	@Transactional
	void update(Contact contact, String email, String name) {
		Serializable id = contact.id
		contactDataService.update(id, name, email)
		log.debug "Updated contact $contact"
	}
}
