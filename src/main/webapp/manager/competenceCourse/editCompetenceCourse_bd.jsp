<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html:xhtml/>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/enum" prefix="e" %>

<h2><bean:message bundle="MANAGER_RESOURCES" key="label.manager.edit.competenceCourse" /></h2>

<br/>

<span class="error"><!-- Error messages go here --><html:errors /></span>

<html:form action="/createEditCompetenceCourse">  
	<html:hidden bundle="HTMLALT_RESOURCES" altKey="hidden.method" property="method" value="edit"/>
	<html:hidden bundle="HTMLALT_RESOURCES" altKey="hidden.competenceCourseID" property="competenceCourseID"/>	
	<table>
		<tr>
			<td>
				<bean:message bundle="MANAGER_RESOURCES" key="message.manager.degree.curricular.plan.name"/>
			</td>
			<td>
				<html:text bundle="HTMLALT_RESOURCES" altKey="text.name" size="60" property="name" />
			</td>
		</tr>
		<tr>
			<td>
				<bean:message bundle="MANAGER_RESOURCES" key="label.manager.curricularCourse.code"/>
			</td>
			<td>
				<html:text bundle="HTMLALT_RESOURCES" altKey="text.code" size="12" property="code" />
			</td>
		</tr>
		<tr>
			<td>
				<bean:message bundle="MANAGER_RESOURCES" key="label.manager.department"/>
			</td>
			<td>
				<logic:iterate id="department" name="departments">
					<html:multibox bundle="HTMLALT_RESOURCES" altKey="multibox.departmentIDs" property="departmentIDs" >
						<bean:write name="department" property="externalId"/>
					</html:multibox>
					<bean:write name="department" property="name"/><br/>
				</logic:iterate>
			</td>
		</tr>
	</table>
	
	<br/>
	
	<html:submit bundle="HTMLALT_RESOURCES" altKey="submit.submit" styleClass="inputbutton">
		<bean:message bundle="MANAGER_RESOURCES" key="button.save"/>
	</html:submit>
	<html:reset bundle="HTMLALT_RESOURCES" altKey="reset.reset"  styleClass="inputbutton">
		<bean:message bundle="MANAGER_RESOURCES" key="label.clear"/>
	</html:reset>
</html:form>