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
            triggers {
                scm('*/5 * * * *')
            }
            steps {
                maven {
                    goals('clean install')
                    goals(' -B')
                    goals(' -Pimage')
                    mavenInstallation(jobConfig.tools.maven)
                }
                //shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
            }
        }
    }
}
