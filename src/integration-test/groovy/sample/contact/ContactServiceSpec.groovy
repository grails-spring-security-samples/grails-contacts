package sample.contact

import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
@Rollback
class ContactServiceSpec extends Specification {

	private static final Map<String, String> USERNAMES_TO_PASSWORDS = [
			  rod: 'koala', dianne: 'emu', scott: 'wombat', peter: 'opal']

	private @Autowired ContactService contactService

	def cleanup() {
		SecurityContextHolder.clearContext()
	}

	def 'dianne'() {
		setup:
		makeActiveUser 'dianne' // has ROLE_USER

		when:
		List<Contact> contacts = contactService.findAll()

		then:
		4 == contacts.size()

		assertContainsContact 4, contacts
		assertContainsContact 5, contacts
		assertContainsContact 6, contacts
		assertContainsContact 8, contacts

		assertDoestNotContainContact 1, contacts
		assertDoestNotContainContact 2, contacts
		assertDoestNotContainContact 3, contacts
	}

	def 'rod'() {
		setup:
		makeActiveUser 'rod' // has ROLE_SUPERVISOR

		when:
		List<Contact> contacts = contactService.findAll()

		then:
		4 == contacts.size()

		assertContainsContact 1, contacts
		assertContainsContact 2, contacts
		assertContainsContact 3, contacts
		assertContainsContact 4, contacts

		assertDoestNotContainContact 5, contacts

		Contact c1 = contactService.findById(4)
		contactService.deletePermission c1, new PrincipalSid('bob'), ADMINISTRATION
		contactService.addPermission    c1, new PrincipalSid('bob'), ADMINISTRATION
	}

	def 'scott'() {
		setup:
		makeActiveUser 'scott' // has ROLE_USER

		when:
		List<Contact> contacts = contactService.findAll();

		then:
		5 == contacts.size()

		assertContainsContact 4, contacts
		assertContainsContact 6, contacts
		assertContainsContact 7, contacts
		assertContainsContact 8, contacts
		assertContainsContact 9, contacts

		assertDoestNotContainContact 1, contacts
	}

	private void assertContainsContact(long id, List<Contact> contacts) {

		if (contacts.any { Contact contact -> contact.id == id }) {
			return
		}

		assert false, "List of contacts should have contained: $id"
	}

	private void assertDoestNotContainContact(long id, List<Contact> contacts) {
		if (contacts.any { Contact contact -> contact.id == id }) {
			assert false, "List of contact should NOT (but did) contain: $id"
		}
	}

	/**
	 * Locates the first <code>Contact</code> of the exact name specified.
	 * <p>
	 * Uses the {@link ContactService#findAll()} method.
	 *
	 * @param id Identify of the contact to locate (must be an exact match)
	 *
	 * @return the domain or <code>null</code> if not found
	 */
	private Contact getContact(String id) {
		contactService.findAll().find { it.id == id }
	}

	private void makeActiveUser(String username) {
		SecurityContextHolder.context.authentication = new UsernamePasswordAuthenticationToken(
				  username, USERNAMES_TO_PASSWORDS[username] ?: '')
	}
}
