package net.sourceforge.fenixedu.presentationTier.Action.scientificCouncil.candidacy.degreeCandidacyForGraduatedPerson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.DegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.ExecutionInterval;
import net.sourceforge.fenixedu.domain.candidacyProcess.CandidacyProcess;
import net.sourceforge.fenixedu.domain.candidacyProcess.IndividualCandidacyProcess;
import net.sourceforge.fenixedu.domain.candidacyProcess.graduatedPerson.DegreeCandidacyForGraduatedPersonIndividualProcess;
import net.sourceforge.fenixedu.domain.candidacyProcess.graduatedPerson.DegreeCandidacyForGraduatedPersonProcess;
import net.sourceforge.fenixedu.domain.period.DegreeCandidacyForGraduatedPersonCandidacyPeriod;
import net.sourceforge.fenixedu.presentationTier.Action.candidacy.CandidacyProcessDA;
import net.sourceforge.fenixedu.presentationTier.Action.candidacy.graduatedPerson.DegreeCandidacyForGraduatedPersonProcessDA.DegreeCandidacyForGraduatedPersonDegreeBean;
import net.sourceforge.fenixedu.presentationTier.Action.masterDegree.coordinator.CoordinatedDegreeInfo;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.SpreadsheetXLSExporter;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;
import pt.utl.ist.fenix.tools.util.i18n.Language;

@Mapping(path = "/caseHandlingDegreeCandidacyForGraduatedPersonProcess", module = "scientificCouncil", formBeanClass = CandidacyProcessDA.CandidacyProcessForm.class)
@Forwards( { @Forward(name = "intro", path = "/scientificCouncil/candidacy/mainCandidacyProcess.jsp") })
public class DegreeCandidacyForGraduatedPersonProcessDA extends CandidacyProcessDA {

    @Override
    protected Class getProcessType() {
	return DegreeCandidacyForGraduatedPersonProcess.class;
    }

    @Override
    protected Class getChildProcessType() {
	return DegreeCandidacyForGraduatedPersonIndividualProcess.class;
    }

    @Override
    protected Class getCandidacyPeriodType() {
	return DegreeCandidacyForGraduatedPersonCandidacyPeriod.class;
    }

    @Override
    protected DegreeCandidacyForGraduatedPersonProcess getProcess(HttpServletRequest request) {
	return (DegreeCandidacyForGraduatedPersonProcess) super.getProcess(request);
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	setChooseDegreeBean(request);
	return super.execute(mapping, actionForm, request, response);
    }

    private void setChooseDegreeBean(HttpServletRequest request) {
	ChooseDegreeBean chooseDegreeBean = (ChooseDegreeBean) getObjectFromViewState("choose.degree.bean");

	if (chooseDegreeBean == null) {
	    chooseDegreeBean = new ChooseDegreeBean();
	}

	request.setAttribute("chooseDegreeBean", chooseDegreeBean);
    }

    private ChooseDegreeBean getChooseDegreeBean(HttpServletRequest request) {
	return (ChooseDegreeBean) request.getAttribute("chooseDegreeBean");
    }

    @Override
    protected CandidacyProcess getCandidacyProcess(HttpServletRequest request, final ExecutionInterval executionInterval) {
	return executionInterval.hasDegreeCandidacyForGraduatedPersonCandidacyPeriod() ? executionInterval
		.getDegreeCandidacyForGraduatedPersonCandidacyPeriod().getDegreeCandidacyForGraduatedPersonProcess() : null;
    }

    @Override
    protected ActionForward introForward(final ActionMapping mapping) {
	return mapping.findForward("intro");
    }

    @Override
    protected void setStartInformation(ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) {
	if (!hasExecutionInterval(request)) {
	    final List<ExecutionInterval> executionIntervals = ExecutionInterval
		    .readExecutionIntervalsWithCandidacyPeriod(getCandidacyPeriodType());
	    if (executionIntervals.size() == 1) {
		setCandidacyProcessInformation(request, getCandidacyProcess(request, executionIntervals.get(0)));
	    } else {
		request.setAttribute("canCreateProcess", canCreateProcess(getProcessType().getName()));
		request.setAttribute("executionIntervals", executionIntervals);
	    }
	} else {
	    setCandidacyProcessInformation(request, getCandidacyProcess(request, getExecutionInterval(request)));
	}
    }

