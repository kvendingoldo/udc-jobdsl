package jobdsl.build.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcBuildCustom {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam('REF_SPEC', 'refs/heads/master', '')
            }
            wrappers {
              colorizeOutput()
              preBuildCleanup()
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        url(jobConfig.job.repository)
                    }
                    branch('${REF_SPEC}')
                    extensions {
                        wipeOutWorkspace()
                        submoduleOptions {
                            recursive()
                        }
                    }
                }
            }
            steps {
                shell('gcloud docker -a')
                shell('mvn versions:set -DnewVersion="0.0.0-$(date \"+%Y%m%d.%H%M%S\")-${BUILD_NUMBER}"')
                maven {
                    goals('clean deploy')
                    goals('-B')
                    goals('-C')
                    goals(' -Pimage')
                    goals('-Ddocker.registry.host=gcr.io')
                }
            }
        }
    }
}
