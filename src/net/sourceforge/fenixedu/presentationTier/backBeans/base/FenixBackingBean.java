package net.sourceforge.fenixedu.presentationTier.backBeans.base;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.applicationTier.Filtro.exception.FenixFilterException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.domain.DomainObject;
import net.sourceforge.fenixedu.presentationTier.Action.sop.utils.ServiceUtils;
import net.sourceforge.fenixedu.presentationTier.Action.sop.utils.SessionConstants;
import net.sourceforge.fenixedu.presentationTier.jsf.components.UIViewState;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;

public class FenixBackingBean {

    protected IUserView userView;

    protected String errorMessage;

    private UIViewState viewState;

    public FenixBackingBean() {
        final FacesContext facesContext = FacesContext.getCurrentInstance();

        final HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

        final Locale locale = (Locale) session.getAttribute(Globals.LOCALE_KEY);
        facesContext.getViewRoot().setLocale(locale);
    }

    public IUserView getUserView() {
        return userView;
    }

    public void setUserView(IUserView userView) {
        this.userView = userView;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    protected HttpServletResponse getResponse() {
        return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
    }

    protected String getRequestParameter(String parameterName) {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get(parameterName);
    }

    protected Object getRequestAttribute(String attributeName) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(attributeName);
    }

    protected void setRequestAttribute(String attributeName, Object attributeValue) {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(attributeName,
                attributeValue);
    }

    public String getContextPath() {
        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
                .getContextPath();
    }

    public ResourceBundle getResourceBundle(String bundleName) {
        return ResourceBundle.getBundle(bundleName, FacesContext.getCurrentInstance().getViewRoot()
                .getLocale());
    }

    public ResourceBundle getResourceBundle(String bundleName, Locale locale) {
        return ResourceBundle.getBundle(bundleName, locale);
    }

    public UIViewState getViewState() {
        if (this.viewState == null) {
            this.viewState = new UIViewState();
        }

        return viewState;
    }

    public void setViewState(UIViewState viewState) {
        this.viewState = viewState;
    }

    private void addMessage(Severity facesMessage, String message) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(facesMessage, message, null));
    }

    protected void addErrorMessage(String message) {
        addMessage(FacesMessage.SEVERITY_ERROR, message);
    }

    protected void addInfoMessage(String message) {
        addMessage(FacesMessage.SEVERITY_INFO, message);
    }

    protected void addWarnMessage(String message) {
        addMessage(FacesMessage.SEVERITY_WARN, message);
    }

    public List readAllDomainObjects(final Class clazz) throws FenixFilterException,
            FenixServiceException {
        return (List) ServiceUtils
                .executeService(getUserView(), "ReadAllDomainObjects", new Object[] { clazz });
    }

    public DomainObject readDomainObject(final Class clazz, final Integer idInternal)
            throws FenixFilterException, FenixServiceException {
        return (DomainObject) ServiceUtils.executeService(getUserView(), "ReadDomainObject",
                new Object[] { clazz, idInternal });
    }

    protected Integer getAndHoldIntegerParameter(final String parameterName) {
        final String parameterString = getRequestParameter(parameterName);
        final Integer parameterValue;
        if (parameterString != null && parameterString.length() > 0) {
            parameterValue = Integer.valueOf(parameterString);
            setRequestAttribute(parameterName, parameterValue);
        } else {
            parameterValue = null;
        }
        return parameterValue;
    }

    protected String getAndHoldStringParameter(final String parameterName) {
        final String parameterString = getRequestParameter(parameterName);
        final String parameterValue;
        if (parameterString != null && parameterString.length() > 0) {
            parameterValue = parameterString;
            setRequestAttribute(parameterName, parameterValue);
        } else {
            parameterValue = null;
        }
        return parameterValue;
    }
    
    protected String getFormatedMessage(final String bundleName, final String key, final String ... args) {
    	final ResourceBundle bundle = getResourceBundle(bundleName);
        String message = bundle.getString(key);
    	return formatMessage(message, args);
    }

    protected String getFormatedMessage(final ResourceBundle bundle, final String key, final String ... args) {
        return formatMessage(bundle.getString(key), args);
    }
    
    private String formatMessage(String message, final String... args) {
        if(message != null && args != null) {
            for(int i = 0; args.length > i; i++) {
                String substring = "{" + i + "}";
                message = StringUtils.replace(message, substring, args[i]);
            }
        }
        return message;
    }
    
    protected void addErrorMessages(final ResourceBundle bundle, final String key, final String ... args) {
        String message = bundle.getString(key);
        if (message != null) {
            int indexOfManyArgs = message.indexOf("{...}");
            if (args != null && indexOfManyArgs != -1) {
                addErrorMessage(message.substring(0, indexOfManyArgs));
                for (final String arg : args) {
                    addErrorMessage(arg);
                }
            } else {
                addErrorMessage(formatMessage(message, args));
            }
        }
    }
    
    protected Double getAndHoldDoubleParameter(final String parameterName) {
        final String parameterString = getRequestParameter(parameterName);
        final Double parameterValue;
        if (parameterString != null && parameterString.length() > 0) {
            parameterValue = Double.valueOf(parameterString);
            setRequestAttribute(parameterName, parameterValue);
        } else {
            parameterValue = null;
        }
        return parameterValue;
    }

}
