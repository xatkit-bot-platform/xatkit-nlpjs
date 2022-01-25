package com.xatkit.core.recognition;

import com.xatkit.AbstractXatkitTest;
import com.xatkit.core.EventDefinitionRegistry;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.recognition.nlpjs.NlpjsIntentRecognitionProvider;
import com.xatkit.core.recognition.nlpjs.NlpjsIntentRecognitionProviderTest;
import com.xatkit.core.recognition.processor.SpacePunctuationPreProcessor;
import com.xatkit.core.recognition.processor.TrimParameterValuesPostProcessor;
import com.xatkit.core.recognition.processor.TrimPunctuationPostProcessor;
import com.xatkit.core.server.XatkitServer;
import org.apache.commons.configuration2.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntentRecognitionProviderFactoryTest extends AbstractXatkitTest {

    private IntentRecognitionProvider provider;

    private XatkitBot xatkitBot;

    @Before
    public void setUp() {
        xatkitBot = mock(XatkitBot.class);
        when(xatkitBot.getEventDefinitionRegistry()).thenReturn(new EventDefinitionRegistry());
        when(xatkitBot.getXatkitServer()).thenReturn(mock(XatkitServer.class));
    }

    @After
    public void tearDown() {
        if(nonNull(provider) && !provider.isShutdown()) {
            try {
                provider.shutdown();
            } catch(IntentRecognitionProviderException e) {
                /*
                 * Nothing to do, the provider will be re-created anyways.
                 */
            }
        }
    }

    @Test
    public void getIntentRecognitionProviderDialogFlowProperties() {
        /*
         * Use DialogFlowIntentRecognitionProviderTest.buildConfiguration to get a valid configuration (with a valid
         * path to a credentials file)
         */
        provider = IntentRecognitionProviderFactory.getIntentRecognitionProvider(xatkitBot,
                NlpjsIntentRecognitionProviderTest.buildConfiguration());
        assertThat(provider).as("Not null IntentRecognitionProvider").isNotNull();
        assertThat(provider).as("IntentRecognitionProvider is a DialogFlowIntentRecognitionProvider").isInstanceOf(NlpjsIntentRecognitionProvider.class);
        assertThat(provider.getRecognitionMonitor()).as("Recognition monitor is not null").isNull();
        assertThat(provider.getPreProcessors()).hasSize(1);
        /*
         * NLP.js provider uses SpacePunctuationPreProcessor by default to improve the quality of intent recognition.
         * It also uses TrimPunctuationPostProcessor and TrimParameterValuesPostProcessor for post processing.
         */
        assertThat(provider.getPreProcessors().get(0)).isInstanceOf(SpacePunctuationPreProcessor.class);
        assertThat(provider.getPostProcessors()).hasSize(2);
        assertThat(provider.getPostProcessors()).anyMatch(p -> p instanceof TrimPunctuationPostProcessor);
        assertThat(provider.getPostProcessors()).anyMatch(p -> p instanceof TrimParameterValuesPostProcessor);
    }

    @Test
    public void getIntentRecognitionProviderDialogFlowPropertiesDisabledAnalytics() {
        Configuration configuration = NlpjsIntentRecognitionProviderTest.buildConfiguration();
        configuration.addProperty(IntentRecognitionProviderFactoryConfiguration.ENABLE_RECOGNITION_ANALYTICS, false);
        provider = IntentRecognitionProviderFactory.getIntentRecognitionProvider(xatkitBot, configuration);
        assertThat(provider).as("Not null IntentRecognitionProvider").isNotNull();
        assertThat(provider).as("IntentRecognitionProvider is a DialogFlowIntentRecognitionProvider").isInstanceOf(NlpjsIntentRecognitionProvider.class);
        assertThat(provider.getRecognitionMonitor()).as("Recognition monitor is null").isNull();
        assertThat(provider.getPreProcessors()).as("PreProcessor list is empty").hasSize(1);
        /*
         * NLP.js provider uses SpacePunctuationPreProcessor by default to improve the quality of intent recognition.
         * It also uses TrimPunctuationPostProcessor and TrimParameterValuesPostProcessor for post processing.
         */
        assertThat(provider.getPreProcessors().get(0)).isInstanceOf(SpacePunctuationPreProcessor.class);
        assertThat(provider.getPostProcessors()).hasSize(2);
        assertThat(provider.getPostProcessors()).anyMatch(p -> p instanceof TrimPunctuationPostProcessor);
        assertThat(provider.getPostProcessors()).anyMatch(p -> p instanceof TrimParameterValuesPostProcessor);
    }
}
