package sample.contact

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION
import static org.springframework.security.acls.domain.BasePermission.DELETE
import static org.springframework.security.acls.domain.BasePermission.READ

@GrailsCompileStatic
class AddPermissionCommand implements Validateable {

    private static final List<Integer> ADD_PERMISSIONS = [ADMINISTRATION.mask, READ.mask, DELETE.mask]

    Long contactId
    Integer permission = READ.mask
    String recipient

    static constraints = {
        contactId nullable: false
        permission inList: ADD_PERMISSIONS
        recipient maxSize: 100
    }
}