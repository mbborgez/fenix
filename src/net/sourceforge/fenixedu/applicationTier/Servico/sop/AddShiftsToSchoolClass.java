package net.sourceforge.fenixedu.applicationTier.Servico.sop;

import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.InvalidArgumentsServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoClass;
import net.sourceforge.fenixedu.domain.SchoolClass;
import net.sourceforge.fenixedu.domain.Shift;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;

public class AddShiftsToSchoolClass extends Service {

	public void run(InfoClass infoClass, List<String> shiftOIDs) throws FenixServiceException,
			ExcepcaoPersistencia {
		final SchoolClass schoolClass = (SchoolClass) persistentObject.readByOID(
				SchoolClass.class, infoClass.getIdInternal());
		if (schoolClass == null) {
			throw new InvalidArgumentsServiceException();
		}

		for (final String shiftOID : shiftOIDs) {
			final Shift shift = (Shift) persistentObject.readByOID(Shift.class, new Integer(shiftOID));
			if (shift == null) {
				throw new InvalidArgumentsServiceException();
			}
			
			schoolClass.associateShift(shift);
		}
	}

}
