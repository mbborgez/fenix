package net.sourceforge.fenixedu.presentationTier.Action.publico.candidacies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu._development.PropertiesManager;
import net.sourceforge.fenixedu.applicationTier.Filtro.exception.FenixFilterException;
import net.sourceforge.fenixedu.applicationTier.Servico.caseHandling.CreateNewProcess;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.person.PersonBean;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.PublicCandidacyHashCode;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.candidacyProcess.CandidacyPrecedentDegreeInformationBean;
import net.sourceforge.fenixedu.domain.candidacyProcess.CandidacyProcess;
import net.sourceforge.fenixedu.domain.candidacyProcess.CandidacyProcessDocumentUploadBean;
import net.sourceforge.fenixedu.domain.candidacyProcess.DegreeOfficePublicCandidacyHashCode;
import net.sourceforge.fenixedu.domain.candidacyProcess.IndividualCandidacyDocumentFile;
import net.sourceforge.fenixedu.domain.candidacyProcess.IndividualCandidacyProcess;
import net.sourceforge.fenixedu.domain.candidacyProcess.IndividualCandidacyProcessBean;
import net.sourceforge.fenixedu.domain.candidacyProcess.IndividualCandidacyProcessWithPrecedentDegreeInformationBean;
import net.sourceforge.fenixedu.domain.candidacyProcess.IndividualCandidacyProcessWithPrecedentDegreeInformationBean.PrecedentDegreeType;
import net.sourceforge.fenixedu.domain.candidacyProcess.degreeChange.DegreeChangeIndividualCandidacyProcess.SendEmailForApplicationSubmission;
import net.sourceforge.fenixedu.domain.candidacyProcess.exceptions.HashCodeForEmailAndProcessAlreadyBounded;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.person.IDDocumentType;
import net.sourceforge.fenixedu.presentationTier.Action.candidacy.IndividualCandidacyProcessDA;
import net.sourceforge.fenixedu.presentationTier.servlets.filters.ContentInjectionRewriter;
import net.sourceforge.fenixedu.util.StringUtils;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;

import pt.utl.ist.fenix.tools.util.Pair;
import pt.utl.ist.fenix.tools.util.i18n.Language;

import com.octo.captcha.module.struts.CaptchaServicePlugin;
import com.octo.captcha.service.CaptchaServiceException;

public abstract class RefactoredIndividualCandidacyProcessPublicDA extends IndividualCandidacyProcessDA {

    private static final String SIBS_ENTITY_CODE = PropertiesManager.getProperty("sibs.entityCode");

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	ResourceBundle bundle = ResourceBundle.getBundle("resources.CandidateResources", Language.getLocale());
	request.setAttribute("application.name", bundle.getString(getCandidacyNameKey()));
	request.setAttribute("mappingPath", mapping.getPath());
	request.setAttribute("isApplicationSubmissionPeriodValid", isApplicationSubmissionPeriodValid());
	request.setAttribute("sibsEntityCode", SIBS_ENTITY_CODE);

