package net.sourceforge.fenixedu.applicationTier.Servico.degreeAdministrativeOffice;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.domain.CurricularCourseEquivalence;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;

public class DeleteCurricularCourseEquivalency extends Service {

    public void run(final Integer curricularCourseEquivalencyID) throws ExcepcaoPersistencia {
        final CurricularCourseEquivalence curricularCourseEquivalence = (CurricularCourseEquivalence) persistentObject.readByOID(CurricularCourseEquivalence.class, curricularCourseEquivalencyID);
        curricularCourseEquivalence.delete();
    }

}
