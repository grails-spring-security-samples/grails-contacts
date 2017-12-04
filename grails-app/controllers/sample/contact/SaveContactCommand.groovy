package sample.contact

import grails.validation.Validateable
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class SaveContactCommand implements Validateable {
    String name
    String email

    static constraints = {
        name  size: 3..50
        email size: 3..50, email: true
    }
}