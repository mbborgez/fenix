/*
 * Created on 22/Set/2003
 */
package net.sourceforge.fenixedu.applicationTier.Servico.manager;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NonExistingServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoProfessorship;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Professorship;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentTeacher;

/**
 * @author lmac1
 */
public class InsertProfessorShip extends Service {
    public InsertProfessorShip() {
    }

    public void run(InfoProfessorship infoProfessorShip)
            throws FenixServiceException, ExcepcaoPersistencia {
        final ExecutionCourse executionCourse = (ExecutionCourse) persistentObject.readByOID(
                ExecutionCourse.class, infoProfessorShip.getInfoExecutionCourse().getIdInternal());
        if (executionCourse == null) {
            throw new NonExistingServiceException("message.nonExisting.executionCourse", null);
        }

        final IPersistentTeacher persistentTeacher = persistentSupport.getIPersistentTeacher();
        final Teacher teacher = persistentTeacher.readByNumber(infoProfessorShip.getInfoTeacher().getTeacherNumber());
        if (teacher == null) {
            throw new NonExistingServiceException("message.non.existing.teacher", null);
        }

        Professorship.create(infoProfessorShip.getResponsibleFor(), executionCourse, teacher, infoProfessorShip.getHours());
    }
}