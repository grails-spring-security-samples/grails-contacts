package sample.contact.auth

import grails.gorm.services.Service

@Service(Role)
interface RoleDataService {
    Role save(String authority)
}