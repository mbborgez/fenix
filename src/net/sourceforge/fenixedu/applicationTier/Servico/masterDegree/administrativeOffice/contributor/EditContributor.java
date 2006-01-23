/*
 * Created on 21/Mar/2003
 */
package net.sourceforge.fenixedu.applicationTier.Servico.masterDegree.administrativeOffice.contributor;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.ExistingServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NonExistingContributorServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoContributor;
import net.sourceforge.fenixedu.domain.Contributor;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;

/**
 * @author Nuno Nunes (nmsn@rnl.ist.utl.pt) Joana Mota (jccm@rnl.ist.utl.pt)
 */
public class EditContributor extends Service {

    public InfoContributor run(InfoContributor infoContributor, Integer contributorNumber,
            String contributorName, String contributorAddress) throws ExcepcaoPersistencia,
            NonExistingContributorServiceException, ExistingServiceException {

        final Contributor storedContributor = (Contributor) persistentObject.readByOID(
                Contributor.class, infoContributor.getIdInternal());

        if (storedContributor == null) {
            throw new NonExistingContributorServiceException();
        }

        // Read if exists any contributor with the new number
        Contributor contributor = persistentSupport.getIPersistentContributor().readByContributorNumber(
                contributorNumber);
        if (contributor != null && !contributor.equals(storedContributor)) {
            throw new ExistingServiceException();
        }

        storedContributor.setContributorNumber(contributorNumber);
        storedContributor.setContributorName(contributorName);
        storedContributor.setContributorAddress(contributorAddress);

        return InfoContributor.newInfoFromDomain(storedContributor);
    }

}
