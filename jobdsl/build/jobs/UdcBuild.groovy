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
            triggers {
                scm('*/5 * * * *')
            }
            steps {
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
                maven {
                    goals('clean deploy')
                    goals('-B')
                    goals('-C')
                    goals(' -Pimage')
                    goals('-Ddocker.registry.host=gcr.io')
                    mavenInstallation(jobConfig.tools.maven)
                }
            }
        }
    }
}
