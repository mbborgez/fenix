/**
 * Nov 24, 2005
 */
package net.sourceforge.fenixedu.applicationTier.Servico.teacher.services;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.domain.ExecutionPeriod;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.domain.teacher.TeacherService;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;

/**
 * @author Ricardo Rodrigues
 * 
 */

public class ReadTeacherServiceByTeacherIDAndExecutionPeriodID extends Service {

    public TeacherService run(Integer teacherID, Integer executionPeriodID) throws ExcepcaoPersistencia {
        Teacher teacher = (Teacher) persistentObject.readByOID(Teacher.class,
                teacherID);
        ExecutionPeriod executionPeriod = (ExecutionPeriod) persistentObject.readByOID(ExecutionPeriod.class, executionPeriodID);

        return teacher.getTeacherServiceByExecutionPeriod(executionPeriod);
    }

}
