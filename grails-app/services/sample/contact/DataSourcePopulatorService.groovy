package sample.contact

import grails.gorm.transactions.Transactional
import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION
import static org.springframework.security.acls.domain.BasePermission.DELETE
import static org.springframework.security.acls.domain.BasePermission.READ
import static org.springframework.security.acls.domain.BasePermission.WRITE
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.acls.model.ObjectIdentityGenerator
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder as SCH

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclUtilService
import sample.contact.auth.Role
import sample.contact.auth.User
import sample.contact.auth.UserRole

@GrailsCompileStatic
@Transactional
class DataSourcePopulatorService {

	private static final String[] firstNames = [
		'Bob', 'Mary', 'James', 'Jane', 'Kristy', 'Kirsty', 'Kate', 'Jeni', 'Angela', 'Melanie', 'Kent',
		'William', 'Geoff', 'Jeff', 'Adrian', 'Amanda', 'Lisa', 'Elizabeth', 'Prue', 'Richard', 'Darin',
		'Phillip', 'Michael', 'Belinda', 'Samantha', 'Brian', 'Greg', 'Matthew'
	]

	private static final String[] lastNames = [
		'Smith', 'Williams', 'Jackson', 'Rictor', 'Nelson', 'Fitzgerald', 'McAlpine', 'Sutherland', 'Abbott',
		'Hall', 'Edwards', 'Gates', 'Black', 'Brown', 'Gray', 'Marwell', 'Booch', 'Johnson', 'McTaggart',
		'Parklin', 'Findlay', 'Robinson', 'Giugni', 'Lang', 'Chi', 'Carmichael'
	]

	private static final int createEntities = 50

	private @Autowired AclService aclService
	private @Autowired AclUtilService aclUtilService
	private @Autowired ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy

	void populate() {

		// Set a user account that will initially own all the created data
		SCH.context.authentication = new UsernamePasswordAuthenticationToken('rod', 'koala',
				AuthorityUtils.createAuthorityList('ROLE_IGNORED'))

		Role userRole = new Role('ROLE_USER').save()
		Role supervisorRole = new Role('ROLE_SUPERVISOR').save()

		createUser 'rod',    'koala',  true,  userRole, supervisorRole
		createUser 'dianne', 'emu',    true,  userRole
		createUser 'scott',  'wombat', true,  userRole
		createUser 'peter',  'opal',   false, userRole
		createUser 'bill',   'wombat', true,  userRole
		createUser 'bob',    'wombat', true,  userRole
		createUser 'jane',   'wombat', true,  userRole

		def contacts = [['John Smith',       'john@somewhere.com'],
		                ['Michael Citizen',  'michael@xyz.com'],
		                ['Joe Bloggs',       'joe@demo.com'],
		                ['Karen Sutherland', 'karen@sutherland.com'],
		                ['Mitchell Howard',  'mitchell@abcdef.com'],
		                ['Rose Costas',      'rose@xyz.com'],
		                ['Amanda Smith',     'amanda@abcdef.com'],
		                ['Cindy Smith',      'cindy@smith.com'],
		                ['Jonathan Citizen', 'jonathan@xyz.com']
		].collect { nameAndEmail -> new Contact((String)nameAndEmail[0], (String)nameAndEmail[1]).save() }

		Random random = new Random()

		for (int i = 10; i < createEntities; i++) {
			String firstName = firstNames[random.nextInt(firstNames.size())]
			String lastName = lastNames[random.nextInt(lastNames.size())]
			contacts << new Contact("$firstName $lastName", "${firstName}@${lastName.toLowerCase()}.com").save()
		}

		// Create acl_object_identity rows (and also acl_class rows as needed
		ObjectIdentityGenerator oig = (ObjectIdentityGenerator)objectIdentityRetrievalStrategy
		for (long i = 1; i < createEntities; i++) {
			aclService.createAcl oig.createObjectIdentity(i, Contact.name)
		}

		// Now grant some permissions
		aclUtilService.addPermission contacts[0], 'rod',    ADMINISTRATION
		aclUtilService.addPermission contacts[1], 'rod',    READ
		aclUtilService.addPermission contacts[2], 'rod',    READ
		aclUtilService.addPermission contacts[2], 'rod',    WRITE
		aclUtilService.addPermission contacts[2], 'rod',    DELETE
		aclUtilService.addPermission contacts[3], 'rod',    ADMINISTRATION
		aclUtilService.addPermission contacts[3], 'dianne', ADMINISTRATION
		aclUtilService.addPermission contacts[3], 'scott',  READ
		aclUtilService.addPermission contacts[4], 'dianne', ADMINISTRATION
		aclUtilService.addPermission contacts[4], 'dianne', READ
		aclUtilService.addPermission contacts[5], 'dianne', READ
		aclUtilService.addPermission contacts[5], 'dianne', WRITE
		aclUtilService.addPermission contacts[5], 'dianne', DELETE
		aclUtilService.addPermission contacts[5], 'scott',  READ
		aclUtilService.addPermission contacts[6], 'scott',  ADMINISTRATION
		aclUtilService.addPermission contacts[7], 'dianne', ADMINISTRATION
		aclUtilService.addPermission contacts[7], 'dianne', READ
		aclUtilService.addPermission contacts[7], 'scott',  READ
		aclUtilService.addPermission contacts[8], 'scott',  ADMINISTRATION
		aclUtilService.addPermission contacts[8], 'scott',  READ
		aclUtilService.addPermission contacts[8], 'scott',  WRITE
		aclUtilService.addPermission contacts[8], 'scott',  DELETE

		// Now expressly change the owner of the first ten contacts
		// We have to do this last, because 'rod' owns all of them (doing it sooner would prevent ACL updates)
		// Note that ownership has no impact on permissions - they're separate (ownership only allows ACl editing)
		aclUtilService.changeOwner contacts[4], 'dianne'
		aclUtilService.changeOwner contacts[5], 'dianne'
		aclUtilService.changeOwner contacts[6], 'scott'
		aclUtilService.changeOwner contacts[7], 'dianne'
		aclUtilService.changeOwner contacts[8], 'scott'

		def users = ['bill', 'bob', 'jane'] // don't want to mess around with consistent sample data
		def permissions = [ADMINISTRATION, READ, DELETE]

		for (int i = 10; i < createEntities; i++) {
			aclUtilService.addPermission contacts[i - 1], users[random.nextInt(users.size())],
					permissions[random.nextInt(permissions.size())]

			aclUtilService.addPermission contacts[i - 1], users[random.nextInt(users.size())],
					permissions[random.nextInt(permissions.size())]
		}

		// logout
		SCH.clearContext()
	}

	private void createUser(String username, String password, boolean enabled, Role... roles) {
		def user = new User(username: username, enabled: enabled, password: password).save()
		for (role in roles) {
			UserRole.create user, role
		}
	}
}