	setProcess(request);
	return super.execute(mapping, actionForm, request, response);
    }

    @Override
    protected void prepareInformationForBindPersonToCandidacyOperation(HttpServletRequest request,
	    IndividualCandidacyProcess process) {
	// TODO Auto-generated method stub

    }

    protected ActionForward verifySubmissionPreconditions(ActionMapping mapping) {
	if (getCurrentOpenParentProcess() == null)
	    return mapping.findForward("open-candidacy-process-closed");

	return null;
    }

    protected ActionForward verifyEditPreconditions(ActionMapping mapping) {
	if (getCurrentOpenParentProcess() == null)
	    return mapping.findForward("open-candidacy-process-closed");

	return null;
    }

    public ActionForward beginCandidacyProcessIntro(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) {
	if (isInEnglishLocale()) {
	    return mapping.findForward("begin-candidacy-process-intro-en");
	}

	return mapping.findForward("begin-candidacy-process-intro");
    }

    public ActionForward preparePreCreationOfCandidacy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	ActionForward actionForwardError = verifySubmissionPreconditions(mapping);
	if (actionForwardError != null)
	    return actionForwardError;

	request.setAttribute("candidacyPreCreationBean", new CandidacyPreCreationBean());
	return mapping.findForward("show-pre-creation-candidacy-form");
    }

    public ActionForward preparePreCreationOfCandidacyInvalid(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return preparePreCreationOfCandidacy(mapping, form, request, response);
    }

    @SuppressWarnings("unchecked")
    protected CandidacyProcess getCurrentOpenParentProcess() {
	Set<CandidacyProcess> degreeChangeCandidacyProcesses = RootDomainObject.readAllDomainObjects(getParentProcessType());

	for (CandidacyProcess candidacyProcess : degreeChangeCandidacyProcesses) {
	    if (candidacyProcess.hasOpenCandidacyPeriod()) {
		return candidacyProcess;
	    }
	}

	return null;
    }

    public ActionForward bindEmailWithHashCodeAndSendMailWithLink(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) {
	ActionForward actionForwardError = verifySubmissionPreconditions(mapping);
	if (actionForwardError != null)
	    return actionForwardError;

	if (!validateCaptcha(mapping, request)) {
	    invalidateDocumentFileRelatedViewStates();
	    request.setAttribute(getIndividualCandidacyProcessBeanName(), getIndividualCandidacyProcessBean());
	    return mapping.findForward("show-pre-creation-candidacy-form");
	}

	try {
	    String email = (String) getObjectFromViewState("PublicAccessCandidacy.preCreationForm");
	    DegreeOfficePublicCandidacyHashCode.getUnusedOrCreateNewHashCodeAndSendEmailForApplicationSubmissionToCandidate(
		    getProcessType(), getCurrentOpenParentProcess(), email);

	    return mapping.findForward("show-email-message-sent");
	} catch (HashCodeForEmailAndProcessAlreadyBounded e) {
	    addActionMessage(request, "error.candidacy.hash.code.already.bounded");
	    return mapping.findForward("show-pre-creation-candidacy-form");
	}
    }

    public ActionForward showApplicationSubmissionConditions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	ActionForward actionForwardError = verifySubmissionPreconditions(mapping);
	if (actionForwardError != null)
	    return actionForwardError;

	String hash = request.getParameter("hash");
	DegreeOfficePublicCandidacyHashCode candidacyHashCode = (DegreeOfficePublicCandidacyHashCode) DegreeOfficePublicCandidacyHashCode
		.getPublicCandidacyCodeByHash(hash);

	if (candidacyHashCode == null) {
	    return mapping.findForward("open-candidacy-processes-not-found");
	}

	if (candidacyHashCode.getIndividualCandidacyProcess() != null) {
	    request.setAttribute("individualCandidacyProcess", candidacyHashCode.getIndividualCandidacyProcess());
	    return viewCandidacy(mapping, form, request, response);
	}

	request.setAttribute("hash", hash);
	return mapping.findForward("show-application-submission-conditions");
    }

    protected abstract String getCandidacyNameKey();

    public abstract ActionForward viewCandidacy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response);

    protected boolean validateCaptcha(ActionMapping mapping, HttpServletRequest request) {
	final String captchaId = request.getSession().getId();
	final String captchaResponse = request.getParameter("j_captcha_response");

	try {
	    if (!CaptchaServicePlugin.getInstance().getService().validateResponseForID(captchaId, captchaResponse)) {
		addActionMessage("captcha.error", request, "captcha.wrong.word");
		return false;
	    }
	    return true;
	} catch (CaptchaServiceException e) { // may be thrown if the id is not
	    // valid
	    e.printStackTrace();
	    addActionMessage("captcha.error", request, "captcha.wrong.word");
	    return false;
	}
    }

    private boolean isInEnglishLocale() {
	Locale locale = Language.getLocale();
	return locale.getLanguage().equals(Locale.ENGLISH.getLanguage());
    }

    public ActionForward continueCandidacyCreation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	IndividualCandidacyProcessBean bean = (IndividualCandidacyProcessBean) getIndividualCandidacyProcessBean();

	final PersonBean personBean = bean.getPersonBean();

	if (existsIndividualCandidacyProcessForDocumentId(request, personBean.getIdDocumentType(), personBean
		.getDocumentIdNumber())) {
	    addActionMessage("individualCandidacyMessages", request, "error.candidacy.for.person.already.exists");
	    return executeCreateCandidacyPersonalInformationInvalid(mapping, form, request, response);
	}

	final Person person = Person.readByDocumentIdNumberAndIdDocumentType(personBean.getDocumentIdNumber(), personBean
		.getIdDocumentType());

	// check if person already exists
	if (person != null) {
	    if (isPersonStudentOrEmployeeAndNumberIsCorrect(person, bean.getPersonNumber())) {
		if (!person.getDateOfBirthYearMonthDay().equals(personBean.getDateOfBirth())) {
		    // found person with diff date
		    addActionMessage("individualCandidacyMessages", request,
			    "error.public.candidacies.fill.personal.information.and.institution.id");
		    return executeCreateCandidacyPersonalInformationInvalid(mapping, form, request, response);
		} else if (!StringUtils.isEmpty(personBean.getSocialSecurityNumber())
			&& !person.getSocialSecurityNumber().equals(personBean.getSocialSecurityNumber())) {
		    // found person with diff social security number
		    addActionMessage("individualCandidacyMessages", request,
			    "error.public.candidacies.fill.personal.information.and.institution.id");
		    return executeCreateCandidacyPersonalInformationInvalid(mapping, form, request, response);
		} else {
		    personBean.setPerson(person);
		}
	    } else {
		// found person with diff ist userid
		addActionMessage("individualCandidacyMessages", request,
			"error.public.candidacies.fill.personal.information.and.institution.id");
		return executeCreateCandidacyPersonalInformationInvalid(mapping, form, request, response);
	    }
	} else {
	    if (Person.readByContributorNumber(personBean.getSocialSecurityNumber()) != null) {
		// found person with same contributor number
		addActionMessage("individualCandidacyMessages", request,
			"error.public.candidacies.fill.personal.information.and.institution.id");
		return executeCreateCandidacyPersonalInformationInvalid(mapping, form, request, response);
	    }

	    if (!StringUtils.isEmpty(bean.getPersonNumber())) {
		// person must fill ist userid
		addActionMessage("individualCandidacyMessages", request,
			"error.public.candidacies.fill.personal.information.and.institution.id");
		return executeCreateCandidacyPersonalInformationInvalid(mapping, form, request, response);
	    } else {
		fillExternalPrecedentInformation(mapping, form, request, response);
	    }
	}

	IndividualCandidacyDocumentFile photoDocumentFile = createIndividualCandidacyDocumentFile(bean.getPhotoDocument(), bean
		.getPersonBean().getDocumentIdNumber());
	bean.getPhotoDocument().setDocumentFile(photoDocumentFile);
	request.setAttribute(getIndividualCandidacyProcessBeanName(), getIndividualCandidacyProcessBean());

	return mapping.findForward("candidacy-continue-creation");
    }

    private boolean isPersonStudentOrEmployeeAndNumberIsCorrect(Person person, String personNumber) {
	return (person.hasStudent() && person.getStudent().getNumber().toString().equals(personNumber))
		|| (person.hasEmployee() && person.getEmployee().getEmployeeNumber().toString().equals(personNumber));
    }

    public ActionForward executeCreateCandidacyPersonalInformationInvalid(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {
	request.setAttribute(getIndividualCandidacyProcessBeanName(), getIndividualCandidacyProcessBean());
	invalidateDocumentFileRelatedViewStates();

	return mapping.findForward("show-candidacy-creation-page");
    }

    protected boolean candidacyIndividualProcessExistsForThisEmail(String email) {
	return DegreeOfficePublicCandidacyHashCode.getPublicCandidacyHashCodeByEmailAndCandidacyProcessType(email,
		getProcessType(), getCurrentOpenParentProcess()) != null;
    }

    protected IndividualCandidacyProcess createNewPublicProcess(IndividualCandidacyProcessBean bean) throws DomainException,
	    FenixFilterException, FenixServiceException {
	return (IndividualCandidacyProcess) CreateNewProcess.run(getProcessType(), bean,
		buildActivitiesForApplicationSubmission(bean.getPublicCandidacyHashCode()));
    }

    private List<Pair<Class<?>, Object>> buildActivitiesForApplicationSubmission(DegreeOfficePublicCandidacyHashCode hashCode) {
	final List<Pair<Class<?>, Object>> result = new ArrayList<Pair<Class<?>, Object>>();

	result.add(pair(SendEmailForApplicationSubmission.class, hashCode));

	return result;
    }

    private Pair<Class<?>, Object> pair(final Class<?> class1, final Object object) {
	return new Pair<Class<?>, Object>(class1, object);
    }

    public ActionForward fillInternalPrecedentInformation(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {
	final IndividualCandidacyProcessWithPrecedentDegreeInformationBean bean = (IndividualCandidacyProcessWithPrecedentDegreeInformationBean) getIndividualCandidacyProcessBean();

	StudentCurricularPlan studentCurricularPlan = bean.getLastPrecedentStudentCurricularPlan();

	if (studentCurricularPlan == null) {
	    addActionMessage("candidacyMessages", request, "error.public.candidacies.message.no.student.curricular.plan", null);
	} else {
	    if (studentCurricularPlan.getRegistration().isTransited()) {
		addActionMessage("candidacyMessages", request, "error.public.candidacies.message.no.student.curricular.plan",
			null);
	    } else {
		bean.setPrecedentDegreeType(PrecedentDegreeType.INSTITUTION_DEGREE);
		bean.setPrecedentStudentCurricularPlan(studentCurricularPlan);
		createCandidacyPrecedentDegreeInformation(bean, bean.getPrecedentStudentCurricularPlan());
	    }
	}

	// super.fillPrecedentInformationPostback(mapping, actionForm, request,
	// response);

	request.setAttribute(getIndividualCandidacyProcessBeanName(), bean);
	return mapping.findForward("candidacy-continue-creation");
    }

    public ActionForward fillExternalPrecedentInformation(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {
	final IndividualCandidacyProcessWithPrecedentDegreeInformationBean bean = (IndividualCandidacyProcessWithPrecedentDegreeInformationBean) getIndividualCandidacyProcessBean();
	bean.setPrecedentDegreeType(PrecedentDegreeType.EXTERNAL_DEGREE);
	bean.setPrecedentDegreeInformation(new CandidacyPrecedentDegreeInformationBean());

	request.setAttribute(getIndividualCandidacyProcessBeanName(), bean);
	return mapping.findForward("candidacy-continue-creation");
    }

    public ActionForward prepareEditCandidacyProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	request.setAttribute(getIndividualCandidacyProcessBeanName(), getIndividualCandidacyProcessBean());
	return mapping.findForward("edit-candidacy");
    }

    @Override
    protected void setStartInformation(ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	// TODO Auto-generated method stub

    }

    protected String getFormattedApplicationSubmissionEndDate() {
	DateTime end = getCurrentOpenParentProcess().getCandidacyEnd();
	if (isInEnglishLocale()) {
	    return end.toString("dd', 'MMMM' of 'yyyy", Language.getLocale());
	}
	return end.toString("dd' de 'MMMM' de 'yyyy", Language.getLocale());
    }

    @Override
    protected boolean existsIndividualCandidacyProcessForDocumentId(HttpServletRequest request, IDDocumentType documentType,
	    String identification) {
	return getCurrentOpenParentProcess().getOpenChildProcessByDocumentId(documentType, identification) != null;
    }

    protected boolean isApplicationSubmissionPeriodValid() {
	CandidacyProcess process = getCurrentOpenParentProcess();

	if (process == null)
	    return false;

	DateTime now = new DateTime(System.currentTimeMillis());
	return now.isAfter(process.getCandidacyStart()) && now.isBefore(process.getCandidacyEnd());
    }

    public ActionForward backToViewCandidacyInternal(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	IndividualCandidacyProcess individualCandidacyProcess = (IndividualCandidacyProcess) request
		.getAttribute("individualCandidacyProcess");
	return forward(request, mapping.getPath() + ".do?method=prepareCandidacyCreation&hash="
		+ individualCandidacyProcess.getCandidacyHashCode().getValue());
    }

    public ActionForward prepareEditCandidacyQualifications(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	request.setAttribute(getIndividualCandidacyProcessBeanName(), getIndividualCandidacyProcessBean());
	return mapping.findForward("edit-candidacy-habilitations");
    }

    private ActionForward forward(HttpServletRequest request, String windowLocation) {
	final ActionForward actionForward = new ActionForward();
	String contextContextPath = request.getParameter(ContentInjectionRewriter.CONTEXT_ATTRIBUTE_NAME);
	windowLocation = windowLocation + "&" + ContentInjectionRewriter.CONTEXT_ATTRIBUTE_NAME + "=" + contextContextPath;
	actionForward.setName(windowLocation);
	actionForward.setPath(windowLocation);
	actionForward.setRedirect(true);
	return actionForward;
    }

    public static class CandidacyPreCreationBean implements java.io.Serializable {
	/**
         * 
         */
	private static final long serialVersionUID = 1L;

	private String email;

	public String getEmail() {
	    return this.email;
	}

	public void setEmail(String email) {
	    this.email = email;
	}
    }

    public ActionForward backToViewCandidacy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	IndividualCandidacyProcess individualCandidacyProcess = getDomainObject(request, "individualCandidacyProcess");
	return forward(request, getLinkFromPublicCandidacyHashCodeForInternalUse(mapping, request, individualCandidacyProcess
		.getCandidacyHashCode()));
    }

    protected String getLinkFromPublicCandidacyHashCodeForInternalUse(ActionMapping mapping, HttpServletRequest request,
	    PublicCandidacyHashCode hashCode) {
	return mapping.getPath() + ".do?method=prepareCandidacyCreation&hash=" + hashCode.getValue();
    }

    public ActionForward prepareEditCandidacyDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	CandidacyProcessDocumentUploadBean bean = new CandidacyProcessDocumentUploadBean();
	bean.setIndividualCandidacyProcess(getIndividualCandidacyProcessBean().getIndividualCandidacyProcess());
	request.setAttribute("candidacyDocumentUploadBean", bean);
	return mapping.findForward("edit-candidacy-documents");
    }

    public ActionForward editCandidacyDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws FenixServiceException, FenixFilterException, IOException {
	CandidacyProcessDocumentUploadBean uploadBean = (CandidacyProcessDocumentUploadBean) getObjectFromViewState("individualCandidacyProcessBean.document.file");
	try {
	    IndividualCandidacyDocumentFile documentFile = createIndividualCandidacyDocumentFile(uploadBean, uploadBean
		    .getIndividualCandidacyProcess().getPersonalDetails().getDocumentIdNumber());
	    uploadBean.setDocumentFile(documentFile);

	    executeActivity(uploadBean.getIndividualCandidacyProcess(), "EditPublicCandidacyDocumentFile", uploadBean);
	    request.setAttribute("individualCandidacyProcess", uploadBean.getIndividualCandidacyProcess());
	    return backToViewCandidacyInternal(mapping, form, request, response);
	} catch (final DomainException e) {
	    invalidateDocumentFileRelatedViewStates();
	    CandidacyProcessDocumentUploadBean bean = new CandidacyProcessDocumentUploadBean();
	    bean.setIndividualCandidacyProcess(uploadBean.getIndividualCandidacyProcess());
	    request.setAttribute("candidacyDocumentUploadBean", bean);

	    addActionMessage(request, e.getMessage(), e.getArgs());
	    request.setAttribute(getIndividualCandidacyProcessBeanName(), getIndividualCandidacyProcessBean());
	    return mapping.findForward("edit-candidacy-documents");
	}
    }

}