    public ActionForward prepareExecutePrintCandidacies(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {

	response.setContentType("application/vnd.ms-excel");
	response.setHeader("Content-disposition", "attachment; filename=" + getReportFilename());

	writeReport(getProcess(request), response.getOutputStream());
	response.getOutputStream().flush();
	response.flushBuffer();
	return null;
    }

    private void writeReport(final DegreeCandidacyForGraduatedPersonProcess process, final ServletOutputStream outputStream)
	    throws IOException {
	final List<Spreadsheet> spreadsheets = new ArrayList<Spreadsheet>();
	for (final Entry<Degree, SortedSet<DegreeCandidacyForGraduatedPersonIndividualProcess>> entry : process
		.getValidDegreeCandidaciesForGraduatedPersonsByDegree().entrySet()) {
	    spreadsheets.add(buildReport(entry.getKey(), entry.getValue()));
	}
	new SpreadsheetXLSExporter().exportToXLSSheets(outputStream, spreadsheets);
    }

    private Spreadsheet buildReport(final Degree degree,
	    final SortedSet<DegreeCandidacyForGraduatedPersonIndividualProcess> candidacyProcesses) {

	final Spreadsheet spreadsheet = new Spreadsheet(degree.getSigla(), getHeader());
	for (final DegreeCandidacyForGraduatedPersonIndividualProcess process : candidacyProcesses) {
	    final Row row = spreadsheet.addRow();
	    row.setCell(process.getPersonalDetails().getName());
	    row.setCell(process.getCandidacyPrecedentDegreeInformation().getDegreeAndInstitutionName());
	    row.setCell(process.getCandidacyAffinity());
	    row.setCell(process.getCandidacyDegreeNature());
	    row.setCell(process.getCandidacyPrecedentDegreeInformation().getConclusionGrade());
	    row.setCell(process.getCandidacyGrade());
	    if (process.isCandidacyAccepted() || process.isCandidacyRejected()) {
		row.setCell(ResourceBundle.getBundle("resources/EnumerationResources", Language.getLocale()).getString(
			process.getCandidacyState().getQualifiedName()));
	    } else {
		row.setCell("");
	    }
	}

	return spreadsheet;
    }

    private List<Object> getHeader() {
	final ResourceBundle bundle = ResourceBundle.getBundle("resources/ApplicationResources", Language.getLocale());
	final List<Object> result = new ArrayList<Object>();
	result.add(bundle.getString("label.name"));
	result.add(bundle.getString("label.candidacy.degree.and.school"));
	result.add(bundle.getString("label.candidacy.affinity"));
	result.add(bundle.getString("label.candidacy.degreeNature"));
	result.add(bundle.getString("label.candidacy.mfc"));
	result.add(bundle.getString("label.candidacy.grade"));
	result.add(bundle.getString("label.candidacy.result"));
	return result;
    }

    @Override
    protected List<CandidacyDegreeBean> createCandidacyDegreeBeans(HttpServletRequest request) {
	final DegreeCandidacyForGraduatedPersonProcess process = getProcess(request);
	final List<CandidacyDegreeBean> candidacyDegreeBeans = new ArrayList<CandidacyDegreeBean>();
	for (final DegreeCandidacyForGraduatedPersonIndividualProcess child : process
		.getAcceptedDegreeCandidacyForGraduatedPersonIndividualCandidacies()) {
	    candidacyDegreeBeans.add(new DegreeCandidacyForGraduatedPersonDegreeBean(child));
	}
	Collections.sort(candidacyDegreeBeans);
	return candidacyDegreeBeans;
    }

    protected Spreadsheet buildIndividualCandidacyReport(final Spreadsheet spreadsheet,
	    final IndividualCandidacyProcess individualCandidacyProcess) {
	DegreeCandidacyForGraduatedPersonIndividualProcess degreeCandidacyForGraduatedPersonProcess = (DegreeCandidacyForGraduatedPersonIndividualProcess) individualCandidacyProcess;
	ResourceBundle enumerationBundle = ResourceBundle.getBundle("resources/EnumerationResources", Language.getLocale());
	ResourceBundle candidateBundle = ResourceBundle.getBundle("resources/CandidateResources", Language.getLocale());

	final Row row = spreadsheet.addRow();
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getProcessCode());
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getPersonalDetails().getName());
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getPersonalDetails().getIdDocumentType().getLocalizedName());
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getPersonalDetails().getDocumentIdNumber());
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getPersonalDetails().getCountry().getCountryNationality()
		.getContent());
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getCandidacyPrecedentDegreeInformation()
		.getDegreeAndInstitutionName());
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getCandidacyPrecedentDegreeInformation().getDegreeDesignation());
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getCandidacyPrecedentDegreeInformation().getConclusionDate()
		.toString(dateFormat));
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getCandidacyPrecedentDegreeInformation().getConclusionGrade());
	row.setCell(degreeCandidacyForGraduatedPersonProcess.getCandidacy().getSelectedDegree().getName());
	row.setCell(enumerationBundle.getString(individualCandidacyProcess.getCandidacyState().getQualifiedName()));
	row.setCell(candidateBundle.getString(degreeCandidacyForGraduatedPersonProcess.getProcessChecked() != null
		&& degreeCandidacyForGraduatedPersonProcess.getProcessChecked() ? MESSAGE_YES : MESSAGE_NO));
	return spreadsheet;
    }

    @Override
    protected List<IndividualCandidacyProcess> getChildProcesses(final CandidacyProcess process, HttpServletRequest request) {
	List<IndividualCandidacyProcess> processes = process.getChildProcesses();
	List<IndividualCandidacyProcess> selectedDegreesIndividualCandidacyProcesses = new ArrayList<IndividualCandidacyProcess>();
	Degree selectedDegree = getChooseDegreeBean(request).getDegree();

	for (IndividualCandidacyProcess child : processes) {
	    if ((selectedDegree == null)
		    || ((DegreeCandidacyForGraduatedPersonIndividualProcess) child).getCandidacy().getSelectedDegree() == selectedDegree) {
		selectedDegreesIndividualCandidacyProcesses.add(child);
	    }
	}

	return selectedDegreesIndividualCandidacyProcesses;
    }

    public DegreeCurricularPlan getDegreeCurricularPlan(HttpServletRequest request) {
	final Integer degreeCurricularPlanOID = CoordinatedDegreeInfo.findDegreeCurricularPlanID(request);
	request.setAttribute("degreeCurricularPlanID", degreeCurricularPlanOID);

	if (degreeCurricularPlanOID != null) {
	    return rootDomainObject.readDegreeCurricularPlanByOID(degreeCurricularPlanOID);
	}

	return null;
    }

}