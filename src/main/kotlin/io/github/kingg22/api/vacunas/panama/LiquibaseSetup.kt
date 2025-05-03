package io.github.kingg22.api.vacunas.panama

import io.github.kingg22.api.vacunas.panama.util.logger
import io.quarkus.liquibase.runtime.NativeImageResourceAccessor
import io.quarkus.runtime.ImageMode
import io.quarkus.runtime.Startup
import io.quarkus.runtime.util.StringUtil
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseConnection
import liquibase.database.DatabaseFactory
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.CompositeResourceAccessor
import liquibase.resource.DirectoryResourceAccessor
import liquibase.resource.ResourceAccessor
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.FileNotFoundException
import java.nio.file.Paths
import java.util.Optional
import kotlin.jvm.optionals.getOrElse

/* Copy of https://github.com/quarkusio/quarkus/issues/14682#issuecomment-1205682175 */
@ApplicationScoped
@Startup
class LiquibaseSetup {
    @ConfigProperty(name = "quarkus.liquibase.enabled")
    private lateinit var active: Optional<Boolean>

    @ConfigProperty(name = "quarkus.liquibase.contexts")
    private lateinit var contexts: Optional<List<String>>

    @ConfigProperty(name = "quarkus.liquibase.labels")
    private lateinit var labels: Optional<List<String>>

    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    private lateinit var datasourceUrl: List<String>

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    private lateinit var datasourceUrlJdbc: Optional<String>

    @ConfigProperty(name = "quarkus.datasource.username")
    private lateinit var datasourceUsername: String

    @ConfigProperty(name = "quarkus.datasource.password")
    private lateinit var datasourcePassword: String

    @ConfigProperty(name = "quarkus.liquibase.change-log")
    private lateinit var changeLogLocation: String

    @ConfigProperty(name = "quarkus.liquibase.search-path")
    private lateinit var searchPath: Optional<List<String>>

    private val filesystemPrefix = "filesystem:"

    @PostConstruct
    fun init() {
        launchMigrationInBackground()
    }

    private fun launchMigrationInBackground() {
        if (active.getOrElse { false }) {
            Thread {
                runLiquibaseMigration()
            }.start()
        }
    }

    private fun runLiquibaseMigration() {
        val logger = logger()
        var liquibase: Liquibase? = null
        try {
            val contexts = createContexts()
            val labels = createLabels()
            logger.info("liquibase setup to $datasourceUrl")
            logger.info("Liquibase with contexts [$contexts] and labels [$labels]")
            val resourceAccessor: ResourceAccessor = resolveResourceAccessor()
            check(datasourceUrl.isNotEmpty() || datasourceUrlJdbc.isPresent) {
                "Datasource url must not be empty for Liquibase to run. Found: $datasourceUrl"
            }
            val uniqueDatasource = if (datasourceUrl.size > 1) {
                datasourceUrl.first {
                    it.contains("vertx-reactive") || it.contains("\\d*".toRegex())
                }
            } else if (datasourceUrl.size == 1) {
                datasourceUrl.first()
            } else {
                datasourceUrlJdbc.get()
            }
            var fixedUrl = uniqueDatasource.replace("vertx-reactive", "jdbc")
            if (!fixedUrl.startsWith("jdbc:")) {
                fixedUrl = "jdbc:$fixedUrl"
                logger.warn("Fixed datasource url to $fixedUrl")
            }
            logger.info("Using datasource url: $fixedUrl")
            val conn: DatabaseConnection = DatabaseFactory.getInstance().openConnection(
                fixedUrl,
                datasourceUsername,
                datasourcePassword,
                null,
                resourceAccessor,
            )
            liquibase = Liquibase(parseChangeLog(changeLogLocation), resourceAccessor, conn)
            liquibase.update(contexts, labels)
        } catch (e: Exception) {
            logger.error("Liquibase Migration Exception: ", e)
        } finally {
            liquibase?.close()
        }
    }

    /** Copy of [io.quarkus.liquibase.LiquibaseFactory] */
    @Throws(FileNotFoundException::class)
    private fun resolveResourceAccessor(): ResourceAccessor {
        val rootAccessor = CompositeResourceAccessor()
        return if (ImageMode.current().isNativeImage) {
            nativeImageResourceAccessor(rootAccessor)
        } else {
            defaultResourceAccessor(rootAccessor)
        }
    }

    @Throws(FileNotFoundException::class)
    private fun defaultResourceAccessor(rootAccessor: CompositeResourceAccessor): ResourceAccessor {
        rootAccessor.addResourceAccessor(
            ClassLoaderResourceAccessor(Thread.currentThread().getContextClassLoader()),
        )

        if (!changeLogLocation.startsWith(filesystemPrefix) && searchPath.isEmpty) {
            return rootAccessor
        }

        if (searchPath.isEmpty) {
            return rootAccessor.addResourceAccessor(
                DirectoryResourceAccessor(
                    Paths.get(
                        StringUtil
                            .changePrefix(changeLogLocation, filesystemPrefix, ""),
                    ).parent,
                ),
            )
        }

        for (searchPath in searchPath.get()) {
            rootAccessor.addResourceAccessor(DirectoryResourceAccessor(Paths.get(searchPath)))
        }
        return rootAccessor
    }

    private fun nativeImageResourceAccessor(rootAccessor: CompositeResourceAccessor): ResourceAccessor =
        rootAccessor.addResourceAccessor(NativeImageResourceAccessor())

    private fun parseChangeLog(changeLog: String): String {
        if (changeLog.startsWith(filesystemPrefix) && searchPath.isEmpty) {
            return Paths.get(StringUtil.changePrefix(changeLog, filesystemPrefix, ""))
                .fileName.toString()
        }

        if (changeLog.startsWith(filesystemPrefix)) {
            return StringUtil.changePrefix(changeLog, filesystemPrefix, "")
        }

        if (changeLog.startsWith("classpath:")) {
            return StringUtil.changePrefix(changeLog, "classpath:", "")
        }

        return changeLog
    }

    private fun createContexts() = Contexts(contexts.getOrElse { emptyList() })

    private fun createLabels() = LabelExpression(labels.getOrElse { emptyList() })
}
