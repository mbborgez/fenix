package net.sourceforge.fenixedu.domain.accounting.paymentCodes;

import java.util.Set;

import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.accounting.Event;
import net.sourceforge.fenixedu.domain.accounting.PaymentCode;
import net.sourceforge.fenixedu.domain.accounting.PaymentCodeType;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.util.Money;

import org.joda.time.YearMonthDay;

public class IndividualCandidacyPaymentCode extends IndividualCandidacyPaymentCode_Base {

    protected IndividualCandidacyPaymentCode(final PaymentCodeType paymentCodeType, final YearMonthDay startDate,
	    final YearMonthDay endDate, final Money minAmount, final Money maxAmount) {
	super();
	init(paymentCodeType, startDate, endDate, minAmount, maxAmount);
    }

    public static IndividualCandidacyPaymentCode create(final PaymentCodeType paymentCodeType, final YearMonthDay startDate,
	    final YearMonthDay endDate, final Money minAmount, final Money maxAmount) {
	return PaymentCode.canGenerateNewCode(IndividualCandidacyPaymentCode.class, paymentCodeType, null) ? new IndividualCandidacyPaymentCode(
		paymentCodeType, startDate, endDate, minAmount, maxAmount)
		: findAndReuseExistingCode(paymentCodeType, startDate, endDate, minAmount, maxAmount);

    }

    public static IndividualCandidacyPaymentCode getAvailablePaymentCodeAndUse(final PaymentCodeType paymentCodeType,
	    final YearMonthDay date, Event event, Person person) {
	Set<IndividualCandidacyPaymentCode> individualCandidacyPaymentCodes = RootDomainObject
		.readAllDomainObjects(IndividualCandidacyPaymentCode.class);

	for (IndividualCandidacyPaymentCode paymentCode : individualCandidacyPaymentCodes) {
	    if (paymentCode.isAvailable(paymentCodeType, date)) {
		paymentCode.use(event, person);

		return paymentCode;
	    }
	}

	return null;
    }

    protected void init(final PaymentCodeType paymentCodeType, YearMonthDay startDate, YearMonthDay endDate, Money minAmount,
	    Money maxAmount) {
	super.init(paymentCodeType, startDate, endDate, minAmount, maxAmount, null);
    }

    protected Boolean isAvailable(final PaymentCodeType paymentCodeType, final YearMonthDay date) {
	return this.getType().equals(paymentCodeType) && !this.getStartDate().isAfter(date) && !this.getEndDate().isBefore(date)
		&& this.getPerson() == null;
    }

    protected static IndividualCandidacyPaymentCode findAndReuseExistingCode(final PaymentCodeType paymentCodeType,
	    final YearMonthDay startDate, final YearMonthDay endDate, final Money minAmount, final Money maxAmount) {

	IndividualCandidacyPaymentCode paymentCode = getAvailablePaymentCodeForReuse();
	paymentCode.reuse(startDate, endDate, minAmount, maxAmount, null);

	return paymentCode;
    }

    protected static IndividualCandidacyPaymentCode getAvailablePaymentCodeForReuse() {
	Set<IndividualCandidacyPaymentCode> individualCandidacyPaymentCodes = RootDomainObject
		.readAllDomainObjects(IndividualCandidacyPaymentCode.class);

	for (IndividualCandidacyPaymentCode paymentCode : individualCandidacyPaymentCodes) {
	    if (paymentCode.isAvailableForReuse())
		return paymentCode;
	}

	return null;
    }

    @Override
    protected void checkParameters(Event event, final Person person) {
	if (event == null) {
	    throw new DomainException("error.accounting.paymentCodes.IndividualCandidacyPaymentCode.event.cannot.be.null");
	}

	if (person == null) {
	    throw new DomainException("error.accounting.paymentCodes.IndividualCandidacyPaymentCode.person.cannot.be.null");
	}
    }

    protected void use(Event event, Person person) {
	checkParameters(event, person);

	this.setPerson(person);
	this.setAccountingEvent(event);
    }

    @Override
    public void setPerson(Person student) {
	if (this.getPerson() != null || !this.isNew())
	    throw new DomainException("error.net.sourceforge.fenixedu.domain.accounting.PaymentCode.cannot.modify.person");

	super._setPerson(student);
    }

    @Override
    public void setAccountingEvent(Event accountingEvent) {
	if (this.getAccountingEvent() != null || !this.isNew())
	    throw new DomainException("error.accounting.paymentCodes.AccountingEventPaymentCode.cannot.modify.accountingEvent");

	super._setAccountingEvent(accountingEvent);
    }

}