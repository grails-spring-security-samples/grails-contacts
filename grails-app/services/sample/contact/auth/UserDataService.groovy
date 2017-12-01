package sample.contact.auth

import grails.gorm.services.Service

@Service(User)
interface UserDataService {
    User save(String username, String password, boolean enabled)
}