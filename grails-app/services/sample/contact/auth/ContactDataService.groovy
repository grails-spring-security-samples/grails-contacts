package sample.contact.auth

import grails.gorm.services.Query
import grails.gorm.services.Service
import groovy.transform.CompileStatic
import sample.contact.Contact

@CompileStatic
@Service(Contact)
interface ContactDataService {

    Contact save(String name, String email)

    Contact findById(Serializable id)

    Contact update(Serializable id, String name, String email)

    void delete(Serializable id)

    @Query("from $Contact as contact order by id")
    List<Contact> findAllOrderById()

    List<Long> findContactId()
}