package net.neoturbine.auction.sniper

import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SuiteDisplayName("End To End Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("net/neoturbine/auction/sniper")
class RunCucumberTest
