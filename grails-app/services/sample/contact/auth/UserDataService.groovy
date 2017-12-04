package sample.contact.auth

import grails.gorm.services.Service
import groovy.transform.CompileStatic

@CompileStatic
@Service(User)
interface UserDataService {

    User save(String username, String password, boolean enabled)

    List<String> findUserUsername()
}