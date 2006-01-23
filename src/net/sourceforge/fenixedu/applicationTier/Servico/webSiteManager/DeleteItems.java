package net.sourceforge.fenixedu.applicationTier.Servico.webSiteManager;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.domain.WebSiteItem;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentWebSiteItem;

/**
 * @author Fernanda Quit�rio 26/09/2003
 * 
 */
public class DeleteItems extends Service {

	public boolean run(Integer sectionCode, List itemsToDelete) throws FenixServiceException, ExcepcaoPersistencia {
		IPersistentWebSiteItem persistentWebSiteItem = persistentSupport.getIPersistentWebSiteItem();

		Iterator iterItemsCode = itemsToDelete.iterator();
		while (iterItemsCode.hasNext()) {
			Integer itemCode = (Integer) iterItemsCode.next();

			WebSiteItem webSiteItemAux = (WebSiteItem) persistentObject.readByOID(
					WebSiteItem.class, itemCode);
			if (webSiteItemAux != null) {
				persistentWebSiteItem.delete(webSiteItemAux);
			}
		}
		return true;
	}
}