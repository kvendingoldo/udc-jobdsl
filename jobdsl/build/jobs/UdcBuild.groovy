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
              timestamps()
              preBuildCleanup()
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        github(jobConfig.job.repository, 'ssh')
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
                scm('*/1 * * * *')
            }
            steps {
                gitHubSetCommitStatusBuilder {
                  statusMessage {
                      content('pending')
                  }
                }
                shell('gcloud docker -a')
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
                envInjectBuilder {
                    propertiesFilePath('version.properties')
                    propertiesContent('')
                }
                dsl {
                    text(dslFactory.readFileFromWorkspace('jobdsl/build/groovy/BuildNameSetter.groovy'))
                }
                maven {
                    goals('clean deploy')
                    goals('-B')
                    goals('-C')
                    goals('-q')
                    goals(' -Pimage')
                    goals('-Ddocker.registry.host=gcr.io')
                }
            }
            publishers {
              downstreamParameterized {
                  trigger('../Orchestrator/UDC_Deploy_Orchestrator') {
                      parameters {
                          predefinedProp('VERSION', '${VERSION}')
                      }
                  }
              }
              gitHubCommitNotifier {
                  resultOnFailure('fail')
                  statusMessage {
                    content('ok')
                  }
              }
            }
        }
      }
}
