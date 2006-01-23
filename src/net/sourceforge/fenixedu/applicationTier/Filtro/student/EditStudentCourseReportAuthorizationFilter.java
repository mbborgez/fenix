/*
 * Created on Feb 18, 2004
 *  
 */
package net.sourceforge.fenixedu.applicationTier.Filtro.student;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.applicationTier.Filtro.framework.DomainObjectAuthorizationFilter;
import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.CurricularCourseScope;
import net.sourceforge.fenixedu.domain.Student;
import net.sourceforge.fenixedu.domain.gesdis.StudentCourseReport;
import net.sourceforge.fenixedu.domain.person.RoleType;
import net.sourceforge.fenixedu.domain.student.Delegate;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentStudent;
import net.sourceforge.fenixedu.persistenceTier.student.IPersistentDelegate;
import net.sourceforge.fenixedu.util.DelegateYearType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

/**
 * @author <a href="mailto:lesa@mega.ist.utl.pt">Leonor Almeida </a>
 * @author <a href="mailto:shmc@mega.ist.utl.pt">Sergio Montelobo </a>
 *  
 */
public class EditStudentCourseReportAuthorizationFilter extends DomainObjectAuthorizationFilter {

    /*
     * (non-Javadoc)
     * 
     * @see ServidorAplicacao.Filtro.framework.DomainObjectAuthorizationFilter#getRoleType()
     */
    protected RoleType getRoleType() {
        return RoleType.DELEGATE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ServidorAplicacao.Filtro.framework.DomainObjectAuthorizationFilter#verifyCondition(ServidorAplicacao.IUserView,
     *      java.lang.Integer)
     */
    protected boolean verifyCondition(IUserView id, Integer objectId) {
        try {
            IPersistentStudent persistentStudent = persistentSupport.getIPersistentStudent();
            IPersistentDelegate persistentDelegate = persistentSupport.getIPersistentDelegate();

            Student student = persistentStudent.readByUsername(id.getUtilizador());
            Delegate delegate = persistentDelegate.readByStudent(student);
            StudentCourseReport studentCourseReport = (StudentCourseReport) persistentObject
                    .readByOID(StudentCourseReport.class, objectId);
            CurricularCourse curricularCourse = studentCourseReport.getCurricularCourse();
            List scopes = curricularCourse.getScopes();
            List years = (List) CollectionUtils.collect(scopes, new Transformer() {
                public Object transform(Object arg0) {
                    CurricularCourseScope curricularCourseScope = (CurricularCourseScope) arg0;
                    return curricularCourseScope.getCurricularSemester().getCurricularYear().getYear();
                }
            });
            years = removeDuplicates(years);
            Iterator iter = years.iterator();
            while (iter.hasNext()) {
                Integer year = (Integer) iter.next();
                List delegates = persistentDelegate.readByDegreeAndExecutionYearAndYearType(
                        curricularCourse.getDegreeCurricularPlan().getDegree(), delegate.getExecutionYear(),
                        DelegateYearType.getEnum(year.intValue()));

                if (delegates.contains(delegate))
                    return true;
            }
            return false;
        } catch (ExcepcaoPersistencia e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param years
     * @return
     */
    private List removeDuplicates(List years) {
        List result = new ArrayList();
        Iterator iter = years.iterator();
        while (iter.hasNext()) {
            Integer year = (Integer) iter.next();
            if (!result.contains(year))
                result.add(year);
        }
        return result;
    }
}