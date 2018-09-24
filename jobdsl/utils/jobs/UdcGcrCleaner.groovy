package jobdsl.utils.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcGcrCleaner {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            wrappers {
                preBuildCleanup()
                colorizeOutput()
            }
            triggers {
                cron(jobConfig.job.cron)
            }
            steps {
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
            }
        }
    }
}
