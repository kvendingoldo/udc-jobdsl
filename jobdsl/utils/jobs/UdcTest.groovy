package jobdsl.utils.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcTest {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            wrappers {
                preBuildCleanup()
                colorizeOutput()
            }
            environmentVariables {
                env('AUTHORS', jobConfig.job.authors)
                overrideBuildParameters(true)
            }
        }
    }
}
