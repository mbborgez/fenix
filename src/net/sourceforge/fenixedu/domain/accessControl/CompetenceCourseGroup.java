package net.sourceforge.fenixedu.domain.accessControl;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.fenixedu.domain.CompetenceCourse;
import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.Enrolment;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.accessControl.groups.language.Argument;
import net.sourceforge.fenixedu.domain.accessControl.groups.language.GroupBuilder;
import net.sourceforge.fenixedu.domain.accessControl.groups.language.exceptions.GroupDynamicExpressionException;
import net.sourceforge.fenixedu.domain.accessControl.groups.language.operators.IdOperator;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.domain.student.Student;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This group represents the group of students associated to at least one degree
 * where a specific execution course is present
 * 
 * @author cmej
 */
public class CompetenceCourseGroup extends ExecutionCourseGroup {

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1L;

    public CompetenceCourseGroup(ExecutionCourse executionCourse) {
	super(executionCourse);
    }

    @Override
    public String getName() {
	return RenderUtils.getResourceString("SITE_RESOURCES",
		"label.net.sourceforge.fenixedu.domain.accessControl.CompetenceCourseGroup", new Object[] { getExecutionCourse()
			.getNome() });
    }

    @Override
    public Set<Person> getElements() {
	HashSet<Person> elements = new HashSet<Person>();
	if (hasExecutionCourse()) {
	    for (Person person : Person.readAllPersons()) {
		if (isMember(person)) {
		    elements.add(person);
		}
	    }
	}
	return elements;
    }

    @Override
    public boolean isMember(Person person) {
	if (person != null && person.hasStudent() && hasExecutionCourse()) {
	    final Student student = person.getStudent();
	    if (student.readAttendByExecutionCourse(getExecutionCourse()) != null) {
		return true;
	    }

	    final Set<CompetenceCourse> competenceCourses = getExecutionCourse().getCompetenceCourses();
	    for (final Registration registration : person.getStudent().getRegistrationsSet()) {
		for (final StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
		    for (Enrolment enrolment : studentCurricularPlan.getEnrolments()) {
			final CurricularCourse curricularCourse = enrolment.getCurricularCourse();
			if (curricularCourse.hasCompetenceCourse()) {
			    final CompetenceCourse competenceCourse = curricularCourse.getCompetenceCourse();
			    if (competenceCourses.contains(competenceCourse)) {
				return true;
			    }
			}
		    }
		}
	    }
	}
	return false;
    }

    @Override
    public boolean equals(Object object) {
	return object != null && object instanceof CompetenceCourseGroup;
    }

    @Override
    public int hashCode() {
	return this.getClass().hashCode();
    }

    @Override
    protected Argument[] getExpressionArguments() {
	return new Argument[] { new IdOperator(getObject()) };
    }

    public static class Builder implements GroupBuilder {

	public Group build(Object[] arguments) {
	    try {
		return new CompetenceCourseGroup((ExecutionCourse) arguments[0]);
	    } catch (ClassCastException e) {
		throw new GroupDynamicExpressionException("accessControl.group.builder.executionCourse.notExecutionCourse",
			arguments[0].toString());
	    }
	}

	public int getMinArguments() {
	    return 0;
	}

	public int getMaxArguments() {
	    return 1;
	}

    }

}