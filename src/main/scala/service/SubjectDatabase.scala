package service

import model.Subject

class SubjectDatabase extends Database[Subject]{

    class Initializer{
        //TODO
    }

    def this(initializer: SubjectDatabase#Initializer) = this

    def newSubject(): Long = addElement(new Subject)
}
