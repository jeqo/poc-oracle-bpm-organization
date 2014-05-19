package com.acme.bpm.client.boundary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.bpel.services.bpm.common.IBPMContext;
import oracle.bpel.services.workflow.client.IWorkflowServiceClientConstants;
import oracle.bpel.services.workflow.client.WorkflowServiceClientFactory;

import oracle.bpm.client.BPMServiceClientFactory;
import oracle.bpm.services.client.IBPMServiceClient;
import oracle.bpm.services.organization.IBPMOrganizationService;
import oracle.bpm.services.organization.model.MemberType;
import oracle.bpm.services.organization.model.Participant;
import oracle.bpm.services.organization.model.ParticipantProperties;
import oracle.bpm.services.organization.model.ParticipantProperty;
import oracle.bpm.services.organization.model.ParticipantTypeEnum;
import oracle.bpm.services.organization.model.PrincipleRefType;

public class OrganizationHelper {
    private BPMServiceClientFactory clientFactory;
    private IBPMContext bpmContext;

    public OrganizationHelper() {
        super();
    }

    private void beginConnection() throws Exception {
        // Change username/password depending on your environment
        String username = "weblogic";
        char[] password = "welcome1".toCharArray();

        // also host/port of admin server
        String host = "devbam01";
        String port = "8051";
        String ejbHost = "t3://" + host + ":" + port;

        Map<IWorkflowServiceClientConstants.CONNECTION_PROPERTY, String> properties =
            new HashMap<IWorkflowServiceClientConstants.CONNECTION_PROPERTY, String>();

        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.CLIENT_TYPE,
                       WorkflowServiceClientFactory.REMOTE_CLIENT);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_PROVIDER_URL,
                       ejbHost);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_INITIAL_CONTEXT_FACTORY,
                       "weblogic.jndi.WLInitialContextFactory");

        clientFactory =
                BPMServiceClientFactory.getInstance(properties, null, null);
        bpmContext =
                clientFactory.getBPMUserAuthenticationService().authenticate(username,
                                                                             password,
                                                                             null);
    }

    public void createUserExtendedProperty(String propertyName,
                                           PropertyType propertyType) {
        try {
            beginConnection();

            IBPMServiceClient bpmServiceClient =
                clientFactory.getBPMServiceClient();

            IBPMOrganizationService bpmOrganizationService =
                bpmServiceClient.getBPMOrganizationService();

            ParticipantProperty participantProperty =
                new ParticipantProperty();

            participantProperty.setName(propertyName);
            participantProperty.setPropertyType(propertyType.name());

            bpmOrganizationService.createParticipantProperty(bpmContext,
                                                             participantProperty);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public List<ParticipantProperties> getParticipantsProperties(List<String> participantsNames) {

        List<ParticipantProperties> participantsProperties =
            new ArrayList<ParticipantProperties>();

        try {
            beginConnection();

            IBPMServiceClient bpmServiceClient =
                clientFactory.getBPMServiceClient();

            IBPMOrganizationService bpmOrganizationService =
                bpmServiceClient.getBPMOrganizationService();

            List<Participant> participantes = new ArrayList<Participant>();

            for (String participantName : participantsNames) {
                PrincipleRefType principleRef = new MemberType();
                principleRef.setName(participantName);
                principleRef.setRealm("jazn.com");
                principleRef.setType(ParticipantTypeEnum.USER);

                Participant participante = new Participant(principleRef);

                participantes.add(participante);
            }

            participantsProperties =
                    bpmOrganizationService.getPropertiesOfParticipants(bpmContext,
                                                                       participantes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return participantsProperties;
    }

    private void closeConnection() {
        if (clientFactory != null && bpmContext != null) {
            try {
                clientFactory.getBPMUserAuthenticationService().destroyBPMContext(bpmContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public enum PropertyType {
        FREEFORM_STRING,
        DATE,
        NUMBER;

        @SuppressWarnings("compatibility:-1920236344794051628")
        private static final long serialVersionUID = 1L;
    }
}
