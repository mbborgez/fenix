/**
 * Nov 21, 2005
 */
package net.sourceforge.fenixedu.applicationTier.Servico.teacher.professorship;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Professorship;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;

/**
 * @author Ricardo Rodrigues
 * 
 */

public class ReadProfessorshipByTeacherIDAndExecutionCourseID extends Service {

    public Professorship run(final Integer teacherID, final Integer executionCourseID)
            throws ExcepcaoPersistencia {
        Teacher teacher = (Teacher) persistentObject.readByOID(Teacher.class,
                teacherID);
        ExecutionCourse executionCourse = (ExecutionCourse) persistentObject.readByOID(ExecutionCourse.class, executionCourseID);

        return teacher.getProfessorshipByExecutionCourse(executionCourse);
    }
}
