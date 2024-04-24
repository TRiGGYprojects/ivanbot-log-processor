package app.eelman.ivanbotlogprocessor.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pavlov")
class PavlovConfiguration(val logPath: String)
