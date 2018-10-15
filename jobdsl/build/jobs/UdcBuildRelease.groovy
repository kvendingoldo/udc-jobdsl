package jobdsl.build.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcBuildRelease {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            properties {
                promotions {
                    promotion {
                        name('create-release-candidate')
                        icon('star-purple')
                        actions {
                            copyArtifacts('${PROMOTED_JOB_NAME}') {
                                includePatterns('variables.txt')
                                buildSelector {
                                    buildNumber('${PROMOTED_NUMBER}')
                                }
                            }
                            downstreamParameterized {
                                trigger('../Utils/UDC_Create_RC') {
                                    block {
                                        buildStepFailure('UNSTABLE')
                                        failure('UNSTABLE')
                                        unstable('UNSTABLE')
                                    }
                                    parameters {
                                        propertiesFile('${WORKSPACE}/variables.txt', true)
                                    }
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
                    branch(':.+release-\\d+.\\d+')
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
                        content('building...')
                    }
                }
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.variablesGeneratorScript))
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.versionGeneratorScript))
                envInjectBuilder {
                    propertiesFilePath('variables.txt')
                    propertiesContent('')
                }
                buildNameUpdater {
                    fromFile(false)
                    buildName('${VERSION}')
                    fromMacro(true)
                    macroTemplate('${VERSION}')
                    macroFirst(false)
                }
                shell('gcloud docker -a')
                maven {
                    goals('clean deploy')
                    goals('-B')
                    goals('-C')
                    goals('-q')
                    goals(' -Pimage')
                    goals('-Ddocker.registry.host=gcr.io')
                    goals('-Ddocker.repository=university-course/udc/dev/${RELEASE_FAMILY}')
                    injectBuildVariables(false)
                }
            }
            publishers {
                gitHubCommitNotifier {
                    resultOnFailure('fail')
                    statusMessage {
                        content('build is completed')
                    }
                }

                archiveArtifacts {
                    exclude('')
                    pattern('variables.txt')
                }
            }
        }
    }
}
