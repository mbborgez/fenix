/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.ui.struts.action.scientificCouncil.thesis;

import java.io.Serializable;

import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.thesis.ThesisParticipationType;

public class TeacherThesisCreditsInfoBean implements Serializable {

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1L;

    private Student student;
    private String dissertationTitle;
    private ThesisParticipationType teacherParticipationType;
    private Integer creditsPercentage;

    public TeacherThesisCreditsInfoBean(Student student, String dissertationTitle,
            ThesisParticipationType teacherParticipationType, Integer creditsPercentage) {

        setStudent(student);
        setDissertationTitle(dissertationTitle);
        setTeacherParticipationType(teacherParticipationType);
        setCreditsPercentage(creditsPercentage);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getDissertationTitle() {
        return dissertationTitle;
    }

    public void setDissertationTitle(String dissertationTitle) {
        this.dissertationTitle = dissertationTitle;
    }

    public ThesisParticipationType getTeacherParticipationType() {
        return teacherParticipationType;
    }

    public void setTeacherParticipationType(ThesisParticipationType teacherParticipationType) {
        this.teacherParticipationType = teacherParticipationType;
    }

    public Integer getCreditsPercentage() {
        return creditsPercentage;
    }

    public void setCreditsPercentage(Integer creditsPercentage) {
        this.creditsPercentage = creditsPercentage;
    }

}
