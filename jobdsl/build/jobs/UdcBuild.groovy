package jobdsl.build.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcBuild {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam('REF_SPEC', 'refs/heads/master', '')
            }
            environmentVariables {
              //env('BUILD_VERSION', '0.0.0-${BUILD_TIMESTAMP}-${BUILD_NUMBER}')
            }
            wrappers {
              buildName('${BUILD_VERSION}')
              colorizeOutput()
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        url(jobConfig.job.repository)
                    }
                    branch('${REF_SPEC}')
                }
            }
            steps {
                //shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
                shell("mvn clean install")
            }
        }
    }
}
