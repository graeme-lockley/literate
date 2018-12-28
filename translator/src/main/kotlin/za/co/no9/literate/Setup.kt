package za.co.no9.literate

import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.File


fun configure(templateDir: File): Configuration {
    val cfg =
            Configuration(Configuration.VERSION_2_3_28)

    cfg.setDirectoryForTemplateLoading(templateDir)
    cfg.defaultEncoding = "UTF-8"
    cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
    cfg.logTemplateExceptions = false
    cfg.wrapUncheckedExceptions = true

    return cfg
}