package br.com.fiap.apitests;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Ponto de entrada do Maven/JUnit: executa todos os arquivos .feature de src/test/resources/features.
 * Relatórios gerados em target/: cucumber-report.html, cucumber.json e cucumber.xml (JUnit).
 * Para rodar só uma parte use tags, ex.: mvn test -Dcucumber.filter.tags="@voluntarios-ms"
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.fiap.apitests")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-report.html, json:target/cucumber.json, junit:target/cucumber.xml")
public class RunCucumberTest {
}
