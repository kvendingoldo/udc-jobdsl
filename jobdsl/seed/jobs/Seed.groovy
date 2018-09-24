package jobdsl.seed.jobs

import com.kvendingoldo.jdcl.core.Functions

class Seed {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam('BRANCH', jobConfig.job.branch, '')
            }
            wrappers {
                preBuildCleanup()
                colorizeOutput()
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        url(jobConfig.job.repository)
                    }
                    branch('refs/heads/${BRANCH}')
                }
            }
            triggers {
                githubPush()
                scm('*/1 * * * *')
            }
            steps {
                dsl {
                    external(jobConfig.job.dsl.external)
                    removeAction('DELETE')
                    removeViewAction('DELETE')
                    additionalClasspath(jobConfig.job.dsl.classpath)
                }
            }
        }
    }
}
