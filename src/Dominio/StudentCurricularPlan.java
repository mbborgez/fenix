/*
 * StudentCurricularPlan.java
 *
 * Created on 21 de December de 2002, 16:39
 */

package Dominio;

import java.util.Date;

import java.util.List;

import Util.StudentCurricularPlanState;

/**
 *
 * @author  Nuno Nunes & Joana Mota
 */

public class StudentCurricularPlan implements IStudentCurricularPlan {

  protected Integer internalCode;
  protected Integer studentKey;
  protected Integer degreeCurricularPlanKey;

  protected IStudent student;
  protected IDegreeCurricularPlan degreeCurricularPlan;
  protected Date startDate;
  protected StudentCurricularPlanState currentState;
  
  private List associatedBranches;
    
    
  /** Construtor sem argumentos p�blico requerido pela moldura de objectos OJB */
  public StudentCurricularPlan() { }
    
  public StudentCurricularPlan(IStudent student, IDegreeCurricularPlan courseCurricularPlan, 
  			Date startDate, StudentCurricularPlanState currentState) {

	this.student = student;
	this.degreeCurricularPlan = courseCurricularPlan;
	this.startDate = startDate;
	this.currentState = currentState;
  }
    

  public boolean equals(Object obj) {
    boolean resultado = false;
    if (obj instanceof IStudentCurricularPlan) {
      IStudentCurricularPlan studentCurricularPlan = (IStudentCurricularPlan)obj;
      resultado = 
      ((getStudent().getNumber() == studentCurricularPlan.getStudent().getNumber()) &&
       (getDegreeCurricularPlan().getDegree().getSigla().equals(studentCurricularPlan.getDegreeCurricularPlan().getDegree().getSigla())) &&
       (getCurrentState().getState().intValue() == studentCurricularPlan.getCurrentState().getState().intValue()));
    } 
    return resultado;
  }
  
  public String toString() {
    String result = "[STUDENT_CURRICULAR_PLAN";
    result += ", Internal Code=" + internalCode;
    result += ", Student=" + student;
    result += ", CourseCurricularPlan=" + degreeCurricularPlan;
    result += ", Start Date=" + startDate;
    result += ", State=" + currentState.toString();

    result += "]";
    return result;
  }
   
    
	/**
	 * Returns the degreeCurricularPlan.
	 * @return IDegreeCurricularPlan
	 */
	public IDegreeCurricularPlan getDegreeCurricularPlan() {
		return degreeCurricularPlan;
	}
	
	/**
	 * Returns the degreeCurricularPlanKey.
	 * @return Integer
	 */
	public Integer getDegreeCurricularPlanKey() {
		return degreeCurricularPlanKey;
	}
	
	/**
	 * Returns the currentState.
	 * @return StudentCurricularPlanState
	 */
	public StudentCurricularPlanState getCurrentState() {
		return currentState;
	}
	
	/**
	 * Returns the internalCode.
	 * @return Integer
	 */
	public Integer getInternalCode() {
		return internalCode;
	}
	
	/**
	 * Returns the startDate.
	 * @return Date
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Returns the student.
	 * @return IStudent
	 */
	public IStudent getStudent() {
		return student;
	}
	
	/**
	 * Returns the studentKey.
	 * @return Integer
	 */
	public Integer getStudentKey() {
		return studentKey;
	}
	
	/**
	 * Sets the degreeCurricularPlan.
	 * @param degreeCurricularPlan The degreeCurricularPlan to set
	 */
	public void setDegreeCurricularPlan(IDegreeCurricularPlan courseCurricularPlan) {
		this.degreeCurricularPlan = courseCurricularPlan;
	}
	
	/**
	 * Sets the degreeCurricularPlanKey.
	 * @param degreeCurricularPlanKey The degreeCurricularPlanKey to set
	 */
	public void setDegreeCurricularPlanKey(Integer courseCurricularPlanKey) {
		this.degreeCurricularPlanKey = courseCurricularPlanKey;
	}
	
	/**
	 * Sets the currentState.
	 * @param currentState The currentState to set
	 */
	public void setCurrentState(StudentCurricularPlanState currentState) {
		this.currentState = currentState;
	}
	
	/**
	 * Sets the internalCode.
	 * @param internalCode The internalCode to set
	 */
	public void setInternalCode(Integer internalCode) {
		this.internalCode = internalCode;
	}
	
	/**
	 * Sets the startDate.
	 * @param startDate The startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Sets the student.
	 * @param student The student to set
	 */
	public void setStudent(IStudent student) {
		this.student = student;
	}
	
	/**
	 * Sets the studentKey.
	 * @param studentKey The studentKey to set
	 */
	public void setStudentKey(Integer studentKey) {
		this.studentKey = studentKey;
	}

/**
 * @return List
 */
public List getAssociatedBranches() {
	return associatedBranches;
}

/**
 * Sets the associatedBranches.
 * @param associatedBranches The associatedBranches to set
 */
public void setAssociatedBranches(List associatedBranches) {
	this.associatedBranches = associatedBranches;
}

}
