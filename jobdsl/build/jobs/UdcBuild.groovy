package jobdsl.build.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcBuild {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam('BRANCH', 'master', '')
            }
            properties {
            		promotions{
              			promotion {
                        name('dev')
                        icon('star-purple')
                        actions {
                            gitHubSetCommitStatusBuilder {
                                statusMessage {
                                    content('promotion is pending')
                                }
                            }
                            shell('gcloud docker -a')
                            envInjectBuilder {
                                propertiesFilePath('version.properties')
                                propertiesContent('')
                            }
                            shell(dslFactory.readFileFromWorkspace(jobConfig.job.versionGeneratorScript))
                            buildNameUpdater {
                                fromFile(false)
                                buildName('${VERSION}')
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
                                goals('-Ddocker.repository=university-course/rc')
                            }
                            gitHubCommitNotifier {
                                resultOnFailure('fail')
                                statusMessage {
                                    content('promotion is done')
                                }
                            }
            		        }
                    }
          	    }
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
                    branch('^(?!origin/${BRANCH}|origin/release-$).*')
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
                      content('build is pending')
                  }
                }
                shell('gcloud docker -a')
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.versionGeneratorScript))
                envInjectBuilder {
                    propertiesFilePath('version.properties')
                    propertiesContent('')
                }
                buildNameUpdater {
                    fromFile(false)
                    buildName('${VERSION}')
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
              gitHubCommitNotifier {
                  resultOnFailure('fail')
                  statusMessage {
                    content('build is done')
                  }
              }
            }
        }
      }
}
