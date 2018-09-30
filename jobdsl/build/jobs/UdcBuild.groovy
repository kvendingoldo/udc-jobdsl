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
                scm('*/1 * * * *')
            }
            steps {
                shell('gcloud docker -a')
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
                envInjectBuilder {
                    propertiesFilePath('version.properties')
                    propertiesContent('')
                }
                buildNameUpdater {
                    fromFile(false)
                    buildName('')
                    fromMacro(true)
                    macroTemplate('${VERSION}')
                    macroFirst(false)
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
            }
        }
    }
}
