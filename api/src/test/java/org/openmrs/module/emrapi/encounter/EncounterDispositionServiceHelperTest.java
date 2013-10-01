package org.openmrs.module.emrapi.encounter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterDispositionServiceHelperTest {


    public static final String TEXT_CONCEPT_UUID = "text-concept-uuid";
    public static final String NUMERIC_CONCEPT_UUID = "numeric-concept-uuid";
    @Mock
    private ConceptService conceptService;
    @Mock
    private ObsService obsService;

    private EncounterObservationServiceHelper encounterObservationServiceHelper;
    private String questionConceptUuid;
  //  private String answerConceptUuid;
    private String dispostionNoteConceptUuid;
    private String dispostionNoteValue;
    private EncounterDispositionServiceHelper encounterDispositionServiceHelper;
    private static final String UUID_SUFFIX ="-uuid-1234";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        encounterDispositionServiceHelper = new EncounterDispositionServiceHelper(conceptService);

    }

    @Test
    public void shouldSaveDispositionAsObsGroup() {
        String code = "ADMIT";
        String dispostionNoteValue = "disposition note text";
        String noteConceptUuid = "noteConceptUuid";
        Encounter encounter = new Encounter();
        encounter.setUuid("e-uuid");

        EncounterTransaction.Disposition disposition = new EncounterTransaction.Disposition();
        disposition.setCode(code).setAdditionalObs(Arrays.asList(new EncounterTransaction.Observation().setConceptUuid(noteConceptUuid).setValue(dispostionNoteValue)));

        encounterDispositionServiceHelper.update(encounter, disposition, new Date());

        Set<Obs> obsAtTopLevel = encounter.getObsAtTopLevel(false);
        assertEquals(1, obsAtTopLevel.size());
        Obs obs = obsAtTopLevel.iterator().next();
        assertTrue(obs.isObsGrouping());
        assertEquals(2, obs.getGroupMembers().size());

    }

//    @Test
//    public void shouldAddNewObservationWithDisposition() throws ParseException {
//        String dipositionAction = EmrApiConstants.DISPOSITION_ANSWER_DISCHARGE;
//
//        newConcept(ConceptDatatype.TEXT, TEXT_CONCEPT_UUID);
//        newConcept(ConceptDatatype.CODED, questionConceptUuid);
//        newConcept(ConceptDatatype.TEXT, dispostionNoteConceptUuid);
//        newConcept(ConceptDatatype.TEXT, dipositionAction+UUID_SUFFIX);
//
//        newConceptByName( EmrApiConstants.DISPOSITION_CONCEPT);
//        newConceptByName( EmrApiConstants.DISPOSITION_ANSWER_ADMIT);
//        newConceptByName( EmrApiConstants.DISPOSITION_ANSWER_DISCHARGE);
//        newConceptByName( EmrApiConstants.DISPOSITION_ANSWER_TRANSFER);
//        newConceptByName( EmrApiConstants.DISPOSITION_ANSWER_REFER);
//        newConceptByName( EmrApiConstants.DISPOSITION_NOTE_CONCEPT);
//
//
//
//        List<EncounterTransaction.Observation> observations = asList(
//                new EncounterTransaction.Observation().setConceptUuid(TEXT_CONCEPT_UUID).setValue("text value").setComment("overweight")
//        );
//
//        EncounterTransaction.Disposition disposition = new EncounterTransaction.Disposition().setCode(dipositionAction);
//        disposition.setDispositionNote(dispostionNoteValue);
//
//        Patient patient = new Patient();
//        Encounter encounter = new Encounter();
//        encounter.setUuid("e-uuid");
//        encounter.setPatient(patient);
//
//        Date observationDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse("2005-01-01T00:00:00.000+0000");
//
//
//        encounterDispositionServiceHelper.update(encounter, disposition, observationDateTime);
//
//        assertEquals(2, encounter.getObs().size());
//
//        Obs adtObservation = getObsByUuid(questionConceptUuid,encounter.getAllObs());
//        assertEquals(patient, adtObservation.getPerson());
//        assertEquals("e-uuid", adtObservation.getEncounter().getUuid());
//        assertEquals(observationDateTime, adtObservation.getObsDatetime());
//
//        assertEquals(dipositionAction+UUID_SUFFIX, adtObservation.getValueCoded().getUuid());
//        assertEquals(questionConceptUuid, adtObservation.getConcept().getUuid());
//
//        adtObservation = getObsByUuid(dispostionNoteConceptUuid,encounter.getAllObs());
//        assertEquals(dispostionNoteValue, adtObservation.getValueText());
//        assertEquals(dispostionNoteConceptUuid, adtObservation.getConcept().getUuid());
//
//    }



    private Obs getObsByUuid(String uuid,Set<Obs> obsSet){
        for(Iterator<Obs> obs = obsSet.iterator();obs.hasNext();){
            Obs observation = obs.next();
            if(observation.getConcept().getUuid().equals(uuid)) {
                return observation;
            }
        }
        return null;
    }

    private Concept newConcept(String hl7, String uuid) {
        Concept concept = new Concept();
        ConceptDatatype textDataType = new ConceptDatatype();
        textDataType.setHl7Abbreviation(hl7);
        concept.setDatatype(textDataType);
        concept.setUuid(uuid);
        when(conceptService.getConceptByUuid(uuid)).thenReturn(concept);
        return concept;
    }

    private Concept newConceptByName( String name) {
        Concept concept = new Concept();
        ConceptDatatype textDataType = new ConceptDatatype();
     //   textDataType.setHl7Abbreviation(hl7);
        concept.setDatatype(textDataType);
        concept.setUuid(name+UUID_SUFFIX);
        when(conceptService.getConcept(name)).thenReturn(concept);
        return concept;
    }

}
