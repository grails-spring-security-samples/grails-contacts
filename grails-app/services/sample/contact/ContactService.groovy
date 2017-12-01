package sample.contact

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import grails.gorm.transactions.Transactional
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.model.AccessControlEntry
import org.springframework.security.acls.model.MutableAcl
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.Sid

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclUtilService
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import sample.contact.auth.User

@GrailsCompileStatic
@Slf4j
class ContactService implements InitializingBean {

	private @Autowired AclService aclService
	private @Autowired AclUtilService aclUtilService
	private @Autowired SpringSecurityService springSecurityService

	@PreAuthorize('hasPermission(#contact, admin)')
	@Transactional
	void addPermission(Contact contact, Sid recipient, Permission permission) {
		aclUtilService.addPermission Contact, contact.id, recipient, permission
	}

	@PreAuthorize('hasRole("ROLE_USER")')
	@Transactional
	void create(String email, String name) {
		Contact contact = new Contact(name, email).save(failOnError: true)

		String username = springSecurityService.authentication.name
		// Grant the current principal administrative permission to the contact
		addPermission contact, new PrincipalSid(username), BasePermission.ADMINISTRATION

		log.debug "Created contact $contact and granted admin permission to recipient $username"
	}

	@PreAuthorize('hasPermission(#contact, "delete") or hasPermission(#contact, admin)')
	@Transactional
	void delete(Contact contact) {
		contact.delete()

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
	@Transactional(readOnly = true)
	List<Contact> getAll() {
		log.debug 'Returning all contacts'
		Contact.listOrderById()
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	@PreAuthorize('hasRole("ROLE_USER")')
	@Transactional(readOnly = true)
	List<String> getAllRecipients() {
		log.debug 'Returning all recipients'
		User.createCriteria().list {
			projections {
				property 'username'
			}
			order 'username', 'asc'
		}
	}

	@PreAuthorize('hasPermission(#id, "sample.contact.Contact", read) or hasPermission(#id, "sample.contact.Contact", admin)')
	@Transactional(readOnly = true)
	Contact getById(Long id) {
		log.debug "Returning contact with id: $id"
		Contact.get id
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	@Transactional(readOnly = true)
	Contact getRandomContact() {
		log.debug 'Returning random contact'
		List<Long> ids = Contact.createCriteria().list {
			projections {
				property 'id'
			}
		}

		Contact.get ids[new Random().nextInt(ids.size())]
	}

	@Transactional
	void update(Contact contact, String email, String name) {
		contact.email = email
		contact.name = name
		log.debug "Updated contact $contact"
	}

	void afterPropertiesSet() {
		assert aclService, 'aclService required'
	}
}
